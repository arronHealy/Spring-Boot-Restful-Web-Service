package com.appsdeveloperblog.appws.service.impl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.appws.exceptions.UserServiceException;
import com.appsdeveloperblog.appws.io.entity.AddressEntity;
import com.appsdeveloperblog.appws.io.entity.UserEntity;
import com.appsdeveloperblog.appws.repository.PasswordResetTokenRepository;
import com.appsdeveloperblog.appws.repository.UserRepository;
import com.appsdeveloperblog.appws.shared.AmazonSES;
import com.appsdeveloperblog.appws.shared.UtilsHelper;
import com.appsdeveloperblog.appws.shared.dto.AddressDto;
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
	
	@Mock
	AmazonSES amazonSES;
	
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
		userEntity.setEmail("aaronhealy123@gmail.com");
		userEntity.setEmailVerificationToken("456abc123");
		userEntity.setAddresses(getAddressesEntity());
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
	void testCreateUser_UserServiceException() {
		when(userRepo.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstname("arron");
		userDto.setLastname("healy");
		userDto.setPassword("password");
		userDto.setEmail("aaronhealy123@gmail.com");
		
		assertThrows(UserServiceException.class, () -> {
			userService.createUser(userDto);
		});
	}
	
	@Test
	void testCreateUser() {
		
		when(userRepo.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("xyz123abc");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(password);
		when(userRepo.save(any(UserEntity.class))).thenReturn(userEntity);
		Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstname("arron");
		userDto.setLastname("healy");
		userDto.setPassword("password");
		userDto.setEmail("aaronhealy123@gmail.com");
		
		UserDto storedUserDetails = userService.createUser(userDto);
		
		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstname(), storedUserDetails.getFirstname());
		assertEquals(userEntity.getLastname(), storedUserDetails.getLastname());
		assertNotNull(storedUserDetails.getUserId());		
		assertEquals(userEntity.getAddresses().size(), storedUserDetails.getAddresses().size());
		verify(utils, times(2)).generateAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("password");
		verify(userRepo, times(1)).save(any(UserEntity.class));
	}
	
	private List<AddressDto> getAddressesDto()
	{
		List<AddressDto> addresses = new ArrayList<>();
		
		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");
		addressDto.setCity("Dublin");
		addressDto.setCountry("Ireland");
		addressDto.setPostalCode("xyz123");
		addressDto.setStreetName("123 street");
		
		AddressDto billAddressDto = new AddressDto();
		billAddressDto.setType("billing");
		billAddressDto.setCity("Dublin");
		billAddressDto.setCountry("Ireland");
		billAddressDto.setPostalCode("xyz123");
		billAddressDto.setStreetName("123 street");
		
		addresses.add(addressDto);
		addresses.add(billAddressDto);
		
		return addresses;
	}
	
	private List<AddressEntity> getAddressesEntity(){
		
		List<AddressDto> addresses = getAddressesDto();
		
		Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
		
		return new ModelMapper().map(addresses, listType);
	}
}
