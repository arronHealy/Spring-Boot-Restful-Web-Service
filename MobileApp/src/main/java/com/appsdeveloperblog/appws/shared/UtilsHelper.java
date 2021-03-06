package com.appsdeveloperblog.appws.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.appsdeveloperblog.appws.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UtilsHelper {

	private final Random RANDOM = new SecureRandom();

	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public String generateUserId(int length) {
		return generateRandomString(length);
	}

	public String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder str = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			str.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}

		return new String(str);
	}

	public static boolean hasTokenExpired(String token) {
		
		boolean returnVal = false;
		
		try {
			Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token).getBody();

			Date tokenExpDate = claims.getExpiration();

			Date todayDate = new Date();
			
			returnVal = tokenExpDate.before(todayDate);
		} catch (ExpiredJwtException e) {
			
			returnVal = true;
		}

		return returnVal;
	}

	public String generateEmailVerificationToken(String publicUserId) {
		String token = Jwts.builder().setSubject(publicUserId)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();
		return token;
	}
	
	public String generatePasswordResetToken(String userId)
	{
		String token = Jwts.builder().setSubject(userId)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();
		return token;
	}

}
