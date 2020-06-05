package com.appsdeveloperblog.appws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.appsdeveloperblog.appws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	
	UserDto createUser(UserDto user);
	
	UserDto getUser(String email);
	
	UserDto getUserByUserId(String id);
}
