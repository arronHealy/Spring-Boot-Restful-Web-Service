package com.appsdeveloperblog.appws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.appsdeveloperblog.appws.service.impl.UserServiceImpl;
import com.appsdeveloperblog.appws.shared.dto.AddressDto;
import com.appsdeveloperblog.appws.shared.dto.UserDto;
import com.appsdeveloperblog.appws.ui.model.response.UserRest;

class UserControllerTest {
	
	@InjectMocks
	UserController userController;
	
	@Mock
	UserServiceImpl userService;
	
	UserDto userDto;
	
	final String USER_ID = "XYZABC";

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userDto = new UserDto();
		userDto.setFirstname("arron");
		userDto.setLastname("healy");
		userDto.setEmail("arronhealy123@gmail.com");
		userDto.setEmailVerificationStatus(Boolean.TRUE);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressesDto());
		userDto.setEncryptedPassword("abc123");
	}

	@Test
	void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest userRest = userController.getUser(USER_ID);
		
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstname(), userRest.getFirstname());
		assertEquals(userDto.getLastname(), userRest.getLastname());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
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

}
