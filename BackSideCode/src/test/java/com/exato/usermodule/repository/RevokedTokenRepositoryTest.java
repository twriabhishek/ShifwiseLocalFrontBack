package com.exato.usermodule.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RevokedTokenRepositoryTest {

	 @Test
	 void testExistsByToken() {
	        // Arrange
	        RevokedTokenRepository revokedTokenRepositoryMock = mock(RevokedTokenRepository.class);
	        String tokenToCheck = "sampleToken";
	        when(revokedTokenRepositoryMock.existsByToken(tokenToCheck)).thenReturn(true);

	        // Act
	        boolean tokenExists = revokedTokenRepositoryMock.existsByToken(tokenToCheck);

	        // Assert
	        assertTrue(tokenExists);
	       	    }
}
