package com.ex.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ex.entity.AmountEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmountDTO {
	
	private Integer id;

	private LocalDateTime recordDate;

	private LocalDate date;
	//변동 금액
	private long amount;

	//총액
	private long totalAmount;
	
	//사유 (예시: 입금, 물건판매 등등)
	private String reason;

	public static AmountDTO entityToDTO(AmountEntity ae) {
		return AmountDTO.builder().id(ae.getId())
														.amount(ae.getAmount())
														.reason(ae.getReason())
														.recordDate(ae.getRecordDate())
														.totalAmount(ae.getTotalAmount())
														.build();
	}
	
}
