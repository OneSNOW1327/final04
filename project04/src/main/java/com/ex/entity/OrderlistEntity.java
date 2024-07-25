package com.ex.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orderlist")
public class OrderlistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private Integer quantity; //수량

    @ManyToOne
    private UserEntity user; //유저ID

    @ManyToOne
    private ProductEntity product; //상품ID
    
    private LocalDateTime orderTime; //주문일시
    
    private String payOption; //결제방식
    
    private double discount;//할인률
    
    

    
}