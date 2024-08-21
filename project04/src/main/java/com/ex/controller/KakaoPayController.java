package com.ex.controller;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ex.data.DeliveryDTO;
import com.ex.entity.DeliveryEntity;
import com.ex.repository.BasketRepository;
import com.ex.service.KakaoPayService;
import com.ex.service.ProductService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/kakao/*")
public class KakaoPayController {
	
	private final KakaoPayService kakaoPayService;
	private final ProductService productService;
	private final BasketRepository basketRepository;
	
	@PostMapping("/kakaoPay")
	@PreAuthorize("isAuthenticated()")
	public String kakaoPay(@RequestParam("basketId") String basketId, Model model, Principal principal,
											@ModelAttribute DeliveryDTO deliveryDTO, @RequestParam("payPrice") String payPrice,
											@RequestParam(value = "point", defaultValue = "0") long point){
		// basket의ID를 "ID,ID,.."형식으로 가지고있음
		List<Integer> basketIds = Arrays.stream(basketId.split(","))// 콤마를 기준으로 분리
																.map(Integer::parseInt)// 스트림의 요소를 정수로 변환
																.collect(Collectors.toList());// 스트림의 모든 요소를 새로운 리스트로 만들어 반환	
		// 배송정보 저장
		DeliveryEntity delivery = productService.saveDelivery(deliveryDTO,principal.getName(),point);
		List<String> productNames = new ArrayList<>();
		for (Integer id : basketIds) {
			productNames.add(basketRepository.findById(id).get().getProduct().getName());
		}
		String products=productNames.toString();
		return "redirect:" + kakaoPayService.kakaoPayReady(payPrice,delivery,products,basketId,principal.getName(),point);
	}

	@GetMapping("/kakaoPaySuccess")
	public String kakaoPaySuccess(@RequestParam("pg_token")String pgToken, Model model,Principal principal) {
		String tid = kakaoPayService.payApprove(pgToken);
		return "redirect:/kakao/completed/"+tid;
	}
	
	@GetMapping("/completed/{tid}")
	public String complate(Model model,@PathVariable("tid")String tid) {
		model.addAttribute("delivery",productService.DTOFindByTid(tid));
		return "fullPayResult";
	}
	
	@GetMapping("/cancel")
	public String  cancel() {
		return "kakaoPayCancel";
	}
   
}

