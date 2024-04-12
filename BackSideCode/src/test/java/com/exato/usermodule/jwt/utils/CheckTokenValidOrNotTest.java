package com.exato.usermodule.jwt.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.repository.RevokedTokenRepository;

import jakarta.servlet.http.HttpServletRequest;

class CheckTokenValidOrNotTest {

	@Mock
    private JwtUtils jwtUtils;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @InjectMocks
    private CheckTokenValidOrNot checkTokenValidOrNot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckTokenValidOrNot_ValidToken() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtils.isValidToken("validToken")).thenReturn(true);

        // Act
        boolean result = checkTokenValidOrNot.checkTokenValidOrNot(request);

        // Assert
        assertTrue(result);
        // Verify that isValidToken is called with the correct token
        verify(jwtUtils, times(1)).isValidToken("validToken");
    }
    
    @Test
    void testCheckTokenValidOrNot_InvalidToken() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtUtils.isValidToken("invalidToken")).thenReturn(false);

        // Act
        boolean result = checkTokenValidOrNot.checkTokenValidOrNot(request);

        // Assert
        assertFalse(result);
        // Verify that isValidToken is called with the correct token
        verify(jwtUtils, times(1)).isValidToken("invalidToken");
    }
    
    @Test
    void testCheckTokenValidOrNot_TokenExpired() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(jwtUtils.isValidToken("expiredToken")).thenReturn(true);
        when(jwtUtils.getUsernameFromToken("expiredToken")).thenReturn("expiredUser");
        when(revokedTokenRepository.existsByToken("expiredToken")).thenReturn(true);

        // Act & Assert
        assertThrows(CustomException.class, () -> checkTokenValidOrNot.checkTokenValidOrNot(request));
        // Verify that isValidToken and existsByToken are called with the correct token
        verify(jwtUtils, times(1)).isValidToken("expiredToken");
        verify(revokedTokenRepository, times(1)).existsByToken("expiredToken");
    }

    @Test
    void testCheckTokenValidOrNot_NoTokenInHeader() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        boolean result = checkTokenValidOrNot.checkTokenValidOrNot(request);

        // Assert
        assertFalse(result);
        // Verify that isValidToken is not called
        verify(jwtUtils, never()).isValidToken(any());
    }
    
    @Test
    void testCheckTokenValidOrNot_WithoutAuthorizationHeader() {
        // Mock HttpServletRequest
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);

        // Call the method and assert
        assertFalse(checkTokenValidOrNot.checkTokenValidOrNot(request));
    }
    
    @Test
    void testCheckTokenValidOrNot_WithoutBearerToken() {
        // Mock HttpServletRequest
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Call the method and assert
        assertFalse(checkTokenValidOrNot.checkTokenValidOrNot(request));
    }

}
