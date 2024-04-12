package com.exato.usermodule.config;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	// Exception handling for CustomException (error cases)
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorDetails> handleCustomException(CustomException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false), ex.getStatus());
		return new ResponseEntity<>(errorDetails, ex.getStatus());
	}

	// Handle other exceptions (success cases)
	@ExceptionHandler(SuccessException.class)
	protected ResponseEntity<SuccessResponse> handleSuccessException(SuccessException ex, WebRequest request) {
	    SuccessResponse successResponse = new SuccessResponse(ex.getMessage());
	    return new ResponseEntity<>(successResponse, HttpStatus.OK);
	}

}
