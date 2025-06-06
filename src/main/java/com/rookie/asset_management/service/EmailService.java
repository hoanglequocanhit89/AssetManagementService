package com.rookie.asset_management.service;

public interface EmailService {
  boolean sendSimpleMessage(String to, String subject, String content);
}
