package com.exato.usermodule.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;


class LogoutModelTest {
	
	 
	@Test
    void testLogoutModelGettersSetters() {
        // Arrange
        String username = "testUser";

        // Act
        LogoutModel logoutModel = new LogoutModel();
        logoutModel.setUsername(username);

        // Assert
        assertNotNull(logoutModel);
        assertEquals(username, logoutModel.getUsername());
    }
	
	 @Test
	    void testNoArgsConstructor() {
	        // Act
	        LogoutModel logoutModel = new LogoutModel();

	        // Assert
	        assertNotNull(logoutModel);
	        assertNull(logoutModel.getUsername());
	    }

	    @Test
	    void testAllArgsConstructor() {
	        // Arrange
	        String username = "testUser";

	        // Act
	        LogoutModel logoutModel = new LogoutModel();
	        logoutModel.setUsername("testUser");

	        // Assert
	        assertEquals(username, logoutModel.getUsername());
	    }
	    
	    	    
	    @Test
	    void testToString() {
	        // Arrange
	        String expectedToString = "LogoutModel(username=testUser)";
	        
	        // Act
	        LogoutModel logoutModel = new LogoutModel();
	        logoutModel.setUsername("testUser");

	        // Assert
	        assertEquals(expectedToString, logoutModel.toString());
	    }

}
