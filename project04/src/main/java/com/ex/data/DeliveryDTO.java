package com.ex.data;

import java.util.List;

import com.ex.entity.DeliveryEntity;
import com.ex.entity.OrderlistEntity;
import com.ex.entity.UserEntity;

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
	
	private String receivePostcode; // 받는사람 우편번호
    private String receiveAddress; //받는사람 기본주소
    private String receiveDetailAddress; //받는사람 상세주소
    private String receiveExtraAddress; //받는사람 참고항목
	
	private String memo; // 배송메모
	
	
	private String waybill; // 운송장번호
	
	private long usePoint;
	
	public static DeliveryDTO entityToDTO(DeliveryEntity de) {
		return DeliveryDTO.builder().id(de.getId())
				.user(de.getUser())
				.order(de.getOrder())
				.receiveName(de.getReceiveName())
				.receivePhone(de.getReceivePhone())
				.receivePostcode(de.getReceivePostcode())
				.receiveAddress(de.getReceiveAddress())
				.receiveDetailAddress(de.getReceiveDetailAddress())
				.receiveExtraAddress(de.getReceiveExtraAddress())
				.memo(de.getMemo())
				.usePoint(de.getUsePoint())
				.waybill(de.getWaybill()).build();
	}

}
