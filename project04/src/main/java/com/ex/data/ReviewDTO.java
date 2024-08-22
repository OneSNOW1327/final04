package com.ex.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ex.entity.ProductEntity;
import com.ex.entity.ReviewEntity;
import com.ex.entity.ReviewImgEntity;
import com.ex.entity.UserEntity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
    
	private ProductEntity product;
    
	private UserEntity writer;
	
	private LocalDateTime writeDate;
	
	private int adopted;
    
	private Set<UserEntity> voter;
	
	private String content;
	
	private List<ReviewImgEntity> reviewImages; 
	
	public static ReviewDTO entityToDTO(ReviewEntity re) {
		return ReviewDTO.builder().id(re.getId())
													.product(re.getProduct())
													.writer(re.getWriter())
													.writeDate(re.getWriteDate())
													.adopted(re.getAdopted())
													.voter(re.getVoter())
													.content(re.getContent())
													.reviewImages(re.getReviewImages())
													.build();
	}
	
}
