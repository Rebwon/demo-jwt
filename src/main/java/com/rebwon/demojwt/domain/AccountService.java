package com.rebwon.demojwt.domain;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
	UserDetails loadUserById(Long userId);
}
