package com.ex.data;

import com.ex.entity.ProductEntity;
import com.ex.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasketDTO {
	
	private Integer id;
	
	private ProductEntity product;
   
	private UserEntity user;
	
	private int quantity;

}
