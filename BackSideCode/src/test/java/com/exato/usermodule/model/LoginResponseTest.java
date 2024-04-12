package com.exato.usermodule.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LoginResponseTest {

	 @Test
	    void testLoginResponseConstructorAndGetters() {
	        // Arrange
	        String token = "sampleToken";
	        String tokenType = "Bearer";
	        String username = "john.doe";
	        Long clientId = 1L;
	        String clientName = "TestClient";
	        Long userId = 123L;
	        String[] roles = {"ROLE_USER", "ROLE_ADMIN"};
	        String businessUnit = "TestBusinessUnit";

	        // Act
	        LoginResponse loginResponse = new LoginResponse(token, tokenType, username, clientId, clientName, userId, roles, businessUnit);

	        // Assert
	        assertEquals(token, loginResponse.getToken());
	        assertEquals(tokenType, loginResponse.getTokenType());
	        assertEquals(username, loginResponse.getUsername());
	        assertEquals(clientId, loginResponse.getClientId());
	        assertEquals(clientName, loginResponse.getClientName());
	        assertEquals(userId, loginResponse.getUserId());
	        assertArrayEquals(roles, loginResponse.getRoles());
	        assertEquals(businessUnit, loginResponse.getBusinessUnit());
	    }
	 
	 @Test
	   void testNoArgsConstructor() {
	        // Act
	        LoginResponse loginResponse = new LoginResponse();

	        // Assert
	        assertNotNull(loginResponse);
	    }


}
