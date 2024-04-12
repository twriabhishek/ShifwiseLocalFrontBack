package com.exato.usermodule.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VerifyOTPModelTest {

	 @Test
	    void testVerifyOTPModelGettersAndSetters() {
	        // Arrange
	        VerifyOTPModel verifyOTPModel = new VerifyOTPModel();
	        String otpNumber = "123456";
	        String userEmail = "test@example.com";
	        String name = "John Doe";

	        // Act
	        verifyOTPModel.setOtpNumber(otpNumber);
	        verifyOTPModel.setUserEmail(userEmail);
	        verifyOTPModel.setName(name);

	        // Assert
	        assertEquals(otpNumber, verifyOTPModel.getOtpNumber());
	        assertEquals(userEmail, verifyOTPModel.getUserEmail());
	        assertEquals(name, verifyOTPModel.getName());
	    }

}
