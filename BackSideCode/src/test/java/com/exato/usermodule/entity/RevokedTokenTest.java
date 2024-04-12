package com.exato.usermodule.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class RevokedTokenTest {

	 @Test
	 void testRevokedTokenEntity() {
	        // Arrange
	        RevokedToken revokedToken = new RevokedToken();
	        revokedToken.setId(1L);
	        revokedToken.setToken("mockToken");
	        LocalDateTime revokedAt = LocalDateTime.now();
	        revokedToken.setRevokedAt(revokedAt);

	        // Act and Assert
	        assertEquals(1L, revokedToken.getId());
	        assertEquals("mockToken", revokedToken.getToken());
	        assertEquals(revokedAt, revokedToken.getRevokedAt());
	    }
	 
	 @Test
	 void testRevokedTokenEntityEquality() {
	        // Arrange
	        RevokedToken revokedToken1 = new RevokedToken();
	        revokedToken1.setId(1L);
	        revokedToken1.setToken("mockToken");
	        LocalDateTime revokedAt1 = LocalDateTime.now();
	        revokedToken1.setRevokedAt(revokedAt1);

	        RevokedToken revokedToken2 = new RevokedToken();
	        revokedToken2.setId(1L);
	        revokedToken2.setToken("mockToken");
	        LocalDateTime revokedAt2 = LocalDateTime.now();
	        revokedToken2.setRevokedAt(revokedAt2);

	        // Act and Assert
	        assertEquals(revokedToken1, revokedToken2);
	    }
	 
	
	 @Test
	   void testRevokedTokenEntityToString() {
	        // Arrange
	        RevokedToken revokedToken = new RevokedToken();
	        revokedToken.setId(1L);
	        revokedToken.setToken("mockToken");
	        LocalDateTime revokedAt = LocalDateTime.now();
	        revokedToken.setRevokedAt(revokedAt);

	        // Act and Assert
	        assertEquals("RevokedToken(id=1, token=mockToken, revokedAt=" + revokedAt + ")", revokedToken.toString());
	    }
}
