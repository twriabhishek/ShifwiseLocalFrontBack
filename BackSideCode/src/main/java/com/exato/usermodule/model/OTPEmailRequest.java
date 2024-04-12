package com.exato.usermodule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTPEmailRequest {
	
	private String toAddress;
    private String otp;
    private String urlLink;

}
