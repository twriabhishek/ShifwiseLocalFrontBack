package com.exato.usermodule.config;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class LogoutHandlerServiceTest {

	 @InjectMocks
	    private LogoutHandlerService logoutHandlerService;

	    @Mock
	    private HttpServletRequest request;

	    @Mock
	    private HttpServletResponse response;

	    @Mock
	    private Authentication authentication;
	    
	    @BeforeEach
	    void setUp() {
	        // Initialize mocks
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    void testLogoutWithValidAuthorizationHeader() {
	        // Arrange
	        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");

	        // Act
	        logoutHandlerService.logout(request, response, authentication);

	        // Assert
	        // Add your assertions based on the behavior you expect
	        verify(response, times(0)).setStatus(Mockito.anyInt());
	    }
	    
	    @Test
	    void testLogoutWithInvalidAuthorizationHeader() {
	        // Arrange
	        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

	        // Act
	        logoutHandlerService.logout(request, response, authentication);

	        // Assert
	        verify(response, times(1)).setStatus(400);
	    }
}
