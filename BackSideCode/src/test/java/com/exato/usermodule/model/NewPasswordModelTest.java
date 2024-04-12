package com.exato.usermodule.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NewPasswordModelTest {

	 @Test
	    void testGettersAndSetters() {
	        // Arrange
	        NewPasswordModel newPasswordModel = new NewPasswordModel();
	        String email = "test@example.com";
	        String newPassword = "newPassword";

	        // Act
	        newPasswordModel.setEmail(email);
	        newPasswordModel.setNewPassword(newPassword);

	        // Assert
	        assertEquals(email, newPasswordModel.getEmail());
	        assertEquals(newPassword, newPasswordModel.getNewPassword());
	    }

}
