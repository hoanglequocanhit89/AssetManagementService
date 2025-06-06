package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.UserRequestDTO;
import com.rookie.asset_management.dto.request.user.UpdateUserRequest;
import com.rookie.asset_management.dto.request.user.UserFilterRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.user.CreateUserDtoResponse;
import com.rookie.asset_management.dto.response.user.UserBriefDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.UserMapper;
import com.rookie.asset_management.repository.RoleRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.EmailService;
import com.rookie.asset_management.service.UserService;
import com.rookie.asset_management.service.abstraction.PagingServiceImpl;
import com.rookie.asset_management.service.specification.UserSpecification;
import com.rookie.asset_management.util.SecurityUtils;
import com.rookie.asset_management.util.SpecificationBuilder;
import jakarta.transaction.Transactional;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserService} interface, providing functionality related to user
 * management within the asset management system.
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl extends PagingServiceImpl<UserDtoResponse, User, Integer>
    implements UserService {
  UserRepository userRepository;
  RoleRepository roleRepository;
  UserMapper userMapper;
  PasswordEncoder passwordEncoder;
  EmailService emailService;

  // Autowired constructor for paging service implementation
  @Autowired
  public UserServiceImpl(
      UserRepository userRepository,
      UserMapper userMapper,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      EmailService emailService) {
    super(userMapper, userRepository);
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
  }

  @Transactional
  @Override
  public PagingDtoResponse<UserDtoResponse> getAllUsers(
      UserFilterRequest userFilterRequest, int page, int size, String sortBy, String sortDir) {
    // Get authenticated user from security context
    User admin = SecurityUtils.getCurrentUser();
    // check if the sortBy is sort by firstName or lastName
    // must set to related.property because of the join
    if (sortBy != null && sortBy.equals("firstName")) {
      sortBy = "userProfile.firstName";
    }
    if (sortBy != null && sortBy.equals("lastName")) {
      sortBy = "userProfile.lastName";
    }
    // Create a pageable object for pagination and sorting with default values
    Pageable pageable = createPageable(page, size, sortDir, sortBy);
    // destructure the filter request
    String query = userFilterRequest.getQuery();
    String type = userFilterRequest.getType();
    // Create a specification based on the filter request
    Specification<User> spec =
        new SpecificationBuilder<User>()
            .addIfNotNull(query, UserSpecification.hasNameOrCodeLike(query))
            .addIfNotNull(type, UserSpecification.hasType(type))
            .addIfNotNull(admin.getId(), UserSpecification.hasSameLocationAs(admin.getId()))
            .addIfNotNull(admin.getId(), UserSpecification.excludeAdmin(admin.getId()))
            .add(UserSpecification.excludeDisabled())
            .build();
    return getMany(spec, pageable);
  }

  @Override
  public UserDetailDtoResponse getUserDetails(int userId) {
    User user =
        userRepository
            .findByIdAndDisabledFalse(userId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ("User not found")));

    return userMapper.toUserDetailsDto(user);
  }

  @Transactional
  @Override
  public CreateUserDtoResponse createUser(UserRequestDTO request) {
    // Get the user from JWT token
    User admin = SecurityUtils.getCurrentUser();
    if (!"ADMIN".equalsIgnoreCase(admin.getRole().getName())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only admins can create users");
    }

    // Check if email already exists
    if (userRepository.existsByEmailAndDisabledFalse(request.getEmail())) {
      throw new AppException(HttpStatus.CONFLICT, "Email already exists");
    }

    // Map DTO to Entity
    User user = userMapper.toEntity(request);
    user.getUserProfile().setUser(user);

    boolean isAdmin = "Admin".equalsIgnoreCase(request.getType());
    if (isAdmin) {
      if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
        throw new AppException(
            HttpStatus.BAD_REQUEST, "Location is required when creating an Admin account");
      }
    } else {
      user.setLocation(admin.getLocation());
    }

    Role role = new Role();
    role.setId(isAdmin ? 1 : 2);
    role.setName(isAdmin ? "ADMIN" : "STAFF");
    user.setRole(role);

    // Set audit fields
    user.setCreatedBy(admin);
    user.setUpdatedBy(admin);

    // Set default values
    user.setDisabled(false);
    user.setFirstLogin(true);

    // Generate username
    String username = generateUsername(request.getFirstName(), request.getLastName());
    if (userRepository.existsByUsername(username)) {
      throw new AppException(HttpStatus.CONFLICT, "Username already exists");
    }

    String password = generatePassword(username, user.getUserProfile().getDob());
    // Bcrypt password
    String hashedPassword =
        passwordEncoder.encode(password);

    user.setUsername(username);
    user.setStaffCode("SDTEMP");
    user.setPassword(hashedPassword);
    // Save user to persist and generate staffCode
    user = userRepository.save(user);

    // Send email
    String content = EmailServiceImpl.generateEmailTemplate(user.getUserProfile().getFullName(), username, password);
    boolean isSentEmail = emailService.sendSimpleMessage(user.getEmail(), "Your account has been created", content);

    var createdUser = userMapper.toUserDetailsDto(user);
    var createdUserResponse = userMapper.toCreateUserDtoResponse(createdUser);
    createdUserResponse.setSentEmail(isSentEmail);
    return createdUserResponse;
  }

  private String generateUsername(String firstName, String lastName) {
    String[] lastNameParts = lastName.trim().split("\\s+");
    StringBuilder lastInitials = new StringBuilder();
    for (String part : lastNameParts) {
      if (!part.isEmpty()) {
        lastInitials.append(part.charAt(0));
      }
    }

    String base = (firstName + lastInitials).toLowerCase();

    String normalized = Normalizer.normalize(base, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    String withoutDiacritics = pattern.matcher(normalized).replaceAll("");
    String baseUsername = withoutDiacritics.replaceAll("[^a-z0-9]", "");

    String finalUsername = baseUsername;
    int counter = 1;

    while (userRepository.existsByUsername(finalUsername)) {
      finalUsername = baseUsername + counter;
      counter++;
    }

    return finalUsername;
  }

  public String generatePassword(String username, LocalDate dob) {
    StringBuilder passwordBuilder = new StringBuilder();
    // auto generate password from username and date of birth
    passwordBuilder.append(username);
    passwordBuilder.append("@");
    // format the date of birth to ddMMyyyy
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
    System.out.println("new password:" + passwordBuilder);
    passwordBuilder.append(dob.format(formatter));
    return passwordBuilder.toString();
  }

  @Transactional
  @Override
  public void updateUser(int userId, UpdateUserRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ("User not found")));

    // Mapping non nullable from request to entity
    userMapper.updateUserFromDto(request, user);
    userMapper.updateUserProfileFromDto(request, user.getUserProfile());

    // Update a user role or throw exception if the role not found
    if (request.getRole() != null) {
      Role role = roleRepository.findByName(request.getRole());
      if (role == null) {
        throw new AppException(HttpStatus.NOT_FOUND, "Role not found");
      }
      user.setRole(role);
    }

    // Validate joined date
    if (user.getUserProfile().getDob() != null
        && user.getJoinedDate() != null
        && !user.getJoinedDate().isAfter(user.getUserProfile().getDob())) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Joined date must be after date of birth.");
    }

    userRepository.save(user);
  }

  @Transactional
  @Override
  public void deleteUser(int userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ("User not found")));

    // check if the user is already disabled
    if (Boolean.TRUE.equals(user.getDisabled())) {
      throw new AppException(HttpStatus.CONFLICT, "User is already disabled");
    }

    // check if the user has any assignments
    List<Assignment> assignments = user.getAssignments();
    if (assignments != null && !assignments.isEmpty()) {
      assignments.forEach(
          assignment -> {
            // if the user has any assignments that are waiting, throw exception
            if (isHavePendingAssignment(assignments)) {
              throw new AppException(
                  HttpStatus.CONFLICT,
                  "User has pending assignments, cannot be deleted, please cancel the assignment first");
            }

            // if the user has any assignments that are accepted, check if the user has any
            // returning requests
            // if the user has any returning requests that are not completed, throw exception
            boolean isAccepted = assignment.getStatus() == AssignmentStatus.ACCEPTED;
            if (isAccepted && !isAssigmentReturned(assignment)) {
              throw new AppException(
                  HttpStatus.CONFLICT,
                  "User has pending returning requests, cannot be deleted, please cancel the request first");
            }
          });
    }

    // the user have no assignments, or all assignments are completed (accepted and has been
    // returned)
    // set the user to disabled
    user.setDisabled(true);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public List<UserBriefDtoResponse> getAllUserBrief(String query, String sortBy, String sortDir) {
    // Get the username from JWT token
    User user = SecurityUtils.getCurrentUser();
    // check if the sortBy is sort by firstName or lastName
    // must set to related.property because of the join
    if (sortBy != null && sortBy.equals("firstName")) {
      sortBy = "userProfile.firstName";
    }
    if (sortBy != null && sortBy.equals("lastName")) {
      sortBy = "userProfile.lastName";
    }
    // Create a specification based on the filter request
    Specification<User> spec =
        new SpecificationBuilder<User>()
            .addIfNotNull(query, UserSpecification.hasNameOrCodeLike(query))
            .addIfNotNull(user.getId(), UserSpecification.hasSameLocationAs(user.getId()))
            .addIfNotNull(user.getId(), UserSpecification.excludeAdmin(user.getId()))
            .add(UserSpecification.excludeDisabled())
            .build();

    // Create sorting object
    Sort sort =
        "asc".equalsIgnoreCase(sortDir)
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    // Retrieve users and map to DTOs
    List<User> users = userRepository.findAll(spec, sort);
    return users.stream().map(userMapper::toUserBriefDto).toList();
  }

  private boolean isAssigmentReturned(Assignment assignment) {
    // check if the user has any returning requests
    // if the user has no returning requests, that means the user has not returned the asset yet
    // then throw exception
    ReturningRequest returningRequest = assignment.getReturningRequest();
    if (returningRequest == null) {
      throw new AppException(HttpStatus.CONFLICT, "User has not returned the asset yet");
    }
    return returningRequest.getStatus() == ReturningRequestStatus.COMPLETED;
  }

  private boolean isHavePendingAssignment(List<Assignment> assignments) {
    for (Assignment assignment : assignments) {
      if (assignment.getStatus() == AssignmentStatus.WAITING) {
        return true;
      }
    }
    return false;
  }
}
