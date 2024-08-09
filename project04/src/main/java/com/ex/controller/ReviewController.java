package com.ex.controller;

import com.ex.service.ReviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰와 이미지를 저장하는 메서드
    @PostMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    public String addReview(@PathVariable("productId") Integer productId,
                            @RequestParam("content") String content,
                            @RequestParam("images") MultipartFile[] images,
                            Principal principal) {
        reviewService.addReviewWithImages(productId, content, principal.getName(), images);

        return "redirect:/product/detail/" + productId; // 해당 상품 상세 페이지로 리다이렉트
    }
	
	@GetMapping("/like/{id}")
	@PreAuthorize("isAuthenticated()")
	public String voteAnswer(@PathVariable("id") Integer id, Principal principal) {
		reviewService.likeReview(id, principal.getName());
		return String.format("redirect:/product/detail/%s", reviewService.findByReviewId(id).getId());
	}
	
}
