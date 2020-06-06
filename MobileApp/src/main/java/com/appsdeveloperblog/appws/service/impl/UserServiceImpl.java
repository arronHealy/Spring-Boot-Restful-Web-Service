package com.appsdeveloperblog.appws.service.impl;

import java.util.ArrayList;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.appws.exceptions.UserServiceException;
import com.appsdeveloperblog.appws.io.entity.UserEntity;
import com.appsdeveloperblog.appws.repository.UserRepository;
import com.appsdeveloperblog.appws.service.UserService;
import com.appsdeveloperblog.appws.shared.UtilsHelper;
import com.appsdeveloperblog.appws.shared.dto.UserDto;
import com.appsdeveloperblog.appws.ui.model.response.ErrorMessages;

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
		
		if(userRepo.findByEmail(user.getEmail()) != null) throw new RuntimeException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
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
	public UserDto updateUser(String id, UserDto user) {
		
		UserEntity updatedUser = userRepo.findByUserId(id);
		
		if(updatedUser == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		updatedUser.setFirstname(user.getFirstname());
		updatedUser.setLastname(user.getLastname());
		
		UserDto returnVal = new UserDto();
		
		UserEntity updatedUserDetails = userRepo.save(updatedUser);
		
		returnVal = new ModelMapper().map(updatedUserDetails, UserDto.class);
		
		return returnVal;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = userRepo.findByEmail(email);
		
		if(user == null) throw new UsernameNotFoundException(ErrorMessages.EMAIL_ADRESS_NOT_VERIFED.getErrorMessage());
		
		return new User(user.getEmail(), user.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity user = userRepo.findByEmail(email);
		
		if(user == null) throw new UsernameNotFoundException(ErrorMessages.EMAIL_ADRESS_NOT_VERIFED.getErrorMessage());
		
		UserDto returnVal = new UserDto();
		
		BeanUtils.copyProperties(user, returnVal);
		
		return returnVal;
	}

	@Override
	public UserDto getUserByUserId(String id) {
		UserEntity user = userRepo.findByUserId(id);
		
		if(user == null) throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		UserDto returnVal = new UserDto();
		
		BeanUtils.copyProperties(user, returnVal);
		
		return returnVal;
	}

	@Override
	public void deleteUser(String id) {
		UserEntity user = userRepo.findByUserId(id);
		
		if(user == null) throw new UsernameNotFoundException(ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage());
		
		userRepo.delete(user);
	}

}
