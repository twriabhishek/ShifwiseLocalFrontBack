package com.exato.usermodule.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.exato.usermodule.entity.ClientInfo;

@SpringBootTest
class ClientInfoRepositoryTest {

	 @Mock
	    private ClientInfoRepository clientInfoRepository;

	    @Mock
	    private ClientInfo clientInfo; // Mock ClientInfo object

	    @Test
	    void testFindByEmail() {
	        // Arrange
	        String email = "test@example.com";
	        when(clientInfoRepository.findByEmail(email)).thenReturn(Optional.of(clientInfo));

	        // Act
	        Optional<ClientInfo> foundClientInfo = clientInfoRepository.findByEmail(email);

	        // Assert
	        assertTrue(foundClientInfo.isPresent());
	        assertEquals(clientInfo, foundClientInfo.get());
	    }

}
