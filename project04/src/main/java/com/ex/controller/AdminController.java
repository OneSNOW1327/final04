package com.ex.controller;

import java.time.LocalDate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ex.data.AmountDTO;
import com.ex.data.ProductDTO;
import com.ex.service.AdminService;
import com.ex.service.PhotoService;
import com.ex.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/*")
public class AdminController {
	
	private final ProductService productService;
	private final PhotoService photoService;;
	private final AdminService adminService;
	
	
	@GetMapping("main")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String adminMain(Model model){
		model.addAttribute("typeList", productService.getAllProductTypes());
		model.addAttribute("allAmount",adminService.total());
		model.addAttribute("totalSell", adminService.total("sell"));
		return "adminMain";
	}

	@GetMapping("type/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String adminMain(@PathVariable("id")Integer id,Model model,
			@RequestParam(value="page", defaultValue="1")  int page) {			
			model.addAttribute("productList", productService.typeList(id, page-1));
			model.addAttribute("productType",id);
		return "adminList";
	}

	@GetMapping("product/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String product(@PathVariable("id") Integer id, Model model) {
		ProductDTO productDTO = productService.findById(id);
		productDTO.setThumbnailPaths(photoService.getThumbnailPaths(id));
		productDTO.setDescriptionImagePaths(photoService.getDescriptionImagePaths(id));
		model.addAttribute("productDTO", productDTO);
		return "adminDetail";
	}
	
	@PostMapping("amount")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String amount(@RequestParam("amount")long amount, @RequestParam("reason")String reason) {
		AmountDTO amountDTO = adminService.total().get(0);
		if(reason.equals("deposit")) {
			amountDTO.setAmount(amount);			
		}else if(reason.equals("withdraw")) {
			amountDTO.setAmount(amount * (-1));	
		}
		amountDTO.setReason(reason);
		adminService.amount(amountDTO, LocalDate.now());
		return "redirect:/admin/main";
	}
	
}
