package com.rookie.asset_management.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.rookie.asset_management.dto.request.UserRequestDTO;
import com.rookie.asset_management.enums.AssetStatus;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ValidatorTest {

  @Nested
  @DisplayName("AgeOver18Validator Tests")
  class AgeOver18ValidatorTest {

    private AgeOver18Validator validator;

    @BeforeEach
    void setUp() {
      validator = new AgeOver18Validator();
    }

    @Test
    @DisplayName("isValid should return true when age is over 18")
    void isValid_AgeOver18_ReturnsTrue() {
      LocalDate dob = LocalDate.now().minusYears(20);
      assertTrue(validator.isValid(dob, null));
    }

    @Test
    @DisplayName("isValid should return true when age is exactly 18")
    void isValid_AgeExactly18_ReturnsTrue() {
      LocalDate dob = LocalDate.now().minusYears(18);
      assertTrue(validator.isValid(dob, null));
    }

    @Test
    @DisplayName("isValid should return false when age is under 18")
    void isValid_AgeUnder18_ReturnsFalse() {
      LocalDate dob = LocalDate.now().minusYears(17);
      assertFalse(validator.isValid(dob, null));
    }

    @Test
    @DisplayName("isValid should return true when dob is null")
    void isValid_NullDob_ReturnsTrue() {
      assertTrue(validator.isValid(null, null));
    }
  }

  @Nested
  @DisplayName("NotWeekendValidator Tests")
  class NotWeekendValidatorTest {

    private NotWeekendValidator validator;

    @BeforeEach
    void setUp() {
      validator = new NotWeekendValidator();
    }

    @Test
    @DisplayName("isValid should return true for weekday")
    void isValid_Weekday_ReturnsTrue() {
      LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
      assertTrue(validator.isValid(monday, null));
    }

    @Test
    @DisplayName("isValid should return false for Saturday")
    void isValid_Saturday_ReturnsFalse() {
      LocalDate saturday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
      assertFalse(validator.isValid(saturday, null));
    }

    @Test
    @DisplayName("isValid should return false for Sunday")
    void isValid_Sunday_ReturnsFalse() {
      LocalDate sunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
      assertFalse(validator.isValid(sunday, null));
    }

    @Test
    @DisplayName("isValid should return true when date is null")
    void isValid_NullDate_ReturnsTrue() {
      assertTrue(validator.isValid(null, null));
    }

    @Test
    @DisplayName("isValid should return true for Friday")
    void isValid_Friday_ReturnsTrue() {
      LocalDate friday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
      assertTrue(validator.isValid(friday, null));
    }
  }

  @Nested
  @DisplayName("AfterDOBValidator Tests")
  class AfterDOBValidatorTest {

    private AfterDOBValidator validator;

    @BeforeEach
    void setUp() {
      validator = new AfterDOBValidator();
    }

    @Test
    @DisplayName("isValid should return true when joinedDate is after dob")
    void isValid_JoinedDateAfterDob_ReturnsTrue() {
      UserRequestDTO user = new UserRequestDTO();
      user.setDob(LocalDate.of(1990, 1, 1));
      user.setJoinedDate(LocalDate.of(2020, 1, 1));
      assertTrue(validator.isValid(user, null));
    }

    @Test
    @DisplayName("isValid should return false when joinedDate is before dob")
    void isValid_JoinedDateBeforeDob_ReturnsFalse() {
      UserRequestDTO user = new UserRequestDTO();
      user.setDob(LocalDate.of(2020, 1, 1));
      user.setJoinedDate(LocalDate.of(1990, 1, 1));
      assertFalse(validator.isValid(user, null));
    }

    @Test
    @DisplayName("isValid should return false when joinedDate equals dob")
    void isValid_JoinedDateEqualsDob_ReturnsFalse() {
      UserRequestDTO user = new UserRequestDTO();
      user.setDob(LocalDate.of(2000, 1, 1));
      user.setJoinedDate(LocalDate.of(2000, 1, 1));
      assertFalse(validator.isValid(user, null));
    }

    @Test
    @DisplayName("isValid should return true when dob is null")
    void isValid_NullDob_ReturnsTrue() {
      UserRequestDTO user = new UserRequestDTO();
      user.setDob(null);
      user.setJoinedDate(LocalDate.of(2020, 1, 1));
      assertTrue(validator.isValid(user, null));
    }

    @Test
    @DisplayName("isValid should return true when joinedDate is null")
    void isValid_NullJoinedDate_ReturnsTrue() {
      UserRequestDTO user = new UserRequestDTO();
      user.setDob(LocalDate.of(1990, 1, 1));
      user.setJoinedDate(null);
      assertTrue(validator.isValid(user, null));
    }

    @Test
    @DisplayName("isValid should return true when both dates are null")
    void isValid_BothDatesNull_ReturnsTrue() {
      UserRequestDTO user = new UserRequestDTO();
      user.setDob(null);
      user.setJoinedDate(null);
      assertTrue(validator.isValid(user, null));
    }
  }

  @Nested
  @DisplayName("AssetStatusValidator Tests")
  class AssetStatusValidatorTest {

    private AssetStatusValidator validator;

    @BeforeEach
    void setUp() {
      validator = new AssetStatusValidator();
    }

    @Test
    @DisplayName("isValid should return true for AVAILABLE status")
    void isValid_AvailableStatus_ReturnsTrue() {
      assertTrue(validator.isValid(AssetStatus.AVAILABLE, null));
    }

    @Test
    @DisplayName("isValid should return true for NOT_AVAILABLE status")
    void isValid_NotAvailableStatus_ReturnsTrue() {
      assertTrue(validator.isValid(AssetStatus.NOT_AVAILABLE, null));
    }

    @Test
    @DisplayName("isValid should return true for WAITING status")
    void isValid_WaitingStatus_ReturnsTrue() {
      assertTrue(validator.isValid(AssetStatus.WAITING, null));
    }

    @Test
    @DisplayName("isValid should return true for RECYCLED status")
    void isValid_RecycledStatus_ReturnsTrue() {
      assertTrue(validator.isValid(AssetStatus.RECYCLED, null));
    }

    @Test
    @DisplayName("isValid should return false for ASSIGNED status")
    void isValid_AssignedStatus_ReturnsFalse() {
      assertFalse(validator.isValid(AssetStatus.ASSIGNED, null));
    }
  }

  @Nested
  @DisplayName("CreationAssetStatusValidator Tests")
  class CreationAssetStatusValidatorTest {

    private CreationAssetStatusValidator validator;

    @BeforeEach
    void setUp() {
      validator = new CreationAssetStatusValidator();
    }

    @Test
    @DisplayName("isValid should return true for AVAILABLE status")
    void isValid_AvailableStatus_ReturnsTrue() {
      assertTrue(validator.isValid(AssetStatus.AVAILABLE, null));
    }

    @Test
    @DisplayName("isValid should return true for NOT_AVAILABLE status")
    void isValid_NotAvailableStatus_ReturnsTrue() {
      assertTrue(validator.isValid(AssetStatus.NOT_AVAILABLE, null));
    }

    @Test
    @DisplayName("isValid should return false for WAITING status")
    void isValid_WaitingStatus_ReturnsFalse() {
      assertFalse(validator.isValid(AssetStatus.WAITING, null));
    }

    @Test
    @DisplayName("isValid should return false for RECYCLED status")
    void isValid_RecycledStatus_ReturnsFalse() {
      assertFalse(validator.isValid(AssetStatus.RECYCLED, null));
    }

    @Test
    @DisplayName("isValid should return false for ASSIGNED status")
    void isValid_AssignedStatus_ReturnsFalse() {
      assertFalse(validator.isValid(AssetStatus.ASSIGNED, null));
    }
  }
}
