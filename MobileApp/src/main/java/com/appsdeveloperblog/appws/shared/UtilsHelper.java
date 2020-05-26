package com.appsdeveloperblog.appws.shared;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class UtilsHelper {
	
	private final Random RANDOM = new SecureRandom();
	
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	public String generateUserId(int length)
	{
		return generateRandomString(length);
	}
	
	private String generateRandomString(int length)
	{
		StringBuilder str = new StringBuilder(length);
		
		for(int i = 0; i < length; i++)
		{
			str.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		
		return new String(str);
	}

}
