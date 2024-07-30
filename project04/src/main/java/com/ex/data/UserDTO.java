package com.ex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.ex.entity.BasketEntity;
import com.ex.entity.OrderlistEntity;
import com.ex.entity.ProductEntity;
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

    private String realName;

    private String adress;
    
    private String phone;
    
    private LocalDateTime reg;
    
    private int grade;
    
    private long point;
    
    private List<BasketEntity> basketList; //해당회원의 장바구니
    
    private List<ProductEntity> wishList; //찜목록
    
    private List<OrderlistEntity> orderlist; //주문내역, 결제
    
    public static UserDTO entityToDTO(UserEntity ue) {
		return UserDTO.builder().id(ue.getId())
				.username(ue.getUsername())
				.email(ue.getEmail())
				.realName(ue.getRealName())
				.adress(ue.getAdress())
				.phone(ue.getPhone())
				.reg(ue.getReg())
				.point(ue.getPoint())
				.basketList(ue.getBasketList())
				.wishList(ue.getWishList())
				.grade(ue.getGrade())
				.build();
	}


}
