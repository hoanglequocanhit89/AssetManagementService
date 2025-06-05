package com.rookie.asset_management.service;

public interface MailService {

  void sendMail(String receiver, String subject, String content);
}
