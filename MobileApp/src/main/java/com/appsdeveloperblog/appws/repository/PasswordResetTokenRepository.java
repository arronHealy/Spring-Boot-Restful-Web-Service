package com.appsdeveloperblog.appws.repository;

import org.springframework.data.repository.CrudRepository;

import com.appsdeveloperblog.appws.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
	
	PasswordResetTokenEntity findByToken(String token);

}