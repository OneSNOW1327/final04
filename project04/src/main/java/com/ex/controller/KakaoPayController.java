package com.ex.controller;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ex.data.BasketDTO;
import com.ex.data.DeliveryDTO;
import com.ex.data.OrderlistDTO;
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
											@RequestParam(value = "point", defaultValue = "0") String point){
		if(point.equals("")||point==null) {
			point = "0";
		}
		// basket의ID를 "ID,ID,.."형식으로 가지고있음
		List<Integer> basketIds = Arrays.stream(basketId.split(","))// 콤마를 기준으로 분리
																.map(Integer::parseInt)// 스트림의 요소를 정수로 변환
																.collect(Collectors.toList());// 스트림의 모든 요소를 새로운 리스트로 만들어 반환	
		// 배송정보 저장
		DeliveryEntity delivery = productService.saveDelivery(deliveryDTO,principal.getName(),Integer.parseInt(point));
		List<String> productNames = new ArrayList<>();
		for (Integer id : basketIds) {
			productNames.add(basketRepository.findById(id).get().getProduct().getName());
		}
		String products=productNames.toString();
		return "redirect:" + kakaoPayService.kakaoPayReady(payPrice,delivery,products,basketId,principal.getName(),Integer.parseInt(point),productNames.size());
	}
	
	@PostMapping("/directKakaoPay")
	@PreAuthorize("isAuthenticated()")
	public String directKakaoPay(Model model, Principal principal, 
											@RequestParam("productId") Integer productId, @RequestParam("quantity") int quantity,
											@ModelAttribute DeliveryDTO deliveryDTO, @RequestParam("payPrice") String payPrice,
											@RequestParam(value = "point", defaultValue = "0") String point){
		if(point.equals("")||point==null) {
			point = "0";
		}
		// 배송정보 저장
		DeliveryEntity delivery = productService.saveDelivery(deliveryDTO,principal.getName(),Integer.parseInt(point));
		return "redirect:" + kakaoPayService.directKakaoPayReady(payPrice,delivery,principal.getName(),Integer.parseInt(point),productId, quantity);
	}

	
	@GetMapping("/kakaoPaySuccess")
	public String kakaoPaySuccess(@RequestParam("pg_token")String pgToken, Model model,Principal principal) {
		String tid = kakaoPayService.payApprove(pgToken);
		return "redirect:/kakao/completed/"+tid;
	}
	
	@GetMapping("/completed/{tid}")
	public String complate(Model model,@PathVariable("tid")String tid) {
		model.addAttribute("delivery",productService.findByTid(tid));
		return "fullPayResult";
	}

	@GetMapping("/cancel/{id}")
	public String  cancel(@PathVariable("id")long id,Model model) {
        String result = kakaoPayService.cancelPayment(OrderlistDTO.entityToDTO(productService.orders(id)));
        try {
        	long orderid= Integer.parseInt(result);
        	model.addAttribute("cancelOrder", productService.orderCancel(orderid));
        	model.addAttribute("nowTime", LocalDateTime.now());
    		return "cancelOrder";
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return "redirect:/product/simplePayResult";
	}
	
    // 결제 취소 요청 처리
    @PostMapping("/cancelPayment")
    @PreAuthorize("isAuthenticated()")
    public String cancelPayment(@RequestParam("id") long id,
                                Model model) {
        String result = kakaoPayService.cancelPayment(OrderlistDTO.entityToDTO(productService.orders(id)));
        model.addAttribute("cancelResult", result);
        return "kakaoPayCancelResult"; // 결제 취소 결과 페이지
    }

}

