package com.ex.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ex.data.AmountDTO;
import com.ex.entity.AmountEntity;
import com.ex.repository.AmountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AmountRepository amountRepository;
	
	public void amount(AmountDTO amountDTO,LocalDate ld) {
		LocalDateTime start = ld.atStartOfDay();
		LocalDateTime end = ld.atTime(23,59,59,999999999);
		AmountEntity ae = AmountEntity.builder().build();
		if(amountDTO.getId() != 0 && amountDTO.getId() != null) {
			Optional<AmountEntity> op = 
					amountRepository.findByReasonAndRecordDateBetweenOrderByRecordDateDesc(amountDTO.getReason(), start, end);
			if(op.isPresent()) {
				ae = op.get();
				ae.setAmount(ae.getAmount()+amountDTO.getAmount());
			}else {
				ae.setAmount(amountDTO.getAmount());
			}
			ae.setTotalAmount(amountDTO.getTotalAmount()+amountDTO.getAmount());
		}else {
			ae.setAmount(amountDTO.getAmount());
			ae.setTotalAmount(amountDTO.getAmount());
		}
		ae.setReason(amountDTO.getReason());
		ae.setRecordDate(LocalDateTime.now());
		amountRepository.save(ae);
	}
	
	public List<AmountDTO> total(){
		AmountDTO amountDTO = AmountDTO.builder().build();
		List<AmountEntity> ael = amountRepository.findAllByOrderByRecordDateDesc();
		List<AmountDTO> adl = new ArrayList<>();
		if(!ael.isEmpty()) {
			for(int i = 0; i< ael.size();i++) {
				adl.add(AmountDTO.entityToDTO(ael.get(i)));
			}
		}else {
			amountDTO.setId(0);
			adl.add(amountDTO);
		}
		return adl;
	}
	
	public List<AmountDTO> total(String reason){
		List<AmountEntity> ael = amountRepository.findAllByReasonOrderByRecordDateAsc(reason);
		List<AmountDTO> adl = new ArrayList<>();
		if(!ael.isEmpty()) {
			for(int i = 0; i< ael.size();i++) {
				adl.add(AmountDTO.entityToDTO(ael.get(i)));
			}
		}
		return adl;
	}
	
	
}
