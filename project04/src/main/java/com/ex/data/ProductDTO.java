package com.ex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.ex.entity.BasketEntity;
import com.ex.entity.OrderlistEntity;
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
    private ProducttypeEntity type; //상품타입
    
    private List<ReviewEntity> review; // 리뷰
    
    private List<ProductImgEntity> detail; // 상품이미지
    
    private List<ProductThumbnailEntity> thumbnail; //썸네일
    
    private List<OrderlistEntity> orderlist; // 주문내역
    
    private List<BasketEntity> basketlist; // 장바구니
    
    private List<UserEntity> wishUser; //유저의 위시리스트

    private List<SalesVolumeEntity> salesVolume; //판매량ID

    private String name; //상품명

    private String description; //상품설명

    private double discount; // 할인률

    private int purchasePrice; // 매입가

    private int sellPrice; //판매가

    private int stock; //잔량

    private LocalDateTime registrationDate; //상품등록일자

    private String orderEmail; //주문메일

    // 추가된 필드들
    private int buyPrice; // 구매 금액

    private int typeId; // 상품 유형 ID
    
    private List<PhotoDTO> thumbnailPaths;//사진주소
    private List<PhotoDTO> descriptionImagePaths;//사진주소
}
