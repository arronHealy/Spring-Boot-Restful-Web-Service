package com.appsdeveloperblog.appws.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.appws.exceptions.UserServiceException;
import com.appsdeveloperblog.appws.io.entity.PasswordResetTokenEntity;
import com.appsdeveloperblog.appws.io.entity.UserEntity;
import com.appsdeveloperblog.appws.repository.PasswordResetTokenRepository;
import com.appsdeveloperblog.appws.repository.UserRepository;
import com.appsdeveloperblog.appws.service.UserService;
import com.appsdeveloperblog.appws.shared.AmazonSES;
import com.appsdeveloperblog.appws.shared.UtilsHelper;
import com.appsdeveloperblog.appws.shared.dto.AddressDto;
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
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepo;
	
	@Autowired
	private AmazonSES amazonSES;

	@Override
	public UserDto createUser(UserDto user) {
		
		if(userRepo.findByEmail(user.getEmail()) != null) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		for(int i = 0; i < user.getAddresses().size(); i++)
		{
			AddressDto addressDto = user.getAddresses().get(i);
			addressDto.setUserDetails(user);
			addressDto.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, addressDto);
		}
		
		//BeanUtils.copyProperties(user, userEntity);
		ModelMapper modelMapper = new ModelMapper();
		
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		
		String publicUserId = utils.generateUserId(30);
		
		userEntity.setUserId(publicUserId);
		
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		
		userEntity.setEmailVerificationStatus(false);
		
		UserEntity storedUserDetails = userRepo.save(userEntity);
		
		//BeanUtils.copyProperties(storedUserDetails, returnVal);
		
		UserDto returnVal = modelMapper.map(storedUserDetails, UserDto.class);
		
		//send email
		amazonSES.verifyEmail(returnVal);		
		
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
			
		return new User(user.getEmail(), user.getEncryptedPassword(), user.getEmailVerificationStatus(), true, true, true, new ArrayList<>());
		// return new User(user.getEmail(), user.getEncryptedPassword(), new ArrayList<>());
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

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		
		List<UserDto> returnVal = new ArrayList<>();
		
		Pageable pageableReq = PageRequest.of(page, limit);
		
		Page<UserEntity> userPage = userRepo.findAll(pageableReq);
		
		if(userPage.isEmpty()) throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		List<UserEntity> users = userPage.getContent();
		
		for(UserEntity entity: users) {
			UserDto user = new UserDto();
			BeanUtils.copyProperties(entity, user);
			returnVal.add(user);
		}
		
		return returnVal;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnVal = false;
		
		UserEntity userEntity = userRepo.findUserByEmailVerificationToken(token);
		
		if(userEntity != null)
		{
			boolean tokenExp = UtilsHelper.hasTokenExpired(token);
			
			if(!tokenExp)
			{
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepo.save(userEntity);
				returnVal = true;
			}
		}
		
		return returnVal;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		
		boolean returnVal = false;
		
		UserEntity user = userRepo.findByEmail(email);
		
		if(user == null)
		{
			return returnVal;
		}
		
		String token = new UtilsHelper().generatePasswordResetToken(user.getUserId());
		
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		
		passwordResetTokenEntity.setToken(token);
		
		passwordResetTokenEntity.setUserDetails(user);
		
		passwordResetTokenRepo.save(passwordResetTokenEntity);
		
		returnVal = new AmazonSES().sendPasswordResetRequest(user.getFirstname(), user.getEmail(), token);
		
		return returnVal;
	}

	@Override
	public boolean passwordReset(String token, String password) {
		
		boolean returnVal = false;
		
		if(UtilsHelper.hasTokenExpired(token))
		{
			return returnVal;
		}
		
		PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepo.findByToken(token);
		
		if(passwordResetTokenEntity == null)
		{
			return returnVal;
		}
		
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		
		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		
		userEntity.setEncryptedPassword(encodedPassword);
		
		UserEntity savedUser = userRepo.save(userEntity);
		
		if(savedUser != null && savedUser.getEncryptedPassword().equalsIgnoreCase(encodedPassword))
		{
			returnVal = true;
		}
		
		passwordResetTokenRepo.delete(passwordResetTokenEntity);
		
		return returnVal;
	}

}
