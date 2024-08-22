package com.ex.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ex.entity.BasketEntity;
import com.ex.entity.DeliveryEntity;
import com.ex.entity.ProductEntity;
import com.ex.entity.ProductImgEntity;
import com.ex.entity.ProductThumbnailEntity;
import com.ex.entity.ProducttypeEntity;
import com.ex.entity.ReviewEntity;
import com.ex.entity.SalesVolumeEntity;
import com.ex.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    
    private Integer id;
    private ProducttypeEntity type; // 상품 타입
    
    private List<ReviewEntity> review; // 리뷰
    
    private List<ProductImgEntity> detail; // 상품 이미지
    
    private List<ProductThumbnailEntity> thumbnail; // 썸네일
    
    private List<DeliveryEntity> delivery; // 주문 내역
    
    private List<BasketEntity> basketlist; // 장바구니
    
    private Set<UserEntity> wishUser; // 유저의 위시리스트

    private List<SalesVolumeEntity> salesVolume; // 판매량 ID

    private String name; // 상품명

    private String description; // 상품 설명

    private double discount; // 할인률

    private int buyPrice; // 매입가

    private int sellPrice; // 판매가

    private int stock; // 잔량

    private LocalDateTime registrationDate; // 상품 등록 일자

    private String orderEmail; // 주문 메일

    // 추가된 필드들
    private int purchasePrice; // 구매 금액

    private int typeId; // 상품 유형 ID
    
    private List<PhotoDTO> thumbnailPaths; // 썸네일 이미지 경로
    private List<PhotoDTO> descriptionImagePaths; // 설명 이미지 경로
    
    public static ProductDTO entityToDTO(ProductEntity pe) {
		return  ProductDTO.builder()
				.id(pe.getId())
				.type(pe.getType())
				.name(pe.getName())
				.description(pe.getDescription())
				.discount(pe.getDiscount())
				.buyPrice(pe.getBuyPrice())
				.sellPrice(pe.getSellPrice())
				.stock(pe.getStock())
				.review(pe.getReview())
				.registrationDate(pe.getRegistrationDate())
				.orderEmail(pe.getOrderEmail())
				.salesVolume(pe.getSalesVolume())
				.wishUser(pe.getWishUser())
				.build();
    }


}
