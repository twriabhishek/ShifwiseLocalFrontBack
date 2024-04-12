package com.exato.usermodule.config;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetails {
	
	private Date timestamp;
	private String message;
	private String details;
	private int statusCode;

	 public ErrorDetails(Date timestamp, String message, String details, HttpStatus status) {
	        this.timestamp = timestamp;
	        this.message = message;
	        this.details = details;
	        this.statusCode = status.value();
	    }
	}
