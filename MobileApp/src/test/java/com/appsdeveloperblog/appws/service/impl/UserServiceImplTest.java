package com.appsdeveloperblog.appws.service.impl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.appws.io.entity.UserEntity;
import com.appsdeveloperblog.appws.repository.PasswordResetTokenRepository;
import com.appsdeveloperblog.appws.repository.UserRepository;
import com.appsdeveloperblog.appws.shared.UtilsHelper;
import com.appsdeveloperblog.appws.shared.dto.UserDto;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepo;
	
	@Mock
	UtilsHelper utils;
	
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//@Mock
	//PasswordResetTokenRepository passwordResetTokenRepo;
	
	String userId = "abc123def456";
	
	String password = "123abc456xyz";
	
	UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstname("arron");
		userEntity.setLastname("healy");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(password);
	}

	@Test
	void testGetUser() {
		
		when(userRepo.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = userService.getUser("test@mail.com");
		
		assertNotNull(userDto);
		assertEquals("arron", userDto.getFirstname());
	}
	
	@Test
	void testGetUser_UsernameNotFoundException() {
		
		when(userRepo.findByEmail(anyString())).thenReturn(null);
		
		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getUser("test@mail.com");
		});
	}
	
	@Test
	void testCreateUser() {
		
		when(userRepo.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("xyz123abc");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(password);
		when(userRepo.save(any(UserEntity.class))).thenReturn(userEntity);
	}

}
