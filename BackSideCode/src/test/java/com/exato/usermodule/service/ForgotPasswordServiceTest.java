/**package com.exato.usermodule.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.exato.usermodule.model.ForgotModel;
import com.exato.usermodule.model.NewPasswordModel;
import com.exato.usermodule.model.VerifyOTPModel;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
class ForgotPasswordServiceTest {

	 @Test
	    void testSendResetPasswordEmail() {
	        // Arrange
	        ForgotPasswordService forgotPasswordServiceMock = mock(ForgotPasswordService.class);
	        ForgotModel forgotModelMock = mock(ForgotModel.class);
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Act
	        forgotPasswordServiceMock.sendResetPasswordEmail(forgotModelMock, request);

	        // Assert
	        // Verify that the method was called with the expected parameters
	        verify(forgotPasswordServiceMock, times(1)).sendResetPasswordEmail(forgotModelMock,request);
	        
	    }
	 
	 @Test
	    void testVerifyOtp() {
	        // Arrange
	        ForgotPasswordService forgotPasswordServiceMock = mock(ForgotPasswordService.class);
	        VerifyOTPModel verifyOTPModel = mock(VerifyOTPModel.class);

	        // Act
            forgotPasswordServiceMock.verifyOtp(verifyOTPModel);

	        // Assert
	        // Verify that the method was called with the expected parameters
	        verify(forgotPasswordServiceMock, times(1)).verifyOtp(verifyOTPModel);
	       
	    }

	    @Test
	    void testChangePassword() {
	        // Arrange
	        ForgotPasswordService forgotPasswordServiceMock = mock(ForgotPasswordService.class);
	        NewPasswordModel newPasswordModel = mock(NewPasswordModel.class);
	        String email = "test@example.com";

	        // Act
	      forgotPasswordServiceMock.changePassword(newPasswordModel, email);

	        // Assert
	        // Verify that the method was called with the expected parameters
	        verify(forgotPasswordServiceMock, times(1)).changePassword(newPasswordModel,email);
	      
	    }

	    @Test
	    void testResetPassword() {
	        // Arrange
	        ForgotPasswordService forgotPasswordServiceMock = mock(ForgotPasswordService.class);
	        NewPasswordModel newPasswordModel = mock(NewPasswordModel.class);
	        String email = "test@example.com";

	        // Act
	        forgotPasswordServiceMock.resetPassword(newPasswordModel, email);

	        // Assert
	        // Verify that the method was called with the expected parameters
	        verify(forgotPasswordServiceMock, times(1)).resetPassword(newPasswordModel, email);
	      
	    }

}
**/