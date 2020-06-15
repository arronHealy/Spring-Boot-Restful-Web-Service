package com.appsdeveloperblog.appws.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.appsdeveloperblog.appws.io.entity.AddressEntity;
import com.appsdeveloperblog.appws.io.entity.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
	
	List<AddressEntity> findAllByUserDetails(UserEntity entity);
	
	AddressEntity findByAddressId(String id);

}
