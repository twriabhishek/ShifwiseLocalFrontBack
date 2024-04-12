package com.exato.usermodule.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

class InvalidUserAuthEntryPointTest {

	 @Test
	    void testCommence() throws Exception {
	        // Arrange
	        InvalidUserAuthEntryPoint entryPoint = new InvalidUserAuthEntryPoint();
	        MockHttpServletRequest request = new MockHttpServletRequest();
	        MockHttpServletResponse response = new MockHttpServletResponse();
	        AuthenticationException authException = new AuthenticationException("Invalid credentials") {}; // You can customize the exception message

	        // Act
	        entryPoint.commence(request, response, authException);

	        // Assert
	        assertEquals(401, response.getStatus(), "Status code should be 401");
	        assertTrue(response.getContentAsString().contains("You need to login first"),
	                "Response body should contain expected message");
	    }

}
