package com.ex.entity;

import java.util.List;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class DeliveryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Integer id; //pk

	@OneToMany(mappedBy = "delivery")
	private List<OrderlistEntity> order; //주문 내역 OrderlistEntity
	
	@ManyToOne
	private UserEntity user;
	
	private String receiveName; // 받는사람 이름
	
	private String receivePhone; // 받는사람 전화
	
	private String receivePostcode; // 받는사람 우편번호
    private String receiveAddress; //받는사람 기본주소
    private String receiveDetailAddress; //받는사람 상세주소
    private String receiveExtraAddress; //받는사람 참고항목
	
	private String memo; // 배송메모
	
	private String situation; //현재상태
	
	private String waybill; // 운송장번호
	
	private long usePoint;
	
	
}
