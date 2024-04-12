package com.exato.usermodule.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {
	
	private final CheckTokenValidOrNot checkTokenValidOrNot;
	
	public AdminController(CheckTokenValidOrNot checkTokenValidOrNot) {
		this.checkTokenValidOrNot = checkTokenValidOrNot;
	}
	
		@GetMapping("/")
	public String admin(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			return "Token is invalid or not present in header";
		}
		return "admin  level access";
		
	}

}
