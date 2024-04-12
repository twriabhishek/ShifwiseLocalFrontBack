package com.exato.usermodule.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorDetailsTest {

	 @Test
	    void testErrorDetailsConstructor() {
	        // Arrange
	        Date timestamp = new Date();
	        String message = "Test Error Message";
	        String details = "Test Error Details";
	        HttpStatus status = HttpStatus.BAD_REQUEST;

	        // Act
	        ErrorDetails errorDetails = new ErrorDetails(timestamp, message, details, status);

	        // Assert
	        assertNotNull(errorDetails);
	        assertEquals(timestamp, errorDetails.getTimestamp());
	        assertEquals(message, errorDetails.getMessage());
	        assertEquals(details, errorDetails.getDetails());
	        assertEquals(status.value(), errorDetails.getStatusCode());
	    }
	 
	 @Test
	    void testErrorDetailsGetterSetter() {
	        // Arrange
	        Date timestamp = new Date();
	        String message = "Test Error Message";
	        String details = "Test Error Details";
	        HttpStatus status = HttpStatus.BAD_REQUEST;

	        // Act
	        ErrorDetails errorDetails = new ErrorDetails(timestamp, details, details, status);
	        errorDetails.setTimestamp(timestamp);
	        errorDetails.setMessage(message);
	        errorDetails.setDetails(details);
	        errorDetails.setStatusCode(status.value());

	        // Assert
	        assertNotNull(errorDetails);
	        assertEquals(timestamp, errorDetails.getTimestamp());
	        assertEquals(message, errorDetails.getMessage());
	        assertEquals(details, errorDetails.getDetails());
	        assertEquals(status.value(), errorDetails.getStatusCode());
	    }

}
