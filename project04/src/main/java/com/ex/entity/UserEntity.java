package com.ex.entity; 

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS") // 오라클 데이터베이스의 USERS 테이블과 매핑
public class UserEntity {
   
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1) // 시퀀스를 사용한 ID 생성
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username; // 사용자 ID (중복 불가)

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false, unique = true)
    private String email; // 이메일 주소

    private String realName; //사용자이름

    private String adress;
    
    private String phone;
    
    private LocalDateTime reg;
    
    private int grade;
    
    private long point;
    
    @OneToMany(mappedBy = "user", cascade=CascadeType.REMOVE)
    private List<BasketEntity> basketList; //해당회원의 장바구니
    
    @ManyToMany
    private List<ProductEntity> wishList; //찜목록
    
    @OneToMany(mappedBy = "user", cascade=CascadeType.REMOVE)
    private List<DeliveryEntity> delivery; //주문내역, 결제
    
    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<ReviewEntity> review;
}
