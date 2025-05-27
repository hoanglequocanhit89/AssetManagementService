package com.rookie.asset_management.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailsService {
  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
