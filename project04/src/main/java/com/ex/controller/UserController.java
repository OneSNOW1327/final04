package com.ex.controller;

import com.ex.dto.UserDTO;
import com.ex.service.UserService;

import lombok.RequiredArgsConstructor;


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
		model.addAttribute("result", result);
		return "findidpro";
	}

}
