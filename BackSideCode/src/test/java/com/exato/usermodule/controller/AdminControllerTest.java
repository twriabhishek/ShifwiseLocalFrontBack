package com.exato.usermodule.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

	    @Mock
	    private CheckTokenValidOrNot checkTokenValidOrNot;

	    @InjectMocks
	    private AdminController adminController;

	    @Test
	    void testAdminWithValidToken() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class); // create a mock HttpServletRequest with a valid token
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Act
	        String result = adminController.admin(request);

	        // Assert
	        assertEquals("admin  level access", result);
	    }

	    @Test
	    void testAdminWithInvalidToken() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class); // create a mock HttpServletRequest with an invalid token
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act
	        String result = adminController.admin(request);

	        // Assert
	        assertEquals("Token is invalid or not present in header", result);
	    }

}
