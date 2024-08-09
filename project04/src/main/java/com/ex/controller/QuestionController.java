package com.ex.controller;

import com.ex.data.QuestionDTO;
import com.ex.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService; // 문의 서비스

    // 모든 문의 목록을 조회합니다.
    @GetMapping("/questionList")
    public String list(Model model) {
        List<QuestionDTO> questionList = questionService.findAll();
        model.addAttribute("questionList", questionList);
        return "questionList"; // 문의 목록 페이지로 이동
    }

    // 문의 작성 폼으로 이동합니다. (로그인한 사용자만 접근 가능)
    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setUsername(userDetails.getUsername()); // 로그인한 사용자의 이름을 기본값으로 설정
        model.addAttribute("question", questionDTO);
        return "questionForm"; // 문의 작성 페이지로 이동
    }

    // 문의를 저장합니다. (로그인한 사용자만 접근 가능)
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String create(@Valid @ModelAttribute("question") QuestionDTO questionDTO, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (bindingResult.hasErrors()) {
            return "questionForm"; // 유효성 검사 실패 시 다시 작성 페이지로 이동
        }
        questionService.save(questionDTO, userDetails.getUsername());
        return "redirect:/question/questionList"; // 문의 목록 페이지로 리다이렉트
    }

    // 문의를 삭제합니다. (로그인한 사용자만 접근 가능)
    @PostMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        questionService.deleteById(id, userDetails.getUsername());
        return "redirect:/question/questionList"; // 문의 목록 페이지로 리다이렉트
    }

    // 특정 문의 조회 및 답변 작성 폼으로 이동합니다.
    @GetMapping("/detail/{id}")
    @PreAuthorize("isAuthenticated()")
    public String viewQuestion(@PathVariable("id") Long id, Model model) {
        QuestionDTO questionDTO = questionService.findById(id);
        model.addAttribute("question", questionDTO);
        return "questionDetail"; // 문의 상세 페이지로 이동
    }

    // 문의에 대한 답변을 저장합니다.
    @PostMapping("/detail/{id}/answer")
    @PreAuthorize("isAuthenticated()")
    public String answerQuestion(@PathVariable("id") Long id, @RequestParam("answer") String answer) {
        questionService.saveAnswer(id, answer);
        return "redirect:/question/detail/" + id; // 문의 상세 페이지로 리다이렉트
    }
}
