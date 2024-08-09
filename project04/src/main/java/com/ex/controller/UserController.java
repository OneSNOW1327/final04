package com.ex.controller;

import com.ex.data.UserDTO;
import com.ex.entity.UserEntity;
import com.ex.service.UserService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequestMapping("/user/*")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;    
    private final PasswordEncoder passwordEncoder;
    
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
	@PostMapping("/findpwpro")
    public String findPassword(@RequestParam("username") String username, @RequestParam("realName") String realName, @RequestParam("email") String email, Model model) {
        try {
            userService.verifyUser(username, realName, email);
            model.addAttribute("username", username);
            return "findpwpro";
        } catch (RuntimeException e) {
            return "redirect:/user/findpw?error=true";
        }
    }
	@PostMapping("/setnewpassword")
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
	   
	   @GetMapping("/mypage")
	    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
	        UserEntity user = userService.findByUserName(userDetails.getUsername());
	        model.addAttribute("user", user);
	        return "mypage";
	    }
	   
	   // <-- 제성 회원정보 변경 -->
	   @GetMapping("/userUpdateForm") // 회원정보 변경 폼 요청
	    @PreAuthorize("isAuthenticated()") // 인증된 사용자만 접근 가능
	    public String showUserUpdateForm(Principal principal, Model model) {
	        UserEntity user = userService.findByUserName(principal.getName()); // 사용자 정보 가져오기
	        model.addAttribute("user", user); // 사용자 정보 설정
	        return "userUpdateForm"; // userUpdateForm.html 뷰 반환
	    }

	    @PostMapping("/update") // 회원정보 변경 처리
	    @PreAuthorize("isAuthenticated()") // 인증된 사용자만 접근 가능
	    public String updateUser(UserDTO userDTO, Principal principal) {
	        userService.updateUser(principal.getName(), userDTO); // 회원정보 변경 로직 실행
	        return "redirect:/user/mypage"; // 변경 후 마이페이지로 리다이렉트
	    }
}
