package com.appsdeveloperblog.appws.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsHelperTest {
	
	@Autowired
	UtilsHelper utils;

	@BeforeEach
	void setUp() throws Exception {
		
	}

	@Test
	void testGenerateUserId() {
		String userId = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);
		
		assertNotNull(userId);
		assertNotNull(userId2);
		assertTrue(userId.length() == 30);
		assertTrue(!userId.equalsIgnoreCase(userId2));
	}

	@Test
	void testHasTokenNotExpired() {
		String token = utils.generateEmailVerificationToken("abc123xyz");
		
		assertNotNull(token);
		
		boolean hasTokenExpired = UtilsHelper.hasTokenExpired(token);
		
		assertFalse(hasTokenExpired);
	}
	
	/*
	@Test
	void testHasTokenExpired() {
		String tokenExpired = "hard code expired token";
		
		boolean hasTokenExpired = UtilsHelper.hasTokenExpired(tokenExpired);
		
		assertTrue(hasTokenExpired);
	}
	*/

}
