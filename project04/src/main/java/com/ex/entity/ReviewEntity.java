package com.ex.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class ReviewEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
    
	@ManyToOne
	private ProductEntity product;
    
	@ManyToOne
	private UserEntity writer;
	
	private LocalDateTime writeDate;
	
	private int adopted;
    
	@ManyToMany
	private Set<UserEntity> voter;
	
	private String content;
	
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ReviewImgEntity> reviewImages; // 리뷰 이미지 리스트 추가

	
}
