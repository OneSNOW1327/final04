package com.ex.controller;

import com.ex.data.ProductDTO;
import com.ex.entity.BasketEntity;
import com.ex.entity.ProducttypeEntity;
import com.ex.service.ProductService;
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
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final PhotoService photoService;

    // 등록/수정 페이지로 이동
    @GetMapping("/writeForm")
    public String productWriteForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            ProductDTO productDTO = productService.findById(id);
            productDTO.setThumbnailPaths(photoService.getThumbnailPaths(id));
            productDTO.setDescriptionImagePaths(photoService.getDescriptionImagePaths(id));
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

 //디테일
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        ProductDTO productDTO = productService.findById(id);
        productDTO.setThumbnailPaths(photoService.getThumbnailPaths(id));
        productDTO.setDescriptionImagePaths(photoService.getDescriptionImagePaths(id));
        model.addAttribute("productDTO", productDTO);        
        return "productDetail";
    }
    
//장바구니담기 버튼 눌렀을때 
		@PostMapping("basketAdd")
		public String addToBasket(Principal principal,
								 @RequestParam("quantity")int quantity, 
								 @RequestParam("productId")Integer productId) {
			productService.addToBasket(productId, principal.getName(), quantity);			
			return "redirect:/product/detail/"+productId;
		}
		
// 장바구니 현황
		@GetMapping("basketList")
		@PreAuthorize("isAuthenticated()")
		public String basketList(Model model, Principal principal) {
			List<BasketEntity> userBasket = productService.userBasket(principal.getName());
			model.addAttribute("userBasket", userBasket);
			return "basket";
		}
		
//찜 버튼 눌렀을때
		@PostMapping("wishadd/{id}")
		public String addToWish(Principal principal,
							   @PathVariable("id")Integer id){
			productService.addToWish(id, principal.getName());
			return "redirect:/product/Detail/"+id;
		}

//결제페이지
		@PostMapping("paymentPage")//선택된 장바구니 항목이 넘어옴
		public String paymentPage(@RequestParam("selectProduct") String selectProduct,
								 Model model, Principal principal) {	   
		    //선택된 상품들 결제페이지로 보내기
		    model.addAttribute("expectPay", productService.expectPay(selectProduct));
			
		    return "paymentPage";
		}

}

