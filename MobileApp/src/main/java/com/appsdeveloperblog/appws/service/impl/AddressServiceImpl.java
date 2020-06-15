package com.appsdeveloperblog.appws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.appws.io.entity.AddressEntity;
import com.appsdeveloperblog.appws.io.entity.UserEntity;
import com.appsdeveloperblog.appws.repository.AddressRepository;
import com.appsdeveloperblog.appws.repository.UserRepository;
import com.appsdeveloperblog.appws.service.AddressesService;
import com.appsdeveloperblog.appws.shared.dto.AddressDto;

@Service
public class AddressServiceImpl implements AddressesService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AddressRepository addressRepo;

	@Override
	public List<AddressDto> getAddresses(String id) {

		List<AddressDto> returnVal = new ArrayList<>();

		UserEntity userEntity = userRepo.findByUserId(id);

		if (userEntity == null)
			return returnVal;

		Iterable<AddressEntity> addresses = addressRepo.findAllByUserDetails(userEntity);

		for (AddressEntity entity : addresses) {
			returnVal.add(new ModelMapper().map(entity, AddressDto.class));
		}

		return returnVal;
	}

	@Override
	public AddressDto getAddress(String addressId) {
		AddressDto returnValue = null;

		AddressEntity addressEntity = addressRepo.findByAddressId(addressId);

		if (addressEntity != null) {
			returnValue = new ModelMapper().map(addressEntity, AddressDto.class);
		}

		return returnValue;
	}

}
