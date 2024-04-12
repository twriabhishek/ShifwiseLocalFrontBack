/**package com.exato.usermodule.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

@ExtendWith(MockitoExtension.class)
class ForgotPasswordControllerTest {

	 @Mock
	    private UserRepository userRepository;

	    @Mock
	    private ForgotPasswordService forgotPasswordService;

	    @Mock
	    private CheckTokenValidOrNot checkTokenValidOrNot;

	    @InjectMocks
	    private ForgotPasswordController forgotPasswordController;
	    

	    @Test
	    void testHomeuser() {
	        // Arrange

	        // Act
	        ResponseEntity<String> response = forgotPasswordController.homeuser();

	        // Assert
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals("Forgotpassword level access", response.getBody());
	    }
	    
	    @Test
	    void testOpenEmailForm() {
	        // Arrange
	    	ForgotModel forgotModel = mock(ForgotModel.class);
	    	when(forgotModel.getEmail()).thenReturn("test@example.com");

	        HttpServletRequest request = mock(HttpServletRequest.class);
	        User mockUser = mock(User.class); // Create a mock User
	        when(userRepository.findByEmail(forgotModel.getEmail())).thenReturn(Optional.of(mockUser));
	        when(forgotPasswordService.sendResetPasswordEmail(forgotModel,request))
	                .thenReturn(ResponseEntity.ok().build());

	        // Act
	        ResponseEntity<SuccessResponse> responseEntity = forgotPasswordController.openEmailForm(forgotModel, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("OTP has been send to email successfully .." + forgotModel.getEmail(), responseEntity.getBody().getMessage());
	    }
	    
	    @Test
	    void testOpenEmailFormUserNotFound() {
	        // Arrange
	      	        ForgotModel forgotModel = mock(ForgotModel.class);
	    	when(forgotModel.getEmail()).thenReturn("nonexistent@example.com");
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(userRepository.findByEmail(forgotModel.getEmail())).thenThrow(new RuntimeException("Simulated exception"));

	        try {
	            // Act
	            userRepository.findByEmail(forgotModel.getEmail());

	        } catch (RuntimeException exception) {
	  	            assertNotNull(exception);
	       	        }
	    }
	    
	    @Test
	    void testOpenEmailFormWithEmailNotFound() {
	        // Arrange
	        ForgotModel forgotModel = new ForgotModel();
	        forgotModel.setEmail("nonexistent@example.com");

	        when(userRepository.findByEmail(forgotModel.getEmail())).thenReturn(Optional.empty());

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> forgotPasswordController.openEmailForm(forgotModel, mock(HttpServletRequest.class)));

	        assertEquals("Email does not exist !!", exception.getMessage());
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

	        // Verify that the service methods were not invoked
	      //  verify(forgotPasswordService, never()).sendResetPasswordEmail(any(), any());
	    }
	    
	    @Test
	    void testForgotPasswordWithInternalError() {
	        // Arrange
	        ForgotModel forgotModel = new ForgotModel();
	        forgotModel.setEmail("test@example.com");
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the repository response
	        when(userRepository.findByEmail(forgotModel.getEmail())).thenReturn(Optional.of(new User()));

	        // Mocking the service response with a general Exception
	        when(forgotPasswordService.sendResetPasswordEmail(forgotModel, request))
	                .thenThrow(new RuntimeException("Internal Server Error"));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> forgotPasswordController.openEmailForm(forgotModel, request),
	                "Internal Server Error");

	        // Verify that the repository method was called
	    //    verify(userRepository, times(1)).findByEmail(forgotModel.getEmail());
	        // Verify that the service method was called
	    //    verify(forgotPasswordService, times(1)).sendResetPasswordEmail(forgotModel, request);
	    }
	    
	    @Test
	    void testVerifyOtpSuccess() {
	        // Arrange
	        VerifyOTPModel verifyOTPModel = mock(VerifyOTPModel.class);
	        verifyOTPModel.setUserEmail("test@example.com");
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(forgotPasswordService.verifyOtp(verifyOTPModel)).thenReturn(ResponseEntity.ok().build());

	        // Act
	        ResponseEntity<SuccessResponse> responseEntity = forgotPasswordController.verifyOtp(verifyOTPModel, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("OTP successfully verified", responseEntity.getBody().getMessage());
	    }
	    
	    
	    
	    @Test
	    void testVerifyOtpFailure() {
	        // Arrange
	        VerifyOTPModel verifyOTPModel = new VerifyOTPModel();
	        verifyOTPModel.setUserEmail("test@example.com");
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(forgotPasswordService.verifyOtp(verifyOTPModel)).thenReturn(ResponseEntity.badRequest().build());

	        // Act
	        ResponseEntity<SuccessResponse> responseEntity = forgotPasswordController.verifyOtp(verifyOTPModel, request);

	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	        assertEquals("Invalid OTP", responseEntity.getBody().getMessage());
	    }
	    
	    @Test
	    void testVerifyOtpWithCustomException() {
	        // Arrange
	        VerifyOTPModel verifyOTPModel = new VerifyOTPModel();
	        verifyOTPModel.setUserEmail("test@example.com");

	        // Mocking the service response with a CustomException
	        when(forgotPasswordService.verifyOtp(verifyOTPModel)).thenThrow(new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST));

	        // Act & Assert
	        assertThrows(CustomException.class, () -> forgotPasswordController.verifyOtp(verifyOTPModel, mock(HttpServletRequest.class)),
	                "Invalid OTP");

	        // Verify that the service method was called
	     //   verify(forgotPasswordService, times(1)).verifyOtp(verifyOTPModel);
	    }
	    
	    @Test
	    void testVerifyOtpWithInternalError() {
	        // Arrange
	        VerifyOTPModel verifyOTPModel = new VerifyOTPModel();
	        verifyOTPModel.setUserEmail("test@example.com");

	        // Mocking the service response with a general Exception
	        when(forgotPasswordService.verifyOtp(verifyOTPModel)).thenThrow(new RuntimeException("Internal Server Error"));

	        // Act & Assert
	        assertThrows(CustomException.class, () -> forgotPasswordController.verifyOtp(verifyOTPModel, mock(HttpServletRequest.class)),
	                "Internal Server Error");

	        // Verify that the service method was called
	      //  verify(forgotPasswordService, times(1)).verifyOtp(verifyOTPModel);
	    }
	    
	    @Test
	    void testChangePasswordSuccess() {
	        // Arrange
	        NewPasswordModel newPasswordModel = mock(NewPasswordModel.class);
	        String email = "test@example.com";
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(forgotPasswordService.changePassword(newPasswordModel, email)).thenReturn(ResponseEntity.ok().build());

	        // Act
	        ResponseEntity<SuccessResponse> responseEntity = forgotPasswordController.changePassword(newPasswordModel, email, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("Password successfully changed", responseEntity.getBody().getMessage());
	    }
	    
	    @Test
	    void testChangePasswordFailure() {
	        // Arrange
	        NewPasswordModel newPasswordModel = new NewPasswordModel();
	        String email = "test@example.com";
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(forgotPasswordService.changePassword(newPasswordModel, email)).thenReturn(ResponseEntity.badRequest().build());

	        // Act
	        ResponseEntity<SuccessResponse> responseEntity = forgotPasswordController.changePassword(newPasswordModel, email, request);

	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	        assertEquals("Invalid request", responseEntity.getBody().getMessage());
	    }
	    
	    @Test
	    void testChangePasswordWithCustomException() {
	        // Arrange
	        NewPasswordModel newPasswordModel = new NewPasswordModel();
	        String email = "test@example.com";

	        // Mocking the service response with a CustomException
	        when(forgotPasswordService.changePassword(newPasswordModel, email))
	                .thenThrow(new CustomException("Invalid request", HttpStatus.BAD_REQUEST));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> forgotPasswordController.changePassword(newPasswordModel, email, mock(HttpServletRequest.class)),
	                "Invalid request");

	        // Verify that the service method was called
	      //  verify(forgotPasswordService, times(1)).changePassword(newPasswordModel, email);
	    }
	    
	    @Test
	    void testChangePasswordWithInternalError() {
	        // Arrange
	        NewPasswordModel newPasswordModel = new NewPasswordModel();
	        String email = "test@example.com";

	        // Mocking the service response with a general Exception
	        when(forgotPasswordService.changePassword(newPasswordModel, email))
	                .thenThrow(new RuntimeException("Internal Server Error"));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> forgotPasswordController.changePassword(newPasswordModel, email, mock(HttpServletRequest.class)),
	                "Internal Server Error");

	    }
	    
	    @Test
	    void testResetPassword() {
	        // Arrange
	        NewPasswordModel newPasswordModel = mock(NewPasswordModel.class);
	        String email = "test@example.com";
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
	        when(forgotPasswordService.resetPassword(newPasswordModel, email)).thenReturn(ResponseEntity.ok().build());

	        // Act
	        ResponseEntity<SuccessResponse> responseEntity = forgotPasswordController.resetPassword(newPasswordModel, email, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("Password reset successfully", responseEntity.getBody().getMessage());
	    }
	    
	    @Test
	    void testResetPasswordFailure() {
	        // Arrange
	        NewPasswordModel newPasswordModel = mock(NewPasswordModel.class);
	        String email = "test@example.com";
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
	        when(forgotPasswordService.resetPassword(newPasswordModel, email)).thenReturn(ResponseEntity.badRequest().build());

	        // Act
	        ResponseEntity<SuccessResponse> responseEntity = forgotPasswordController.resetPassword(newPasswordModel, email, request);

	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	        assertEquals("Invalid request", responseEntity.getBody().getMessage());
	    }
	    
	    @Test
	    void testResetPasswordWithCustomException() {
	        // Arrange
	        NewPasswordModel newPasswordModel = new NewPasswordModel();
	        String email = "test@example.com";
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the token validation to throw a CustomException
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request))
	                .thenThrow(new CustomException("Token is Expired/invalid or not present in header", HttpStatus.BAD_REQUEST));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> forgotPasswordController.resetPassword(newPasswordModel, email, request),
	                "Token is Expired/invalid or not present in header");

	    
	    }
	    
	    @Test
	    void testResetPasswordWithInternalError() {
	        // Arrange
	        NewPasswordModel newPasswordModel = new NewPasswordModel();
	        String email = "test@example.com";
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the token validation to return true
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
	        // Mocking the service response with a general Exception
	        when(forgotPasswordService.resetPassword(newPasswordModel, email))
	                .thenThrow(new RuntimeException("Internal Server Error"));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> forgotPasswordController.resetPassword(newPasswordModel, email, request),
	                "Internal Server Error");

	        // Verify that the token validation method was called
	      //  verify(checkTokenValidOrNot, times(1)).checkTokenValidOrNot(request);
	        // Verify that the service method was called
	     //   verify(forgotPasswordService, times(1)).resetPassword(newPasswordModel, email);
	    }

}
**/