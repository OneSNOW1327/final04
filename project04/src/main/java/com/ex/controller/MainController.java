package com.ex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
    @GetMapping("/main")
    public String mainPage() {
    	return "main";
    }
    
    @GetMapping("/")
    public String main() {
    	return "redirect:/main";
    }
    
}
