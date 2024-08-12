package com.ex.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ProductEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private ProducttypeEntity type; //상품타입

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
	@JsonIgnore // 직렬화에서 제외
	@OrderBy("adopted DESC, writeDate DESC")
    private List<ReviewEntity> review; // 리뷰

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
	@JsonIgnore // 직렬화에서 제외
    private List<ProductImgEntity> detail; // 상품이미지

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
	@JsonIgnore // 직렬화에서 제외
    private List<ProductThumbnailEntity> thumbnail; //썸네일
    
    @ManyToMany
	@JsonIgnore // 직렬화에서 제외
    private List<DeliveryEntity> delivery;

    @ManyToMany
	@JsonIgnore // 직렬화에서 제외
    private List<BasketEntity> basketlist; // 장바구니

    @ManyToMany
	@JsonIgnore // 직렬화에서 제외
    private List<UserEntity> wishUser; //유저의 위시리스트
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
	@JsonIgnore // 직렬화에서 제외
	@OrderBy("recordDate ASC")
    private List<SalesVolumeEntity> salesVolume; //판매량ID

    private String name; //상품명

    private String description; //상품설명

    private double discount; // 할인률

    private int buyPrice; // 구매가

    private int sellPrice; //판매가

    private int stock; //잔량

    private LocalDateTime registrationDate; //상품등록일자

    private String orderEmail; //주문메일
}
