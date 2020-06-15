package com.appsdeveloperblog.appws.service;

import java.util.List;

import com.appsdeveloperblog.appws.shared.dto.AddressDto;

public interface AddressesService {
	
	List<AddressDto> getAddresses(String id);
	
	AddressDto getAddress(String addressId);

}
