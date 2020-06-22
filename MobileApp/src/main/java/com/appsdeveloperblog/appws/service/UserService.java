package com.appsdeveloperblog.appws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.appsdeveloperblog.appws.shared.dto.AddressDto;
import com.appsdeveloperblog.appws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	
	UserDto createUser(UserDto user);
	
	UserDto getUser(String email);
	
	UserDto getUserByUserId(String id);
	
	UserDto updateUser(String id, UserDto user);
	
	List<UserDto> getUsers(int page, int limit);
	
	void deleteUser(String id);
	
	boolean verifyEmailToken(String token);
	
	boolean requestPasswordReset(String email);
}
