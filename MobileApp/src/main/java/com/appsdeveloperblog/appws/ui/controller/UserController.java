package com.appsdeveloperblog.appws.ui.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.appws.service.UserService;
import com.appsdeveloperblog.appws.shared.dto.UserDto;
import com.appsdeveloperblog.appws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.appws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;

	@GetMapping(path="/{id}")
	public UserRest getUser(@PathVariable String id)
	{
		UserRest returnVal = new UserRest();
		
		UserDto user = userService.getUserByUserId(id);
		
		BeanUtils.copyProperties(user, returnVal);
		
		return returnVal;
	}
	
	@PostMapping
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)
	{
		UserRest returnVal = new UserRest();
		
		UserDto userDto = new UserDto();
		
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto createdUser = userService.createUser(userDto);
		
		BeanUtils.copyProperties(createdUser, returnVal);
		
		return returnVal;
	}
	
	@PutMapping
	public String updateUser()
	{
		return "update user called";
	}
	
	@DeleteMapping
	public String deleteUser()
	{
		return "delete user called";
	}
}
