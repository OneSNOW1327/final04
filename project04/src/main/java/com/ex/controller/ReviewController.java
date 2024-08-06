package com.ex.controller;

import com.ex.entity.UserEntity;
import com.ex.service.ReviewService;
import com.ex.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    public ReviewController(ReviewService reviewService, UserRepository userRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    // 리뷰 작성 폼을 보여주는 메서드
    @GetMapping("/{productId}")
    public String showReviewForm(@PathVariable("productId") Integer productId, Model model) {
        model.addAttribute("productId", productId);

        // 로그인된 사용자 정보 가져오기 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
        }

        return "review"; // review.html 뷰 반환
    }

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
    
    // 리뷰 좋아요 처리 메서드
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> likeReview(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        if (optionalUser.isPresent()) {
            return reviewService.toggleLikeReview(id, optionalUser.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다."); // 사용자 정보가 없을 때
    }
}
