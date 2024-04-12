package com.exato.usermodule.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ForgotModelTest {

	 @Test
	    void testForgotModelGettersSetters() {
	        // Arrange
	        String email = "test@example.com";
	        String otpNumber = "123456";

	        // Act
	        ForgotModel forgotModel = new ForgotModel();
	        forgotModel.setEmail(email);
	        forgotModel.setOtpNumber(otpNumber);

	        // Assert
	        assertNotNull(forgotModel);
	        assertEquals(email, forgotModel.getEmail());
	        assertEquals(otpNumber, forgotModel.getOtpNumber());
	    }

}
