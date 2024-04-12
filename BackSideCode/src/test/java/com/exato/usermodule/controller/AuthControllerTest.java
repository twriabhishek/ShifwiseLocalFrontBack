package com.exato.usermodule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.LoginModel;
import com.exato.usermodule.model.LogoutModel;
import com.exato.usermodule.repository.RevokedTokenRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private CheckTokenValidOrNot checkTokenValidOrNot;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLogin() {
        // Arrange
    	LoginModel loginModel = mock(LoginModel.class);
        when(loginModel.getUsername()).thenReturn("testUser");
        when(loginModel.getPassword()).thenReturn("password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
               
     // Mock JwtUtils
        String token = "mockedToken";
        when(jwtUtils.generateToken(authentication)).thenReturn(token);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn("mockedUsername");
        when(jwtUtils.extractClientId(token)).thenReturn(1L);
        when(jwtUtils.extractClientName(token)).thenReturn("mockedClient");
        when(jwtUtils.extractUserId(token)).thenReturn(100L);
        when(jwtUtils.extractBusinessUnit(token)).thenReturn("mockedBusinessUnit");
        when(jwtUtils.getRolesFromToken(token)).thenReturn("##ROLE_USER,ROLE_ADMIN");


        // Mock UserRepository
        User mockUser = mock(User.class);
        when(userRepository.findByEmail("testUser")).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<?> responseEntity = authController.login(loginModel);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());


    }
    
    @Test
    void testLogin_ExceptionHandling() {
        // Arrange
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        UserRepository userRepository = mock(UserRepository.class);

        AuthController yourController = new AuthController(userRepository, auditLogService, authenticationManager, jwtUtils, checkTokenValidOrNot, revokedTokenRepository);

        LoginModel loginModel = mock(LoginModel.class);
        when(loginModel.getUsername()).thenReturn("testUser");
        when(loginModel.getPassword()).thenReturn("password");

        // Mocking the authentication manager to throw an exception
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Authentication failed"));

        // Act
        ResponseEntity<?> responseEntity = yourController.login(loginModel);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Authentication failed", responseEntity.getBody());
    }
    
    
       
    @Test
    void testLogout() {
    	// Arrange
        LogoutModel logoutModel = mock(LogoutModel.class);
        when(logoutModel.getUsername()).thenReturn("testUser");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn("Bearer mockedToken");

        // Mock RevokedTokenRepository
        when(revokedTokenRepository.existsByToken("mockedToken")).thenReturn(false);

        // Act
        ResponseEntity<String> responseEntity = authController.logout(logoutModel, request);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Logout successful", responseEntity.getBody());
        // Add more assertions based on the expected behavior
    }
    
    @Test
    void testLogout_InvalidToken() {
        // Arrange
        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
        RevokedTokenRepository revokedTokenRepository = mock(RevokedTokenRepository.class);
        AuditLogService auditLogService = mock(AuditLogService.class);
        
        AuthController yourController = new AuthController(userRepository, auditLogService, authenticationManager, jwtUtils, checkTokenValidOrNot, revokedTokenRepository);


        // Mocking the request with an invalid token
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

     // Mocking the logout model
        LogoutModel logoutModel = new LogoutModel();
        logoutModel.setUsername("testUser");

        // Act and Assert
        assertThrows(CustomException.class, () -> yourController.logout(logoutModel, request));
    }
    
   
}
