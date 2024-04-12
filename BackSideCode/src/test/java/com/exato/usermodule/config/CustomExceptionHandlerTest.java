package com.exato.usermodule.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

class CustomExceptionHandlerTest {

	 @Test
	    void testHandleCustomException() {
	        // Arrange
	        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler();
	        CustomException customException = new CustomException("Custom exception message", HttpStatus.BAD_REQUEST);
	        WebRequest webRequest = mock(WebRequest.class);

	        // Act
	        ResponseEntity<ErrorDetails> responseEntity = customExceptionHandler.handleCustomException(customException, webRequest);

	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	        assertEquals("Custom exception message", responseEntity.getBody().getMessage());
	    }

	    @Test
	    void testHandleSuccessException() {
	        // Arrange
	        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler();
	        SuccessException successException = new SuccessException("Success message");
	        WebRequest webRequest = mock(WebRequest.class);

	        // Act
	        ResponseEntity<SuccessResponse> responseEntity = customExceptionHandler.handleSuccessException(successException, webRequest);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("Success message", responseEntity.getBody().getMessage());
	    }

}
