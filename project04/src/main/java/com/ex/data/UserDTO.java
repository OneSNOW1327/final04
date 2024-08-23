package com.ex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.ex.entity.BasketEntity;
import com.ex.entity.DeliveryEntity;
import com.ex.entity.ProductEntity;
import com.ex.entity.QuestionEntity;
import com.ex.entity.ReviewEntity;
import com.ex.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Integer id;

    private String username; // 사용자 이름 (중복 불가)

    private String password; // 비밀번호

    private String email; // 이메일 주소

    private String realName; //사용자 이름

    private String postcode; // 우편번호
    private String address; //기본주소
    private String detailAddress; //상세주소
    private String extraAddress; //참고항목
    
    private String phone;
    
    private LocalDateTime reg;
    
    private int grade;
    
    private long point;

    private List<QuestionEntity>questionList; //해당회원의 질문 리스트
    
    private List<BasketEntity> basketList; //해당회원의 장바구니
    
    private List<ProductEntity> wishList; //찜목록
    
    private List<ReviewEntity> review;

    private List<DeliveryEntity> delivery; //주문내역, 결제
    
    public static UserDTO entityToDTO(UserEntity ue) {
		return UserDTO.builder().id(ue.getId())
				.username(ue.getUsername())
				.email(ue.getEmail())
				.realName(ue.getRealName())
				.postcode(ue.getPostcode())
				.address(ue.getAddress())
				.detailAddress(ue.getDetailAddress())
				.extraAddress(ue.getExtraAddress())
				.phone(ue.getPhone())
				.reg(ue.getReg())
				.grade(ue.getGrade())
				.point(ue.getPoint())
				.basketList(ue.getBasketList())
				.review(ue.getReview())
				.delivery(ue.getDelivery())
				.questionList(ue.getQuestionList())
				.wishList(ue.getWishList())
				.build();
	}
    

}
