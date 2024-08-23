package com.ex.controller;

import com.ex.data.KakaoDTO;
import com.ex.data.UserDTO;
import com.ex.service.KakaoService;
import com.ex.service.UserService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class KakaoController {

    private static final Logger logger = LoggerFactory.getLogger(KakaoController.class);

    private final KakaoService kakaoService;
    private final UserService userService;

    @GetMapping("/kakao")
    public String kakaoCallback(@RequestParam("code") String code, Model model) {
        String accessToken;
        try {
            accessToken = kakaoService.getAccessToken(code);
        } catch (Exception e) {
            logger.error("Access Token을 가져오는 중 오류 발생: " + e.getMessage());
            return "redirect:/error";
        }
        KakaoDTO kakaoDTO;
        try {
            kakaoDTO = kakaoService.getUserInfo(accessToken);
        } catch (Exception e) {
            logger.error("사용자 정보를 가져오는 중 오류 발생: " + e.getMessage());
            return "redirect:/error";
        }

        UserDTO kakaoMember = userService.findUserByEmail(kakaoDTO.getEmail());
       
        if (kakaoMember != null) {
            // 모델을 사용하여 사용자 정보를 kakaoLogin 페이지로 전달
            model.addAttribute("username", kakaoMember.getUsername());
            model.addAttribute("password", "qlalfqjsgh");
            System.out.println("========================"+kakaoMember.getUsername());
            return "kakaoLogin"; // kakaoLogin.html 페이지로 이동
        } else {
            return "redirect:/user/agreement?email=" + kakaoDTO.getEmail();
        }
    }
}
