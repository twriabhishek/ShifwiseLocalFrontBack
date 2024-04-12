package com.exato.usermodule.service;

public interface EmailService {
    boolean sendResetPasswordEmail(String toAddress, String resetLink, String token);
}

