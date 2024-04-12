package com.exato.usermodule.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SuccessResponseTest {

	 @Test
	    void testSuccessResponse() {
	        // Arrange
	        String expectedMessage = "Success Message";
	        SuccessResponse successResponse = new SuccessResponse(expectedMessage);

	        // Act
	        successResponse.setMessage(expectedMessage);

	        // Assert
	        assertEquals(expectedMessage, successResponse.getMessage(), "Message should match");
	    }

}
