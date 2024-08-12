package com.ex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.ex.service.FaqService;
import com.ex.service.UserService;
import com.ex.data.FaqDTO;
import com.ex.entity.UserEntity;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;
    private final UserService userService;

    // FAQ 리스트를 표시하는 메서드
    @GetMapping("/faqList")
    public String showFaqList(Model model, Principal principal) {
        List<FaqDTO> faqs = faqService.getAllFaqs();  // 모든 FAQ를 조회
        model.addAttribute("faqs", faqs);  // 조회된 FAQ 리스트를 모델에 추가

        if (principal != null) {
            UserEntity user = userService.findByUserName(principal.getName());
            model.addAttribute("userGrade", user.getGrade());  // 사용자 등급을 모델에 추가
        } else {
            model.addAttribute("userGrade", 1);  // 비로그인 사용자는 기본적으로 grade 1로 설정
        }

        return "faqList";  // FAQ 리스트를 표시하는 뷰로 이동
    }

    // FAQ 등록 폼을 표시하는 메서드
    @GetMapping("/faqForm")
    public String showFaqForm(Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        return "faqForm";  // FAQ 등록 폼을 표시하는 뷰로 이동
    }

    // FAQ 등록 요청을 처리하는 메서드
    @PostMapping("/faqList")
    public String createFaq(@ModelAttribute FaqDTO faqDTO, Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        faqService.createFaq(faqDTO, principal.getName());  // 현재 로그인한 사용자의 이름을 이용해 FAQ를 등록
        return "redirect:/faq/faqList";  // FAQ 리스트 페이지로 리다이렉트
    }

    // FAQ 수정 폼을 표시하는 메서드
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        FaqDTO faqDTO = faqService.getFaqById(id);  // 기존 FAQ를 조회
        model.addAttribute("faq", faqDTO);  // 조회된 FAQ 데이터를 모델에 추가
        return "faqEdit";  // FAQ 수정 폼을 표시하는 뷰로 이동
    }

    // FAQ 수정 요청을 처리하는 메서드
    @PostMapping("/update/{id}")
    public String updateFaq(@PathVariable("id") Long id, @ModelAttribute FaqDTO faqDTO, Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        faqService.updateFaq(id, faqDTO);  // FAQ 수정
        return "redirect:/faq/faqList";  // 수정 후 FAQ 리스트 페이지로 리다이렉트
    }

    // FAQ 삭제 요청을 처리하는 메서드
    @PostMapping("/delete/{id}")
    public String deleteFaq(@PathVariable("id") Long id) {
        faqService.deleteFaq(id);  // 특정 ID의 FAQ를 삭제
        return "redirect:/faq/faqList";  // FAQ 리스트 페이지로 리다이렉트
    }
}
