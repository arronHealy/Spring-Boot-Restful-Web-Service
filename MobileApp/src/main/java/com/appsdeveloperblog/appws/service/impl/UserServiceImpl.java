package com.appsdeveloperblog.appws.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.appws.io.entity.UserEntity;
import com.appsdeveloperblog.appws.repository.UserRepository;
import com.appsdeveloperblog.appws.service.UserService;
import com.appsdeveloperblog.appws.shared.UtilsHelper;
import com.appsdeveloperblog.appws.shared.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UtilsHelper utils;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {
		
		if(userRepo.findByEmail(user.getEmail()) != null) throw new RuntimeException("User already exists!");
		
		UserEntity userEntity = new UserEntity();
		
		BeanUtils.copyProperties(user, userEntity);
		
		String publicUserId = utils.generateUserId(30);
		
		userEntity.setUserId(publicUserId);
		
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		UserEntity storedUserDetails = userRepo.save(userEntity);
		
		UserDto returnVal = new UserDto();
		
		BeanUtils.copyProperties(storedUserDetails, returnVal);
		
		return returnVal;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = userRepo.findByEmail(email);
		
		if(user == null) throw new UsernameNotFoundException("A user with " + email + " could not be found!");
		
		return new User(user.getEmail(), user.getEncryptedPassword(), new ArrayList<>());
	}

}
