package com.exato.usermodule.service;

import org.springframework.http.ResponseEntity;

import com.exato.usermodule.model.ForgotModel;
import com.exato.usermodule.model.NewPasswordModel;
import com.exato.usermodule.model.VerifyOTPModel;

import jakarta.servlet.http.HttpServletRequest;

public interface ForgotPasswordService {

	ResponseEntity<String> sendResetPasswordEmail(ForgotModel forgotModel,HttpServletRequest request);
	
    ResponseEntity<VerifyOTPModel> verifyOtp(VerifyOTPModel verifyOTPModel);
    
    ResponseEntity<String> changePassword(NewPasswordModel newPasswordModel, String email);
    
    ResponseEntity<String> resetPassword(NewPasswordModel newPasswordModel, String email);
}
