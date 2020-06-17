package com.appsdeveloperblog.appws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.appsdeveloperblog.appws.service.AddressesService;
import com.appsdeveloperblog.appws.service.UserService;
import com.appsdeveloperblog.appws.shared.dto.AddressDto;
import com.appsdeveloperblog.appws.shared.dto.UserDto;
import com.appsdeveloperblog.appws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.appws.ui.model.response.AddressesRest;
import com.appsdeveloperblog.appws.ui.model.response.OperationResponseModel;
import com.appsdeveloperblog.appws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.appws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AddressesService addressesService;
	
	
	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {
		UserRest returnVal = new UserRest();

		UserDto user = userService.getUserByUserId(id);

		ModelMapper modelMapper = new ModelMapper();

		returnVal = modelMapper.map(user, UserRest.class);

		return returnVal;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest returnValue = new UserRest();

		ModelMapper modelMapper = new ModelMapper();

		// if(userDetails.getFirstname().isEmpty()) throw new
		// UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();

		ModelMapper modelMapper = new ModelMapper();

		// if(userDetails.getFirstname().isEmpty()) throw new
		// UserServiceException(ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage());

		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto updatedUser = userService.updateUser(id, userDto);
		returnValue = modelMapper.map(updatedUser, UserRest.class);

		return returnValue;
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationResponseModel deleteUser(@PathVariable String id) {

		OperationResponseModel returnVal = new OperationResponseModel();

		returnVal.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(id);

		returnVal.setOperationResult(RequestOperationStatus.SUCCESS.name());

		return returnVal;
	}

	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int pageNum,
			@RequestParam(value = "limit", defaultValue = "25") int limitNum) {
		List<UserRest> returnVal = new ArrayList<>();
		
		List<UserDto> users = userService.getUsers(pageNum, limitNum);
		
		for(UserDto user: users)
		{
			UserRest model = new UserRest();
			BeanUtils.copyProperties(user, model);
			returnVal.add(model);
		}

		return returnVal;
	}
	
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id)
	{
		List<AddressesRest> returnVal = new ArrayList<>();
		
		List<AddressDto> addresses = addressesService.getAddresses(id);
		
		if(!addresses.isEmpty() || addresses != null)
		{
			Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			
			returnVal = new ModelMapper().map(addresses, listType);
			
			for(AddressesRest addressRest: returnVal)
			{
				Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
				addressRest.add(selfLink);
			}
		}
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
		
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id)).withSelfRel();
		
		return CollectionModel.of(returnVal, userLink, selfLink); 
	}
	
	@GetMapping(path = "/{id}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public EntityModel<AddressesRest> getUserAddress(@PathVariable String id, @PathVariable String addressId)
	{	
		AddressDto address = addressesService.getAddress(addressId);
		
		ModelMapper modelMapper = new ModelMapper();
		
		AddressesRest returnVal = modelMapper.map(address, AddressesRest.class); 
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
		
		Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id)).withRel("addresses");
		
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id, addressId)).withSelfRel();
		
		return EntityModel.of(returnVal, Arrays.asList(userLink, userAddressesLink, selfLink));
	}
}
