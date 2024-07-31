package com.ex.controller;

import com.ex.data.UserDTO;
import com.ex.service.UserService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/*")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;    

	@GetMapping("login")
	public String loginPage() {
		return "login";
	}

	@GetMapping("register")
	public String registerPage() {
		return "register";
	}
	@PostMapping("register")
	public String registerUser( UserDTO userDTO) {
		this.userService.registerUser(userDTO);
		return "redirect:/";
	}

	@GetMapping("agreement")
	public String agreement() {
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
}
