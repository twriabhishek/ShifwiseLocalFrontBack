package com.exato.usermodule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RootController {

	@GetMapping("/")
	public String rootData() {
			return "Hello World! Testing pipeline with exato - 17";
	}

	

}
