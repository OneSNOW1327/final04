package com.ex.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ex.data.BasketDTO;
import com.ex.data.KakaoPayDTO;
import com.ex.data.OrderlistDTO;
import com.ex.data.ProductDTO;
import com.ex.entity.DeliveryEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log
public class KakaoPayService {
	
    private static final String Host = "https://open-api.kakaopay.com";
    private KakaoPayDTO kakaoPayDTO;
    private DeliveryEntity deliveryEntity;
    private String basketId;
    private ProductDTO productDTO;
    private int productQuantity;
    private final ProductService productService;
    private final UserService userService;
    
    public String kakaoPayReady(String payPrice, DeliveryEntity delivery, String productNames,String basketIds, String userName,long point, int quantity) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        							  
        
        headers.set("Authorization", "SECRET_KEY" + " " + "DEV5E3FCF1585B617B742FDE8166830ADC9B0D73");
        headers.set("Content-type", "application/json");
        
        // Server Request Body : 서버 요청 본문
        
        Map<String, String> params = new HashMap<String, String>();


        
        params.put("cid", "TC0ONETIME"); // 가맹점 코드 - 테스트용 건들지말것
        params.put("partner_order_id", Integer.toString(delivery.getId())); // 주문 번호 아래와 동일
        params.put("partner_user_id", userName); // 회원 아이디
        params.put("item_name", productNames); // 상품 명  
        params.put("quantity", String.valueOf(quantity)); // 상품 수량
        params.put("total_amount", payPrice); // 상품 가격  
        params.put("tax_free_amount", "0"); // 상품 비과세 금액_특정 상품에 대해 세금이 면제되는 금액
        params.put("approval_url", "http://localhost:8080/kakao/kakaoPaySuccess"); // 성공시 url
        params.put("cancel_url", "http://localhost:8080/kakao/cancel"); // 실패시 url
        params.put("fail_url", "http://localhost:8080/kakao/fail");

        // 헤더와 바디 붙이기
        HttpEntity<Map<String, String>> body = new HttpEntity<Map<String, String>>(params, headers);

        try {
        	userService.usePoint(point, userName);
            kakaoPayDTO = restTemplate.postForObject(new URI(Host + "/online/v1/payment/ready"), body, KakaoPayDTO.class);
            deliveryEntity = delivery;
            basketId = basketIds;
            //t아이디 대입해서 아래 호출
            log.info(""+ kakaoPayDTO);
       
            return kakaoPayDTO.getNext_redirect_pc_url();
            
        } catch (RestClientException e) {
            e.printStackTrace();		
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
      
        return "/pay";
    }

    public String directKakaoPayReady(String payPrice, DeliveryEntity delivery, String userName,long point,Integer productId, int quantity) {
        RestTemplate restTemplate = new RestTemplate();
        ProductDTO pDTO = productService.findById(productId);
        HttpHeaders headers = new HttpHeaders();
        							  
        headers.set("Authorization", "SECRET_KEY" + " " + "DEV5E3FCF1585B617B742FDE8166830ADC9B0D73");
        headers.set("Content-type", "application/json");
        
        // Server Request Body : 서버 요청 본문
        
        Map<String, String> params = new HashMap<String, String>();


        
        params.put("cid", "TC0ONETIME"); // 가맹점 코드 - 테스트용 건들지말것
        params.put("partner_order_id", Integer.toString(delivery.getId())); // 주문 번호 아래와 동일
        params.put("partner_user_id", userName); // 회원 아이디
        params.put("item_name", pDTO.getName()); // 상품 명  
        params.put("quantity", "1"); // 상품 수량
        params.put("total_amount", payPrice); // 상품 가격  
        params.put("tax_free_amount", "0"); // 상품 비과세 금액_특정 상품에 대해 세금이 면제되는 금액
        params.put("approval_url", "http://localhost:8080/kakao/kakaoPaySuccess"); // 성공시 url
        params.put("cancel_url", "http://localhost:8080/"); // 실패시 url
        params.put("fail_url", "http://localhost:8080/kakao/fail");

        // 헤더와 바디 붙이기
        HttpEntity<Map<String, String>> body = new HttpEntity<Map<String, String>>(params, headers);

        try {
        	userService.usePoint(point, userName);
            kakaoPayDTO = restTemplate.postForObject(new URI(Host + "/online/v1/payment/ready"), body, KakaoPayDTO.class);
            deliveryEntity = delivery;
            productDTO = pDTO;
            productQuantity = quantity;
            //t아이디 대입해서 아래 호출
            log.info(""+ kakaoPayDTO);
       
            return kakaoPayDTO.getNext_redirect_pc_url();
            
        } catch (RestClientException e) {
            e.printStackTrace();		
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
      
        return "/pay";
    }

    
    // 카카오페이 결제 승인
    // 사용자가 결제 수단을 선택하고 비밀번호를 입력해 결제 인증을 완료한 뒤, 최종적으로 결제 완료 처리를 하는 단계
    public String payApprove(String pgToken) {

    	if(basketId!=null) {
	    	List<Integer> basketIds = Arrays.stream(basketId.split(","))// 콤마를 기준으로 분리
	    			.map(Integer::parseInt)// 스트림의 요소를 정수로 변환
	    			.collect(Collectors.toList());// 스트림의 모든 요소를 새로운 리스트로 만들어 반환		
	    	productService.saveOrderInfo(basketIds, deliveryEntity,kakaoPayDTO.getTid());
    	}else {
    		productService.saveOrderInfo(productDTO.getId(), deliveryEntity,kakaoPayDTO.getTid(),productQuantity);
    	}
    	Map<String, String> parameters = new HashMap<>();
    	HttpHeaders headers = new HttpHeaders();     						
    	headers.set("Authorization", "SECRET_KEY" + " " + "DEV5E3FCF1585B617B742FDE8166830ADC9B0D73");
    	headers.set("Content-type", "application/json");        
    	parameters.put("cid", "TC0ONETIME");              // 가맹점 코드(테스트용) 건들지말것
    	parameters.put("tid", kakaoPayDTO.getTid());      // 결제 고유번호 카카오만 아는거 우리쪽 디비에 저장해야함
    	parameters.put("partner_order_id", Integer.toString(deliveryEntity.getId())); // 주문번호 위와 동일
    	parameters.put("partner_user_id", deliveryEntity.getUser().getUsername());// 회원 아이디
    	parameters.put("pg_token", pgToken);              // 결제승인 요청을 인증하는 토큰


    	HttpEntity<Map<String, String>> requestEntity = new HttpEntity<Map<String, String>>(parameters, headers);

    	RestTemplate template = new RestTemplate();
    	String url = "https://open-api.kakaopay.com/online/v1/payment/approve";
    	KakaoPayDTO approveResponse = template.postForObject(url, requestEntity, KakaoPayDTO.class);
    	log.info("결제승인 응답객체: " + approveResponse);

    	return kakaoPayDTO.getTid();
    }
    
    public String cancelPayment(OrderlistDTO oDTO) {
    	System.out.println("==================="+oDTO.getTid());
    	long cancelAmount = (long)(oDTO.getProduct().getSellPrice()*((100-oDTO.getDiscount())/100)*oDTO.getQuantity());
    	
    	RestTemplate restTemplate = new RestTemplate();

    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Authorization", "SECRET_KEY " + "DEV5E3FCF1585B617B742FDE8166830ADC9B0D73");
    	headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    	Map<String, String> params = new HashMap<>();
    	params.put("cid", "TC0ONETIME"); // 가맹점 코드
    	params.put("tid", oDTO.getTid()); // 결제 고유번호
    	params.put("cancel_amount", String.valueOf(cancelAmount)); // 취소 금액
    	params.put("cancel_tax_free_amount", "0"); // 취소 비과세 금액
    	
    	HttpEntity<Map<String,String>> body = new HttpEntity<>(params, headers);

    	try {
    		URI uri = new URI(Host + "/online/v1/payment/cancel");
    		KakaoPayDTO cancelResponse = restTemplate.postForObject(uri, body, KakaoPayDTO.class);
    		log.info("결제 취소 응답객체: " + cancelResponse);

    		return String.valueOf(oDTO.getId());
    	} catch (RestClientException | URISyntaxException e) {
    		e.printStackTrace();
    		return "결제 취소 중 오류가 발생했습니다.";
    	}
    }

    
}
