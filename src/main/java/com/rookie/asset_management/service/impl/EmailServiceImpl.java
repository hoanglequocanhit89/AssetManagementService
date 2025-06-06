package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.config.app.AppPropertiesConfig;
import com.rookie.asset_management.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailServiceImpl implements EmailService {
  final JavaMailSender emailSender;

  @Override
  public boolean sendSimpleMessage(String to, String subject, String content) {
    int maxAttempts = 3;
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
      try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        emailSender.send(message);
        log.info("Email sent to {} on attempt {}", to, attempt);
        return true;
      } catch (Exception ex) {
        log.warn(
            "Failed to send email to {} on attempt {}. Reason: {}", to, attempt, ex.getMessage());
      }
    }
    log.error("Failed to send email to {} after 3 attempts", to);
    return false;
  }

  public static String generateEmailTemplate(String fullName, String username, String password) {
    String url = AppPropertiesConfig.getUiUrl();
    return "Hi "
        + fullName
        + ",\n\n"
        + "Your account has been created. Please find your login details below:\n\n"
        + "Username: "
        + username
        + "\n"
        + "Password: "
        + password
        + "\n\n"
        + "Login here: "
        + url
        + "/login\n\n"
        + "Please change your password after your first login.\n\n"
        + "Thanks,\n"
        + "Admin Team";
  }
}
