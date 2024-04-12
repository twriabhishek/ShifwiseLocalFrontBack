package com.exato.usermodule.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CustomExceptionTest {

	 @Test
	    void testCustomException() {
	        // Arrange
	        String message = "Test exception message";
	        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

	        // Act
	        CustomException customException = new CustomException(message, expectedStatus);

	        // Assert
	        assertEquals(message, customException.getMessage());
	        assertEquals(expectedStatus, customException.getStatus());
	    }
	 
	 @Test
	    void testCustomExceptionGetter() {
	        // Arrange
	        CustomException customException = new CustomException("Test message", HttpStatus.BAD_REQUEST);

	        // Act
	        HttpStatus actualStatus = customException.getStatus();

	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, actualStatus);
	    }

}
