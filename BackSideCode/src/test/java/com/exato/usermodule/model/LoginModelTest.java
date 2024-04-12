package com.exato.usermodule.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LoginModelTest {

	  @Test
	    void testLoginModelGettersSetters() {
	        // Arrange
	        String username = "testUser";
	        String password = "testPassword";

	        // Act
	        LoginModel loginModel = new LoginModel();
	        loginModel.setUsername(username);
	        loginModel.setPassword(password);

	        // Assert
	        assertNotNull(loginModel);
	        assertEquals(username, loginModel.getUsername());
	        assertEquals(password, loginModel.getPassword());
	    }

}
