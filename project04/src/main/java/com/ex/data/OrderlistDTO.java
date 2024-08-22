package com.ex.data;

import java.time.LocalDateTime;

import com.ex.entity.DeliveryEntity;
import com.ex.entity.OrderlistEntity;
import com.ex.entity.ProductEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderlistDTO {

    private Long id; 

    private Integer quantity; //수량

    private ProductEntity product; //상품ID
    
    private DeliveryEntity delivery; //배송상태ID
    
    private LocalDateTime orderTime; //주문일시
    
    private double discount;//할인률    
    
    private String tid; // 카카오 결제 고유 번호
    
	private String situation; //현재상태
	
	public static OrderlistDTO entityToDTO(OrderlistEntity oe) {
		return OrderlistDTO.builder().id(oe.getId())
															.quantity(oe.getQuantity())
															.product(oe.getProduct())
															.delivery(oe.getDelivery())
															.orderTime(oe.getOrderTime())
															.discount(oe.getDiscount())
															.tid(oe.getTid())
															.situation(oe.getSituation())
															.build();
	}
	
}
