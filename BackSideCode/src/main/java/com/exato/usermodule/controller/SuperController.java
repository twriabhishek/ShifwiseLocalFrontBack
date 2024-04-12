package com.exato.usermodule.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/super")
@CrossOrigin
public class SuperController {
	
	private final CheckTokenValidOrNot checkTokenValidOrNot;
	
	public SuperController(CheckTokenValidOrNot checkTokenValidOrNot) {
		this.checkTokenValidOrNot = checkTokenValidOrNot;
				}

	@GetMapping("/")
	public String superAdmin(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			 return "Token is invalid or not present in header";
		}
		return "Super user level access";
	}
}
