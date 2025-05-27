package com.rookie.asset_management.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Service interface for loading user-specific details during authentication.
 *
 * <p>This interface extends the Spring Security {@link
 * org.springframework.security.core.userdetails.UserDetailsService} to provide custom user
 * retrieval logic based on the username. It is used to authenticate users in the application by
 * fetching their details, including roles and credentials, from the data source.
 */
public interface CustomUserDetailsService {

  /**
   * Retrieves user details for the specified username.
   *
   * <p>This method is called by Spring Security during the authentication process to load a user's
   * details, such as their username, password, and authorities (roles). Implementations should
   * query the data source (e.g., a database) to find the user by their username and return a {@link
   * UserDetails} object containing the user's information.
   *
   * @param username the username identifying the user whose details are to be retrieved
   * @return a {@link UserDetails} object containing the user's details, including username,
   *     password, and authorities
   * @throws UsernameNotFoundException if no user is found with the specified username
   */
  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
