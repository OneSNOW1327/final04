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
    private ProducttypeEntity type;

    @OneToMany(mappedBy = "product", cascade=CascadeType.REMOVE)
    private List<ReviewEntity> review;

    @OneToMany(mappedBy = "product", cascade=CascadeType.REMOVE)
    private List<ProductImgEntity> detail;

    @OneToMany(mappedBy = "product", cascade=CascadeType.REMOVE)
    private List<ProductThumbnailEntity> thumbnail;

    @ManyToMany
    private List<OrderlistEntity> orderlist;

    @ManyToOne
    private SalesVolumeEntity salesVolume;

    private String name;

    private String explanation;

    private double discount;

    private int BuyPrice;

    private int SellPrice;

    private int stock;

    private LocalDateTime registrationDate;

    private String orderEmail;
}
