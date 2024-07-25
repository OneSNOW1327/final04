package com.ex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.ex.entity.BasketEntity;
import com.ex.entity.OrderlistEntity;
import com.ex.entity.ProductEntity;
import com.ex.entity.ProductImgEntity;
import com.ex.entity.ProductThumbnailEntity;
import com.ex.entity.ProducttypeEntity;
import com.ex.entity.ReviewEntity;
import com.ex.entity.SalesVolumeEntity;
import com.ex.entity.UserEntity;

import jakarta.persistence.ManyToOne;
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
