
package com.ex.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;

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
import com.ex.data.AmountDTO;
import com.ex.data.FaqDTO;
import com.ex.data.NoticeDTO;
import com.ex.data.ProductDTO;
import com.ex.service.AdminService;
import com.ex.service.FaqService;
import com.ex.service.NoticeService;
import com.ex.service.PhotoService;
import com.ex.service.ProductService;
import com.ex.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/*")
public class AdminController {
	
	private final ProductService productService;
	private final NoticeService noticeService;
	private final PhotoService photoService;;
	private final ReviewService reviewService;;
	private final AdminService adminService;
    private final FaqService faqService;
	
	
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
	
	@GetMapping("adopted/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String adopted(@PathVariable("id") Integer id) {
		reviewService.adopted(id);
		return String.format("redirect:/product/detail/%s", reviewService.findByReviewId(id).getId());
	}
	
	@PostMapping("buy/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String buy(@PathVariable("id")Integer id,@RequestParam("rate")int rate) {
		productService.buyStock(id, rate);
		return "redirect:/admin/product/"+id;
	}
	
	@GetMapping("NoticeWriteForm")
	public String noticeWriteForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
		if (id != null) {
			NoticeDTO noticeDTO = noticeService.findById(id);
			noticeDTO.setNoticePhotoPath(photoService.getNotice(id));  // 이미지 경로 설정
			model.addAttribute("noticeDTO", noticeDTO);
		} else {
			model.addAttribute("noticeDTO", new NoticeDTO());
		}
		return "NoticeWriteForm";
	}
	
	// 등록/수정 처리 메서드
	@PostMapping("NoticewritePro")
	public String saveOrUpdateProduct(@RequestParam("notice") MultipartFile[] notice,
																	@ModelAttribute NoticeDTO noticeDTO)throws IOException {
		Integer id = noticeService.NoticecreateOrUpdate(noticeDTO, notice);
		return String.format("redirect:/NoticeDetail/%d", id);
	}
		
    // FAQ 등록 폼을 표시하는 메서드
    @GetMapping("faqForm")
    public String showFaqForm(Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        return "faqForm";  // FAQ 등록 폼을 표시하는 뷰로 이동
    }

    // FAQ 등록 요청을 처리하는 메서드
    @PostMapping("faqList")
    public String createFaq(@ModelAttribute FaqDTO faqDTO, Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        faqService.createFaq(faqDTO);  // 현재 로그인한 사용자의 이름을 이용해 FAQ를 등록
        return "redirect:/faq/faqList";  // FAQ 리스트 페이지로 리다이렉트
    }

    // FAQ 수정 폼을 표시하는 메서드
    @GetMapping("faqEdit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        FaqDTO faqDTO = faqService.getFaqById(id);  // 기존 FAQ를 조회
        model.addAttribute("faq", faqDTO);  // 조회된 FAQ 데이터를 모델에 추가
        return "faqEdit";  // FAQ 수정 폼을 표시하는 뷰로 이동
    }

    // FAQ 수정 요청을 처리하는 메서드
    @PostMapping("faqUpdate/{id}")
    public String updateFaq(@PathVariable("id") Long id, @ModelAttribute FaqDTO faqDTO, Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        faqService.updateFaq(id, faqDTO);  // FAQ 수정
        return "redirect:/faq/faqList";  // 수정 후 FAQ 리스트 페이지로 리다이렉트
    }

    // FAQ 삭제 요청을 처리하는 메서드
    @PostMapping("faqDelete/{id}")
    public String deleteFaq(@PathVariable("id") Long id) {
        faqService.deleteFaq(id);  // 특정 ID의 FAQ를 삭제
        return "redirect:/faq/faqList";  // FAQ 리스트 페이지로 리다이렉트
    }
	
    @GetMapping("mainNotice/{id}")
    public String mainNotice(@PathVariable("id")Integer id) {
    	noticeService.changeMainNotice(id);
    	
    	return "redirect:/NoticeList";
    }
    
	
	
	
	
}
