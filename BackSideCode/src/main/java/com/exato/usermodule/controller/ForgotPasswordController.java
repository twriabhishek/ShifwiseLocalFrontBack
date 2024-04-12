package com.exato.usermodule.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.config.SuccessResponse;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.model.ForgotModel;
import com.exato.usermodule.model.NewPasswordModel;
import com.exato.usermodule.model.VerifyOTPModel;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.service.ForgotPasswordService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/forgot")
@CrossOrigin
@Slf4j
public class ForgotPasswordController {
	
	private final ForgotPasswordService forgotPasswordService;
	private final UserRepository userRepository;
	private final CheckTokenValidOrNot checkTokenValidOrNot;
	
	public ForgotPasswordController(ForgotPasswordService forgotPasswordService,UserRepository userRepository,CheckTokenValidOrNot checkTokenValidOrNot) {
		this.forgotPasswordService = forgotPasswordService;
		this.userRepository = userRepository;
		this.checkTokenValidOrNot = checkTokenValidOrNot;
	}

	@GetMapping("/")

	public ResponseEntity<String> homeuser() {

		return ResponseEntity.ok("Forgotpassword level access");

	}

	@PostMapping("/forgotpassword")
	public ResponseEntity<SuccessResponse> openEmailForm(@RequestBody ForgotModel forgotModel, HttpServletRequest request) {
		try {
			Optional<User> email = userRepository.findByEmail(forgotModel.getEmail());
			if(!email.isEmpty()) {
	        log.info("Received a request for password reset for email: {}", forgotModel.getEmail());
	        ResponseEntity<?> response = forgotPasswordService.sendResetPasswordEmail(forgotModel, request);

	        if (response.getStatusCode() == HttpStatus.OK) {
	            return ResponseEntity.ok(new SuccessResponse("OTP has been send to email successfully .." + forgotModel.getEmail() ));
	        } else {
                throw new CustomException(" Invalid request", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new CustomException("Email does not exist !!", HttpStatus.NOT_FOUND);
        }
	    } catch (CustomException e) {
	        log.error(" CustomException: " , e);
	        throw e;
	    } catch (Exception e) {
	        log.error("Error occurred while processing password reset request: " + e.getMessage(), e);
	        throw new CustomException("Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	}
	

	@PostMapping("/verifyOTP")
	public ResponseEntity<SuccessResponse> verifyOtp(@RequestBody VerifyOTPModel verifyOTPModel, HttpServletRequest request) {

		try {
			log.info("Verifying OTP for email: {}", verifyOTPModel.getUserEmail());
			ResponseEntity<?> verifyOtp = forgotPasswordService.verifyOtp(verifyOTPModel);

			if (verifyOtp.getStatusCode() == HttpStatus.OK) {
				return ResponseEntity.ok(new SuccessResponse("OTP successfully verified"));
			} else {
				return ResponseEntity.badRequest().body(new SuccessResponse("Invalid OTP"));
			}
		} catch (CustomException e) {
			log.error(" CustomException: " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while verifying OTP: " + e.getMessage(), e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/changePassword")
	public ResponseEntity<SuccessResponse> changePassword(@RequestBody NewPasswordModel newPasswordModel, @RequestParam String email,
			HttpServletRequest request) {

		 try {
		        log.info("Changing password for email: {}", email);
		        ResponseEntity<?> response = forgotPasswordService.changePassword(newPasswordModel, email);

		        if (response.getStatusCode() == HttpStatus.OK) {
		            return ResponseEntity.ok(new SuccessResponse("Password successfully changed"));
		        } else {
		            return ResponseEntity.badRequest().body(new SuccessResponse("Invalid request"));
		        }
		    } catch (CustomException e) {
		        log.error("CustomException: " + e.getMessage(), e);
		        throw e;
		    } catch (Exception e) {
		        log.error("Error occurred while changing password: " + e.getMessage(), e);
		        throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		    }

	}

	@PostMapping("/resetPassword")
	public ResponseEntity<SuccessResponse> resetPassword(@RequestBody NewPasswordModel newPasswordModel, @RequestParam String email,
			HttpServletRequest request) {
		
		  if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) { throw new
		  CustomException("Token is Expired/invalid or not present in header",
		  HttpStatus.BAD_REQUEST); }
		 
		try {
	        log.info("Resetting password for email: {}", email);
	        ResponseEntity<?> response = forgotPasswordService.resetPassword(newPasswordModel, email);

	        if (response.getStatusCode() == HttpStatus.OK) {
	            return ResponseEntity.ok(new SuccessResponse("Password reset successfully"));
	        } else {
	            return ResponseEntity.badRequest().body(new SuccessResponse("Invalid request"));
	        }
	    } catch (CustomException e) {
	        log.error("CustomException: " + e.getMessage(), e);
	      throw e;
	    } catch (Exception e) {
	        log.error("Error occurred while resetting password: " + e.getMessage(), e);
	        throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	}
}
