package com.ex.repository;

import com.ex.entity.UserEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	Optional<UserEntity>  findByUsername(String username);

	Optional<UserEntity> findByRealNameAndEmailAndPhone(String realName, String email, String phone);

	Optional<UserEntity> findByUsernameAndRealNameAndEmail(String username, String realName, String email);

	Optional<UserEntity>findByEmail(String email);

	@Query("SELECT u FROM UserEntity u JOIN u.wishList p WHERE p.id = :productId")
	List<UserEntity> findByWishListId(@Param("productId") Integer productId);

}
