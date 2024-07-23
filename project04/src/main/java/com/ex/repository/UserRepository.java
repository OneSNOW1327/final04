package com.ex.repository;

import com.ex.entity.UserEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
	Optional<UserEntity>  findByUsername(String username);
	
    Optional<UserEntity> findByRealNameAndEmailAndPhone(String realName, String email, String phone);

}
