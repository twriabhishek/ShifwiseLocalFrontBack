package com.exato.usermodule.model;

import lombok.Data;

@Data
public class VerifyOTPModel {

	private String otpNumber;

	private String userEmail;
	
	private String name;

}
