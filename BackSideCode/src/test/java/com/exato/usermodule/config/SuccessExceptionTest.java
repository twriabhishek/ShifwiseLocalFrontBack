package com.exato.usermodule.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SuccessExceptionTest {

	 @Test
	 void testSuccessException() {
	        // Arrange
	        String expectedMessage = "Test Success Message";

	        // Act
	        SuccessException successException = new SuccessException(expectedMessage);

	        // Assert
	        assertEquals(expectedMessage, successException.getMessage());
	    }

}
