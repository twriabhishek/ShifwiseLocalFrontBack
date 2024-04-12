package com.exato.usermodule.service;

public interface OTPEmailService {
    boolean sendOTPEmail(String userEmail, String otp, String siteURL);
}

