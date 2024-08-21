package com.ex.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AmountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private LocalDateTime recordDate;
	
	//변동 금액
	private long amount;

	//총액
	private long totalAmount;
	
	//사유 (예시: 입금, 물건판매 등등)
	private String reason;
    
}
