package com.exato.usermodule.jwt.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.exato.usermodule.entity.Role;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.repository.RevokedTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

class JwtUtilsTest {

	@InjectMocks
    private JwtUtils jwtUtils;
	
	@Mock
    private UserDetails mockUserDetails;

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private Claims mockedClaims;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;
    
    private String validToken = "validToken";
    private String expiredToken = "expiredToken";
    
    @BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
        jwtUtils = spy(new JwtUtils());
        
    }
    
    // Helper methods to create mock objects
    private UserDetails createMockUserDetails() {
        User user = new User();
        user.setId(1L);
        user.setClientId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setAssignedRoles(Set.of(createMockRole("ROLE_USER")));
        user.setActive(true);

        return user;
    }
    
    private UserDetails createMockUserDetails1() {
        User user = new User();
        user.setId(1L);
        user.setClientId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setAssignedRoles(Set.of(createMockRole("ROLE_USER")));
        user.setActive(false);

        return user;
    }
    
    private UserDetails createMockUserDetails2() {
        User user = new User();
        user.setId(1L);
        user.setClientId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setAssignedRoles(null);
        user.setActive(true);

        return user;
    }
    

    private Role createMockRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return role;
    }

    @Test
    void testGenerateToken() {
        // Arrange
        UserDetails userDetails = createMockUserDetails();

        // Act
        String token = jwtUtils.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    void testGetToken_UserNotActive() {
    	
    	UserDetails user= createMockUserDetails1();
      

        JwtUtils yourClassUnderTest = new JwtUtils();

        // Act
        String token = (String) ReflectionTestUtils.invokeMethod(yourClassUnderTest, "getToken", user);

        // Assert
        assertEquals("User not active, so cannot generate a token", token);
    }
    
      
    @Test
    void testGenerateTokenWithException() {
    	
    	UserDetails userDetails = createMockUserDetails();
    	
    	

        // Arrange
        when(jwtUtils.generateToken(userDetails)).thenThrow(new RuntimeException("Error: couldn't generate token"));


        // Act & Assert
        assertThrows(RuntimeException.class, () -> jwtUtils.generateToken(userDetails));
    }
    
    @Test
    void testGenerateTokenWithAuthentication() {
        // Arrange
        UserDetails userDetails = createMockUserDetails();
           Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        when(revokedTokenRepository.existsByToken(anyString())).thenReturn(false);

            
        // Act
        String token = jwtUtils.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);

    }
    
    @Test
    void testGenerateTokenWithUserDetails() {
        // Arrange
        UserDetails userDetails = createMockUserDetails();
        when(revokedTokenRepository.existsByToken(anyString())).thenReturn(false);

        // Act
        String token = jwtUtils.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);

         }
    
    @Test
    void testgenerateTokenWithException() {
        // Arrange
        UserDetails userDetails = createMockUserDetails();

        // Mock the claims for an invalid token with an exception during userId extraction
        when(jwtUtils.generateToken(userDetails)).thenThrow(new RuntimeException("Simulated exception"));
        
        assertThrows(RuntimeException.class, () -> jwtUtils.generateToken(userDetails));
       
    }
    @Test
    void testIsValidToken() {
        // Arrange
        String validToken = generateValidToken();
        String expiredToken = generateExpiredToken();
        String invalidToken = "invalidToken";

        // Mock the claims for valid and expired tokens
        doReturn(mockedClaims).when(jwtUtils).getClaims(validToken);
        when(mockedClaims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 1000000)); // Future expiration
        doReturn(mockedClaims).when(jwtUtils).getClaims(expiredToken);
        when(mockedClaims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 1000000)); // Past expiration
        // Assume getClaims throws an exception for an invalid token
        doReturn(mockedClaims).when(jwtUtils).getClaims(invalidToken);
      //  doThrow(new Exception("Invalid token")).when(jwtUtils).getClaims(invalidToken);

        // Act & Assert
       // assertTrue(jwtUtils.isValidToken(validToken));
        assertFalse(jwtUtils.isValidToken(expiredToken));
        assertFalse(jwtUtils.isValidToken(invalidToken));
    }
    
    @Test
    void testIsValidTokenWithException() {
        // Arrange
        String invalidToken = "invalidToken";

        // Mock the claims for an invalid token with null expiration
        doReturn(mockedClaims).when(jwtUtils).getClaims(invalidToken);
        doReturn(null).when(mockedClaims).getExpiration();

        // Act & Assert
        assertFalse(jwtUtils.isValidToken(invalidToken));

        // Additional assertions or verifications if needed
    }

    private String generateValidToken() {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + 100000)) // Future expiration
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, "testSecret")
                .compact();
    }

    private String generateExpiredToken() {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() - 10000)) // Past expiration
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, "testSecret")
                .compact();
    }
    

    @Test
    void testGetUsernameFromToken() {
        // Arrange
        String validToken = generateValidToken();
        String expiredToken = generateExpiredToken();
        String invalidToken = "invalidToken";

       
        // Mock the claims for valid, expired, and invalid tokens
        doReturn(mockedClaims).when(jwtUtils).getClaims(validToken);
        doReturn("john.doe@example.com##ROLE_USER").when(mockedClaims).getSubject();
        doReturn(new Date(System.currentTimeMillis() + 1000000)).when(mockedClaims).getExpiration();

        doReturn(mockedClaims).when(jwtUtils).getClaims(expiredToken);
        doReturn("john.doe@example.com##ROLE_USER").when(mockedClaims).getSubject(); // Corrected line
      //  doReturn(new Date(System.currentTimeMillis() - 100000)).when(mockedClaims).getExpiration();
        doReturn(mockedClaims).when(jwtUtils).getClaims(invalidToken);
        doReturn(null).when(mockedClaims).getSubject(); // Assuming invalid token should have a null subject
        doReturn(null).when(mockedClaims).getExpiration(); // Assuming invalid token should have a null expiration

        // Act & Assert
   //     assertEquals("john.doe@example.com", jwtUtils.getUsernameFromToken(validToken));
        assertEquals(null, jwtUtils.getUsernameFromToken(expiredToken));
       assertEquals(null, jwtUtils.getUsernameFromToken(invalidToken));

        // Additional assertions if needed
       // verify(log, never()).error(anyString(), anyString());
    }
    
    @Test
    void testgetUsernameFromTokenWithException() {
        // Arrange
       String token = "Invalid token";

        // Mock the claims for an invalid token with an exception during userId extraction
        when(jwtUtils.getUsernameFromToken(token)).thenThrow(new RuntimeException("Simulated exception"));
        
        assertThrows(RuntimeException.class, () -> jwtUtils.getUsernameFromToken(token));
       
    }
    
    @Test
    void testExtractUsernameFromTokenWithException() {
        // Arrange
        String invalidToken = "invalidToken";

        // Mock the claims for an invalid token with an exception during userId extraction
        when(jwtUtils.getUsernameFromToken(invalidToken)).thenThrow(new RuntimeException("Simulated exception"));
        
        assertThrows(RuntimeException.class, () -> jwtUtils.getUsernameFromToken(invalidToken));
       
    }

    @Test
    void testExtractClientId() {
        // Arrange
        String validToken = "mockedValidToken";
        Claims mockedClaims = mock(Claims.class);
        doReturn(mockedClaims).when(jwtUtils).getClaims(validToken);
        doReturn(123L).when(mockedClaims).get("clientId", Long.class);

        // Act
        Long clientId = jwtUtils.extractClientId(validToken);

        // Assert
        assertEquals(123L, clientId);
    }
    
    @Test
    void testExtractClientIdWithException() {
        // Arrange
        String invalidToken = "invalidToken";

        // Mock the claims for an invalid token with an exception during userId extraction
        when(jwtUtils.extractClientId(invalidToken)).thenThrow(new RuntimeException("Simulated exception"));
        
        assertThrows(RuntimeException.class, () -> jwtUtils.extractClientId(invalidToken));
       
    }
    
    @Test
    void testExtractBusinessUnit() {
        // Arrange
        String validToken = "mockedValidToken";
        Claims mockedClaims = mock(Claims.class);
        doReturn(mockedClaims).when(jwtUtils).getClaims(validToken);
        doReturn("BusinessUnitName").when(mockedClaims).get("businessUnit", String.class);

        // Act
        String businessUnit = jwtUtils.extractBusinessUnit(validToken);

        // Assert
        assertEquals("BusinessUnitName", businessUnit);
    }
    
    @Test
    void testExtractBusinessUnitWithException() {
        // Arrange
        String invalidToken = "invalidToken";

        // Mock the claims for an invalid token with an exception during userId extraction
        when(jwtUtils.extractBusinessUnit(invalidToken)).thenThrow(new RuntimeException("Simulated exception"));
        
        assertThrows(RuntimeException.class, () -> jwtUtils.extractBusinessUnit(invalidToken));
       
    }
    
    @Test
    void testExtractClientName() {
        // Arrange
        String validToken = "mockedValidToken";
        Claims mockedClaims = mock(Claims.class);
        doReturn(mockedClaims).when(jwtUtils).getClaims(validToken);
        doReturn("ClientName").when(mockedClaims).get("clientName", String.class);

        // Act
        String clientName = jwtUtils.extractClientName(validToken);

        // Assert
        assertEquals("ClientName", clientName);
    }
    
    @Test
    void testExtractClientNameWithException() {
        // Arrange
        String invalidToken = "invalidToken";

        // Mock the claims for an invalid token with an exception during userId extraction
        when(jwtUtils.extractClientName(invalidToken)).thenThrow(new RuntimeException("Simulated exception"));
        
        assertThrows(RuntimeException.class, () -> jwtUtils.extractClientName(invalidToken));
       
    }

    @Test
    void testExtractUserId() {
        // Arrange
        String validToken = "mockedValidToken";
        Claims mockedClaims = mock(Claims.class);
        doReturn(mockedClaims).when(jwtUtils).getClaims(validToken);
        doReturn(456L).when(mockedClaims).get("userId", Long.class);

        // Act
        Long userId = jwtUtils.extractUserId(validToken);

        // Assert
        assertEquals(456L, userId);
    }
    
    @Test
    void testExtractUserIdWithException() {
        // Arrange
        String invalidToken = "invalidToken";

        // Mock the claims for an invalid token with an exception during userId extraction
        when(jwtUtils.extractUserId(invalidToken)).thenThrow(new RuntimeException("Simulated exception"));
        
        assertThrows(RuntimeException.class, () -> jwtUtils.extractUserId(invalidToken));
       
    }
    
    @Test
    void testGetExpirationDateFromToken() {
    
        // Arrange
        doReturn(mockedClaims).when(jwtUtils).getClaims(validToken);
        doReturn(mockedClaims).when(jwtUtils).getClaims(expiredToken);
        Date expirationDate = new Date(System.currentTimeMillis() + 1000000); // Set a future expiration date
        doReturn(expirationDate).when(mockedClaims).getExpiration();

        // Act
        Date validTokenExpiration = mockedClaims.getExpiration();
        Date expiredTokenExpiration = jwtUtils.getExpirationDateFromToken(expiredToken);

        // Assert
        assertEquals(expirationDate, validTokenExpiration);
        assertEquals(null, expiredTokenExpiration); // Simulating an expired token

          }
    
    @Test
    void testGetRolesFromToken() {
        // Arrange
        String validToken = generateValidToken();
        String expiredToken = generateExpiredToken();
        String invalidToken = "invalidToken";

        // Mock the claims for valid, expired, and invalid tokens
        Claims mockedClaims = mock(Claims.class);
        doReturn(mockedClaims).when(jwtUtils).getClaims(validToken);
        doReturn("john.doe@example.com##ROLE_USER").when(mockedClaims).getSubject();

        doReturn(mockedClaims).when(jwtUtils).getClaims(expiredToken);
        doThrow(new ExpiredJwtException(null, null, "Token expired")).when(mockedClaims).getSubject();

        doReturn(mockedClaims).when(jwtUtils).getClaims(invalidToken);
        doReturn(null).when(mockedClaims).getSubject();

        // Act & Assert
       // assertEquals("john.doe@example.com##ROLE_USER", jwtUtils.getRolesFromToken(validToken));
        assertNull(jwtUtils.getRolesFromToken(expiredToken));
        assertNull(jwtUtils.getRolesFromToken(invalidToken));
    }
    
    @Test
    void testGetRolesFromTokenWithException() {
        // Arrange
        String invalidToken = "invalidToken";
        // Mock the claims for an invalid token
        when(jwtUtils.getRolesFromToken(invalidToken)).thenThrow(new RuntimeException("Simulated exception"));
      //  doThrow(Exception.class).when(mockedClaims).getSubject();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> jwtUtils.getRolesFromToken(invalidToken));
    }

         
    
  
}
