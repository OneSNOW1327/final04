package com.ex.controller;

import com.ex.data.ProductDTO;
import com.ex.entity.ProducttypeEntity;
import com.ex.service.ProductService;
import com.ex.service.PhotoService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/writeForm")
    public String productWriteForm(Model model) {
        List<ProducttypeEntity> productTypes = productService.getAllProductTypes();
        model.addAttribute("productTypes", productTypes);
        return "writeForm";
    }

    @PostMapping("/writePro")
    public String productWrite(
            @ModelAttribute ProductDTO productDTO,
            @RequestParam("thumbnails") MultipartFile[] thumbnails,
            @RequestParam("descriptionImages") MultipartFile[] descriptionImages
    ) throws IOException {

        Integer id = productService.create(productDTO, thumbnails, descriptionImages);
        return String.format("redirect:/product/detail/%d", id);
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        ProductDTO productDTO = productService.findById(id);
        productDTO.setThumbnailPaths(photoService.getThumbnailPaths(id));
        productDTO.setDescriptionImagePaths(photoService.getDescriptionImagePaths(id));
        model.addAttribute("productDTO", productDTO);
        return "productDetail";
    }
    
    //장바구니담기 버튼 눌렀을때_가은 
		@PostMapping("basketAdd/{id}")
		public String addToBasket(Principal principal,
								 @RequestParam("quantity")int quantity, 
								 @PathVariable("id")Integer id) {
			productService.addToBasket(id, principal.getName(), quantity);			
			return "redirect:/product/Detail/"+id;
		}
		
//찜 버튼 눌렀을때_가은
		@PostMapping("wishList/{id}")
		public String addToWish(Principal principal,
							   @PathVariable("id")Integer id){
			productService.addToWish(id, principal.getName());
			return "redirect:/product/Detail/"+id;
		}

}

