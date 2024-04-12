package com.exato.usermodule.serviceimpl;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.exato.usermodule.service.OTPEmailService;
import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.ClientInfo;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.model.ForgotModel;
import com.exato.usermodule.model.NewPasswordModel;
import com.exato.usermodule.model.VerifyOTPModel;
import com.exato.usermodule.repository.ClientInfoRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.service.AuditLogService;
import com.exato.usermodule.service.ForgotPasswordService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
	
	private final UserRepository userRepository;
	private final ClientInfoRepository clientRepository;
	private final PasswordEncoder passwordEncoder;
	private final OTPEmailService otpEmailService;
	private final AuditLogService auditLogService;
	
	public ForgotPasswordServiceImpl(UserRepository userRepository,ClientInfoRepository clientRepository,PasswordEncoder passwordEncoder,OTPEmailService otpEmailService,AuditLogService auditLogService) {
		this.userRepository = userRepository;
		this.clientRepository = clientRepository;
		this.passwordEncoder = passwordEncoder;
		this.otpEmailService = otpEmailService;
		this.auditLogService = auditLogService;
	}

	@Override
	public ResponseEntity<String> sendResetPasswordEmail(ForgotModel forgotModel, HttpServletRequest request) {
		try {
			User findByEmail = userRepository.findByEmail(forgotModel.getEmail()).orElse(null);
		
			if (findByEmail != null) {
				int randomNumber = ThreadLocalRandom.current().nextInt(1000, 10000);
				String otp = String.valueOf(randomNumber);

				boolean flag = otpEmailService.sendOTPEmail(forgotModel.getEmail(), otp, getSiteURL(request));

				if (flag) {
					findByEmail.setOtpNumber(otp);
					userRepository.save(findByEmail);
					log.info("OTP sent successfully for email: {}", forgotModel.getEmail());
					return ResponseEntity.status(HttpStatus.OK).body("OTP sent successfully");
				} else {
					log.error("Failed to send OTP for email: {}", forgotModel.getEmail());
				throw new CustomException("Failed to send OTP", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				log.error("Email is not registered: {}", forgotModel.getEmail());
				throw new CustomException("This email is not registered", HttpStatus.NOT_FOUND);
			}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while sending OTP for email: {}", forgotModel.getEmail(), e);
			throw new CustomException(" Error occurred while sending OTP for email: {}" + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	// for making url
	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	@Override
	public ResponseEntity<VerifyOTPModel> verifyOtp(VerifyOTPModel verifyOTPModel) {
		try {
			String email = verifyOTPModel.getUserEmail();
			User user = this.userRepository.findByEmail(email).orElse(null);
			String otpNumber = verifyOTPModel.getOtpNumber();

			if (user == null) {
				log.error("Email not registered: {}", email);
				throw new CustomException("Email not registered", HttpStatus.OK);
			}

			String myotp = user.getOtpNumber();

			if (myotp.equals(otpNumber)) {
				String redirectUrl = "/forgot/changePassword?email=" + email;
				user.setOtpNumber(null); // enter the otp as null after verifying
				userRepository.save(user);
				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.LOCATION, redirectUrl);
				log.info("OTP verification successful for email: {}", email);
				return ResponseEntity.ok().headers(headers).build();
			} else {
				log.error("Wrong OTP for email: {}", email);
				throw new CustomException("Wrong OTP ", HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error occurred while verifying OTP for email: {}", verifyOTPModel.getUserEmail(), e);
			throw new CustomException(" Error occurred while verifying OTP for email: {}" + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> changePassword(NewPasswordModel newPasswordModel, String email) {
		try {
			User user = userRepository.findByEmail(email).orElse(null);

			if (user == null) {
				log.error("Email not found: {}", email);
				throw new CustomException("Email not found", HttpStatus.OK);
			}

			user.setPassword(passwordEncoder.encode(newPasswordModel.getNewPassword()));
			userRepository.save(user);
			log.info("Password changed successfully for email: {}", email);
			auditLogService.createAuditLog(email, "password changed ", user.getClientId());
			return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
		} catch (Exception e) {
			log.error("Error occurred while changing password for email: {}", email, e);
			throw new CustomException("Error occurred while changing password for email: {} " + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> resetPassword(NewPasswordModel newPasswordModel, String email) {
		try {
			User user = userRepository.findByEmail(email).orElse(null);
			ClientInfo client = clientRepository.findByEmail(email).orElse(null);
			if (client != null) {
				client.setPassword(passwordEncoder.encode(newPasswordModel.getNewPassword()));
				user.setClientId(client.getClientId());
				client.setActive(true);
				clientRepository.save(client);
			}
			if (user != null) {
				user.setPassword(passwordEncoder.encode(newPasswordModel.getNewPassword()));
				user.setActive(true);
				userRepository.save(user);
				log.info("Password reset successfully for email: {}", email);
				auditLogService.createAuditLog(email, "password reset ", user.getClientId());
				return ResponseEntity.status(HttpStatus.OK).body("Password reset successfully");
			} else {
				log.error("Email not found: {}", email);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
			}
		} catch (Exception e) {
			log.error("Error occurred while resetting password for email: {}", email, e);
			throw new CustomException("Error occurred while resetting password for email: {}" + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	

	
}
