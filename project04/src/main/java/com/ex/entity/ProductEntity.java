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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<ReviewEntity> review; // 리뷰

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<ProductImgEntity> detail; // 상품이미지

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<ProductThumbnailEntity> thumbnail; //썸네일

    @ManyToMany
    private List<OrderlistEntity> orderlist; // 주문내역

    @ManyToMany
    private List<BasketEntity> basketlist; // 장바구니

    @ManyToMany
    private List<UserEntity> wishUser; //유저의 위시리스트
    
    @OneToMany
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
