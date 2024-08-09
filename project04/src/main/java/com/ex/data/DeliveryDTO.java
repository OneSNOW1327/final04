package com.ex.data;

import java.util.List;

import com.ex.entity.DeliveryEntity;
import com.ex.entity.OrderlistEntity;
import com.ex.entity.ProductEntity;
import com.ex.entity.UserEntity;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDTO {
	
	private Integer id; //pk

	private List<OrderlistEntity> order; //주문 내역 OrderlistEntity

	private UserEntity user; //회원 UserEntity
	
	private String receiveName; // 받는사람 이름
	
	private String receivePhone; // 받는사람 전화
	
	private String receiveAddress; // 받는사람 주소
	
	private String memo; // 배송메모
	
	private String situation; //현재상태
	
	private String waybill; // 운송장번호
	
	public static DeliveryDTO entityToDTO(DeliveryEntity de) {
		return DeliveryDTO.builder().id(de.getId())
				.order(de.getOrder())
				.receiveName(de.getReceiveName())
				.receivePhone(de.getReceivePhone())
				.receiveAddress(de.getReceiveAddress())
				.memo(de.getMemo())
				.situation(de.getSituation())
				.waybill(de.getWaybill()).build();
	}

}
