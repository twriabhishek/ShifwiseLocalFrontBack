package com.exato.usermodule.config;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


class AccessDeniedHandlerJwtTest {

	 @Test
	    void testHandle() throws IOException, ServletException {
	        // Arrange
	        AccessDeniedHandlerJwt accessDeniedHandlerJwt = new AccessDeniedHandlerJwt();
	        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
	        HttpServletResponse response = new MockHttpServletResponse();
	        AccessDeniedException exception = new AccessDeniedException("You don't have required role");

	        // Act
	        accessDeniedHandlerJwt.handle(request, response, exception);

	        // Assert
	        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus(), "Status code should be 403");

	        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType(), "Content type should be JSON");

	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, Object> responseBody = objectMapper.readValue(((MockHttpServletResponse) response).getContentAsString(), Map.class);

	        assertEquals(HttpServletResponse.SC_FORBIDDEN, responseBody.get("code"), "Response code should be 403");
	        assertEquals("You don't have required role to perform this action.", responseBody.get("payload"),
	                "Response payload should match");
	    }

   

}
