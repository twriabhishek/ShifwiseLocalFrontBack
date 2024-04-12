package com.exato.usermodule.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.exato.usermodule.entity.User;

@SpringBootTest
class UserRepositoryTest {

	@Mock
    private UserRepository userRepository;
	
	 @Mock
	    private User user; 
	
	 @Test
	    void testFindByEmail() {
	        // Arrange
	        String email = "test@example.com";
	        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

	        // Act
	        Optional<User> foundUser = userRepository.findByEmail(email);

	        // Assert
	        assertTrue(foundUser.isPresent());
	        assertEquals(user, foundUser.get());
	    }
	 
	 @Test
	    void testFindAllByClientId() {
	        // Arrange
	        Long clientId = 1L;
	        List<User> userList = new ArrayList<>();
	        when(userRepository.findAllByClientId(clientId)).thenReturn(userList);

	        // Act
	        List<User> foundUsers = userRepository.findAllByClientId(clientId);

	        // Assert
	        assertEquals(userList, foundUsers);
	    }
	 
	 @Test
	    void testFindByClientId() {
	        // Arrange
	        Long clientId = 1L;
	        User user = new User();
	        when(userRepository.findByClientId(clientId)).thenReturn(Optional.of(user));

	        // Act
	        Optional<User> foundUser = userRepository.findByClientId(clientId);

	        // Assert
	        assertTrue(foundUser.isPresent());
	        assertEquals(user, foundUser.get());
	    }
	 
	 @Test
	    void testFindByOtpNumber() {
	        // Arrange
	        String token = "123456";
	        when(userRepository.findByOtpNumber(token)).thenReturn(token);

	        // Act
	        String findByOtpNumber = userRepository.findByOtpNumber(token);

	        // Assert
	        assertEquals(token, findByOtpNumber);
	    }
	 
	 @Test
	    void testFindDistinctClientIds() {
	        // Arrange
	        List<User> clientIds = List.of(user); // Mock list of users
	        when(userRepository.findDistinctClientIds()).thenReturn(clientIds);

	        // Act
	        List<User> distinctClientIds = userRepository.findDistinctClientIds();

	        // Assert
	        assertEquals(clientIds, distinctClientIds);
	    }

	 
}
