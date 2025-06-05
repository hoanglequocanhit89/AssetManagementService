package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GmailServiceImpl implements MailService {

  JavaMailSender mailSender;

  TemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  @NonFinal
  String sender;

  @Override
  @Async
  public void sendMail(String receiver, String subject, String content) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

    try {
      helper.setTo(receiver);
      helper.setSubject(subject);
      //      String htmlContent = templateEngine.process(template, context);
      helper.setText("hihi", false);
      mailSender.send(mimeMessage);
      log.info("Email sent successfully to {}", receiver);
    } catch (MessagingException e) {
      log.error("Failed to send email!");
      log.error(e.getMessage());
    }
  }
}
