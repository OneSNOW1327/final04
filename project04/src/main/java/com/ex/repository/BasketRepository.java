package com.ex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.BasketEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BasketRepository extends JpaRepository<BasketEntity, Integer> {
	
	Optional<BasketEntity> findByUserIdAndProductId(Integer userId, Integer productId);
	List<BasketEntity> findAllByUserIdOrderByIdDesc(Integer userId);
	// 사용자 ID로 장바구니에 있는 아이템 수를 계산하는 메서드
	  @Query("SELECT SUM(b.quantity) FROM BasketEntity b WHERE b.user.id = :userId")
	    Integer countBasketItemsByUserId(@Param("userId") Integer userId);
}
