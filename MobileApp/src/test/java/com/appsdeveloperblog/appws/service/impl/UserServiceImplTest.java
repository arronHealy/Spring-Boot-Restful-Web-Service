package com.appsdeveloperblog.appws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.appsdeveloperblog.appws.io.entity.UserEntity;
import com.appsdeveloperblog.appws.repository.UserRepository;
import com.appsdeveloperblog.appws.shared.dto.UserDto;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepo;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testGetUser() {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstname("arron");
		userEntity.setLastname("healy");
		userEntity.setUserId("abc123def456");
		userEntity.setEncryptedPassword("123abc456xyz");
		
		when(userRepo.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = userService.getUser("test@mail.com");
		
		assertNotNull(userDto);
		assertEquals("arron", userDto.getFirstname());
	}

}
