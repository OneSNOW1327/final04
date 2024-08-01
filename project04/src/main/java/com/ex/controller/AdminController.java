package com.ex.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/*")
public class AdminController {
	
	@GetMapping("main")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String adminMain(){
		return "adminMain";
	}

	
}
