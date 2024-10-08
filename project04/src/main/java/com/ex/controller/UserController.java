package com.ex.controller;

import com.ex.data.QuestionDTO;
import com.ex.data.UserDTO;
import com.ex.entity.UserEntity;
import com.ex.service.UserService;
import com.ex.service.ProductService;
import com.ex.service.QuestionService;
import com.ex.service.ReviewService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/user/*")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final ReviewService reviewService;
	private final ProductService productService;
	private final QuestionService questionService;
	
	@GetMapping("login")
	public String loginPage() {
		return "login";
	}

	@GetMapping("register")
	public String registerPage(@RequestParam(value = "email", required = false) String email, Model model) {
	    if (email != null && !email.isEmpty()) {
	        model.addAttribute("email", email);
	        model.addAttribute("password", "qlalfqjsgh");
	    }
	    return "register";
	}

	@PostMapping("register")
	public String registerUser( UserDTO userDTO) {
		this.userService.registerUser(userDTO);
		return "redirect:/user/login";
	}

	@GetMapping("agreement")
	public String agreement(@RequestParam(value = "email", required = false) String email, Model model) {
	    if (email != null && !email.isEmpty()) {
	        model.addAttribute("email", email);
	        model.addAttribute("password", "qlalfqjsgh");
	    }
	    return "agreement";
	}

	
	@GetMapping("findid")
	public String findid() {
		return "findid";
	}

	@PostMapping("findidpro")
    public String findidpro(@RequestParam("realName") String realName, @RequestParam("email") String email, @RequestParam("phone") String phone, Model model) {
        String result = userService.findUsername(realName, email, phone);
        if (result.equals("존재하지 않는 아이디입니다.")) {
            model.addAttribute("error", result);
        } else {
            model.addAttribute("username", result);
        }
        return "findidpro";
    }
	
	@GetMapping("findpw")
	public String findpw() {
		return "findpw";
	}
	@PostMapping("findpwpro")
    public String findPassword(@RequestParam("username") String username, @RequestParam("realName") String realName, @RequestParam("email") String email, Model model) {
        try {
            userService.verifyUser(username, realName, email);
            model.addAttribute("username", username);
            return "findpwpro";
        } catch (RuntimeException e) {
            return "redirect:/user/findpw?error=true";
        }
    }
	@PostMapping("setnewpassword")
    public String setNewPassword(@RequestParam("username") String username, @RequestParam("newPassword") String newPassword) {
        userService.updatePassword(username, newPassword);
        return "redirect:/user/login";
    }
	// entityToDTO = 엔티티를 DTO형식으로 리턴해서 받는 방식
	   @GetMapping("userinfo")
	   public String userinfo(Principal principal , Model model) {
	      UserDTO userDTO = UserDTO.entityToDTO(this.userService.findByUserName(principal.getName())); 
	      model.addAttribute("userDTO", userDTO);
	      return "userinfo";
	   }
	   
	   @GetMapping("mypage")
	   @PreAuthorize("isAuthenticated()")
	   public String myPage(Principal principal, Model model) {
	       UserEntity user = userService.findByUserName(principal.getName());
	       model.addAttribute("user", user);
	       if(!user.getReview().isEmpty()) {
	           model.addAttribute("myReview", reviewService.myPageReview(principal.getName()));        
	       }
	       if(!user.getWishList().isEmpty()) {
	           model.addAttribute("myWishList", productService.myWishList((principal.getName())));
	       }
	       if(!user.getDelivery().isEmpty()) {
	           model.addAttribute("myOrder", productService.myOrder((principal.getName())));
	       }
	       
	       // 문의 목록 추가
	       List<QuestionDTO> questionList = questionService.findAllByUsername(principal.getName());
	       model.addAttribute("questionList", questionList);

	       return "mypage";
	   }
	   
	   // <-- 제성 회원정보 변경 -->
	   @GetMapping("userUpdateForm") // 회원정보 변경 폼 요청
	    @PreAuthorize("isAuthenticated()") // 인증된 사용자만 접근 가능
	    public String showUserUpdateForm(Principal principal, Model model) {
	        UserEntity user = userService.findByUserName(principal.getName()); // 사용자 정보 가져오기
	        model.addAttribute("user", user); // 사용자 정보 설정
	        return "userUpdateForm"; // userUpdateForm.html 뷰 반환
	    }

	    @PostMapping("update") // 회원정보 변경 처리
	    @PreAuthorize("isAuthenticated()") // 인증된 사용자만 접근 가능
	    public String updateUser(UserDTO userDTO, Principal principal) {
	        userService.updateUser(principal.getName(), userDTO); // 회원정보 변경 로직 실행
	        return "redirect:/user/mypage"; // 변경 후 마이페이지로 리다이렉트
	    }
	    
	    @GetMapping("allReview")
	    @PreAuthorize("isAuthenticated()") // 인증된 사용자만 접근 가능
	    public String myAllReview(Principal pricipal,Model model) {
	    	model.addAttribute("user",UserDTO.entityToDTO(userService.findByUserName(pricipal.getName())));
	    	return "reviewList";
	    }
	    
        @PostMapping("deleteAccount")
        @PreAuthorize("isAuthenticated()") // 인증된 사용자만 접근 가능
        public String deleteAccount(HttpServletRequest request, HttpServletResponse response, Principal principal) {
            userService.deleteUser(principal.getName());

            // 세션 무효화
            request.getSession().invalidate();

            // 쿠키 삭제
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            return "redirect:/user/login?accountDeleted=true";
        }
}
