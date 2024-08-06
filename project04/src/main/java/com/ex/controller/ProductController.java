package com.ex.controller;

import com.ex.data.BasketDTO;
import com.ex.data.ProductDTO;
import com.ex.entity.BasketEntity;
import com.ex.entity.DeliveryEntity;
import com.ex.entity.ProducttypeEntity;
import com.ex.service.ProductService;
import com.ex.service.UserService;
import com.ex.service.PhotoService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestBody;

 
@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
 
	private final ProductService productService;
	private final PhotoService photoService;
	private final UserService userService;

	// 등록/수정 페이지로 이동
	@GetMapping("/writeForm")
	public String productWriteForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
		if (id != null) {
			ProductDTO productDTO = productService.findById(id);
			productDTO.setThumbnailPaths(photoService.getThumbnailPaths(id));
			productDTO.setDescriptionImagePaths(photoService.getDescriptionImagePaths(id));
			if (productDTO.getType() != null) {
				productDTO.setTypeId(productDTO.getType().getId());
			}
			model.addAttribute("productDTO", productDTO);
		} else {
			model.addAttribute("productDTO", new ProductDTO());
		}
		List<ProducttypeEntity> productTypes = productService.getAllProductTypes();
		model.addAttribute("productTypes", productTypes);
		return "writeForm";
	}

	// 등록/수정 처리 메서드
	@PostMapping("/writePro")
	public String saveOrUpdateProduct(
			@ModelAttribute ProductDTO productDTO,
			@RequestParam("thumbnails") MultipartFile[] thumbnails,
			@RequestParam("descriptionImages") MultipartFile[] descriptionImages,
			@RequestParam(value = "deleteThumbnailIds", required = false) List<Integer> deleteThumbnailIds,
			@RequestParam(value = "deleteDescriptionImageIds", required = false) List<Integer> deleteDescriptionImageIds
			) throws IOException {
		Integer id = productService.createOrUpdate(productDTO, thumbnails, descriptionImages, deleteThumbnailIds, deleteDescriptionImageIds);
		return String.format("redirect:/product/detail/%d", id);
	}

	// 디테일
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Integer id, Model model) {
		ProductDTO productDTO = productService.findById(id);
		productDTO.setThumbnailPaths(photoService.getThumbnailPaths(id));
		productDTO.setDescriptionImagePaths(photoService.getDescriptionImagePaths(id));
		model.addAttribute("productDTO", productDTO);        
		return "productDetail";
	}

	@GetMapping("/deleteThumbnail")
	public String deleteThumbnail(@RequestParam("id") Integer id, @RequestParam("productId") Integer productId) {
		photoService.deleteThumbnail(id);
		return "redirect:/product/writeForm?id=" + productId;
	}

	@GetMapping("/deleteDescriptionImage")
	public String deleteDescriptionImage(@RequestParam("id") Integer id, @RequestParam("productId") Integer productId) {
		photoService.deleteDescriptionImage(id);
		return "redirect:/product/writeForm?id=" + productId;
	}

	@PostMapping("/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id) {
		productService.deleteProduct(id);
		return "redirect:/main";
	}

	@GetMapping("/list/{id}")
	public String list(@PathVariable("id")Integer id,Model model,
			@RequestParam(value="page", defaultValue="1")  int page) {			
		model.addAttribute("productList", productService.typeList(id, page-1));
		model.addAttribute("productType",id);
		return "productList";
	}

//0801 성진추가
	@PostMapping("/search")
	public String search(@RequestParam(value="kw", defaultValue="") String kw, Model model,
										@RequestParam(value="page", defaultValue="1")  int page) {			
		model.addAttribute("productList", productService.productSearch(page-1, kw));
		model.addAttribute("kw",kw);
		return "productSearch";
	}
	
//(가은)장바구니담기 버튼 눌렀을때 
	@PostMapping("/basketAdd")
	public String addToBasket(Principal principal,
			@RequestParam("quantity")int quantity, 
			@RequestParam("productId")Integer productId) {
		productService.addToBasket(productId, principal.getName(), quantity);			
		return "redirect:/product/detail/"+productId;
	}

//(가은) 장바구니 현황
	@GetMapping("/basketList")
	@PreAuthorize("isAuthenticated()")
	public String basketList(Model model, Principal principal) {
		List<BasketEntity> userBasket = productService.userBasket(principal.getName());
		model.addAttribute("userBasket", userBasket);
		return "basket";
	}

//(가은) 장바구니 선택상품 삭제
	@PostMapping("/removeBasket")
	@PreAuthorize("isAuthenticated()")
	public String removeBasket(@RequestParam("removeSelected") String removeSelected) {
	    // "removeSelected"라는 이름의 파라미터 basket의ID를 "ID,ID,.."형식으로 가지고있음
	    List<Integer> basketIds = Arrays.stream(removeSelected.split(","))//콤마를 기준으로 분리
		                                   .map(Integer::parseInt)//스트림의 요소를 정수로 변환
		                                   .collect(Collectors.toList());//스트림의 모든 요소를 새로운 리스트로 만들어 반환
		   productService.removeSelectedBaskets(basketIds);
		   return "redirect:/product/basketList";
	}


//(가은) 장바구니 수량 변경 ♣0802♣
	@PostMapping("/updateQuantity")
	public String updateQuantity(@RequestParam("updatedBasketId") int basketId,
								 @RequestParam("updatedQuantity") int quantity) {
		if (basketId != 0 && quantity != 0) {
			System.out.println(basketId + quantity);
	        productService.updateQuantity(basketId, quantity);	        
	    } else {
	        // 값이 올바르지 않은 경우 로그를 남기거나 에러 처리를 할 수 있습니다.
	        System.out.println("Invalid basketId or quantity");
	    }
		return "redirect:/product/basketList";
	}
	
//(가은) 찜 버튼 눌렀을때
	@PostMapping("/wishadd/{id}")
	@PreAuthorize("isAuthenticated()")
	public String addToWish(Principal principal,
						   @PathVariable("id")Integer id){
		productService.addToWish(id, principal.getName());
		return "redirect:/product/Detail/"+id;
	}
	
//(가은) 결제페이지 ♣0802♣
	@PostMapping("/paymentPage")//선택된 장바구니 항목이 넘어옴
	@PreAuthorize("isAuthenticated()")
	public String paymentPage(@RequestParam("selectProduct") String selectProduct,
							  Model model, Principal principal) {
		List<Integer> basketIds = Arrays.stream(selectProduct.split(","))
				.map(Integer::parseInt)//스트림의 요소를 정수로 변환
				.collect(Collectors.toList());//스트림의 모든 요소를 새로운 리스트로		
	 //선택된 상품들 결제페이지로 보내기
		model.addAttribute("expectPay", productService.expectPay(basketIds));
		model.addAttribute("principalUser", userService.findByUserName(principal.getName()));							
	return "paymentPage";
		}

//(가은) 결제정보(상품정보,결제수단) 주문테이블저장, 배송정보저장 ♣0806♣
	@PostMapping("/requestPay")
	public String executePay(@RequestParam("basketId") List<Integer> basketId,
            				@RequestParam("delivery") DeliveryEntity delivery,
            				@RequestParam("paymentOption") String paymentMethod,
            				Model model) {
		for (int a : basketId) {
			System.out.println("basketId"+a);
		}
		//장바구니내역 주문테이블로 저장

		
		//주문완료페이지로 정보 보내기
		//model.addAttribute("products", products);
        model.addAttribute("delivery", delivery);
        model.addAttribute("paymentMethod", paymentMethod);
		return "paymentResult";
	}
	
}
