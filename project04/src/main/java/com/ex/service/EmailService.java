package com.ex.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ex.data.ProductDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender javaMailSender;
	
	@Async
	public boolean sendMailReject(ProductDTO productDTO,int stock) {
		boolean msg = false;
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		
		simpleMailMessage.setTo(productDTO.getOrderEmail());
		simpleMailMessage.setSubject("물품 구매");
		simpleMailMessage.setFrom("shadow132789@gmail.com");
		simpleMailMessage.setText(productDTO.getName()+" 상품 " +stock+"개 구매 부탁드리겠습니다.");
		
		try {
			javaMailSender.send(simpleMailMessage);
		}catch(Exception e) {
			e.printStackTrace();
			return msg;
		}
		return msg = true;
		
	}
	
}
