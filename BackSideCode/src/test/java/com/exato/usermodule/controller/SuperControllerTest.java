package com.exato.usermodule.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class SuperControllerTest {

	 @InjectMocks
	    private SuperController superController;

	    @Mock
	    private CheckTokenValidOrNot checkTokenValidOrNot;

	    @Mock
	    private HttpServletRequest request;

	    @Test
	    void testSuperAdminWithValidToken() {
	        // Arrange
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Act
	        String result = superController.superAdmin(request);

	        // Assert
	        assertEquals("Super user level access", result);
	    }

	    @Test
	    void testSuperAdminWithInvalidToken() {
	        // Arrange
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act
	        String result = superController.superAdmin(request);

	        // Assert
	        assertEquals("Token is invalid or not present in header", result);
	    }

}
