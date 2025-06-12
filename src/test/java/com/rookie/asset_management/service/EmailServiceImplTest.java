package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rookie.asset_management.config.app.AppPropertiesConfig;
import com.rookie.asset_management.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

  @Mock private JavaMailSender emailSender;

  @InjectMocks private EmailServiceImpl emailService;

  private MockedStatic<AppPropertiesConfig> appPropertiesConfigMock;

  @BeforeEach
  void setUp() {
    // Mock static AppPropertiesConfig
    appPropertiesConfigMock = mockStatic(AppPropertiesConfig.class);
    appPropertiesConfigMock.when(AppPropertiesConfig::getUiUrl).thenReturn("http://test-ui.com");
  }

  @AfterEach
  void tearDown() {
    // Close the static mock
    if (appPropertiesConfigMock != null) {
      appPropertiesConfigMock.close();
    }
  }

  @Test
  void sendSimpleMessage_successOnFirstAttempt() {
    String to = "test@example.com";
    String subject = "Test Subject";
    String content = "Test Content";

    boolean result = emailService.sendSimpleMessage(to, subject, content);

    assertTrue(result);
    verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
  }

  @Test
  void sendSimpleMessage_successOnSecondAttempt() {
    String to = "test@example.com";
    String subject = "Test Subject";
    String content = "Test Content";

    doThrow(new RuntimeException("Failed"))
        .doNothing()
        .when(emailSender)
        .send(any(SimpleMailMessage.class));

    boolean result = emailService.sendSimpleMessage(to, subject, content);

    assertTrue(result);
    verify(emailSender, times(2)).send(any(SimpleMailMessage.class));
  }

  @Test
  void sendSimpleMessage_failsAfterThreeAttempts() {
    String to = "test@example.com";
    String subject = "Test Subject";
    String content = "Test Content";

    doThrow(new RuntimeException("Failed")).when(emailSender).send(any(SimpleMailMessage.class));

    boolean result = emailService.sendSimpleMessage(to, subject, content);

    assertFalse(result);
    verify(emailSender, times(3)).send(any(SimpleMailMessage.class));
  }

  @Test
  void generateEmailTemplate_correctFormat() {
    String fullName = "John Doe";
    String username = "johndoe";
    String password = "secret123";

    String expected =
        """
                Hi John Doe,

                Your account has been created. Please find your login details below:

                Username: johndoe
                Password: secret123

                Login here: http://test-ui.com/login

                Please change your password after your first login.

                Thanks,
                Admin Team""";

    String result = EmailServiceImpl.generateEmailTemplate(fullName, username, password);

    assertEquals(expected, result);
  }

  @Test
  void generateEmailTemplate_withEmptyInputs() {
    String fullName = "";
    String username = "";
    String password = "";

    String expected =
        """
                Hi ,

                Your account has been created. Please find your login details below:

                Username:\s
                Password:\s

                Login here: http://test-ui.com/login

                Please change your password after your first login.

                Thanks,
                Admin Team""";

    String result = EmailServiceImpl.generateEmailTemplate(fullName, username, password);

    assertEquals(expected, result);
  }
}
