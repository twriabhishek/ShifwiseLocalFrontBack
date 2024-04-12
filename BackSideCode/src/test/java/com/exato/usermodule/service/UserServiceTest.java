/**package com.exato.usermodule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.exato.usermodule.config.CallNotification;
import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.UserModel;
import com.exato.usermodule.repository.ClientInfoRepository;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.serviceimpl.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;


    @Mock
    private ClientInfoRepository clientInfoRepository;

    @Mock
    private CallNotification callNotification;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private HttpServletRequest request;
    
    // Utility methods for creating mock objects
    private UserModel createMockUserModel() {
        UserModel userModel = new UserModel();
        userModel.setFirstName("John");
        userModel.setLastName("Doe");
        userModel.setEmail("john.doe@example.com");
        // Set other properties as needed
        return userModel;
    }
    
    private UserModel createMockUserModel1() {
        UserModel userModel = new UserModel();
        userModel.setFirstName("Joe");
        userModel.setLastName("Dais");
        userModel.setEmail("joe.dais@example.com");
        // Set other properties as needed
        return userModel;
    }

    private Role createMockRole() {
        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("ROLE_USER");
        // Set other properties as needed
        return mockRole;
    }

    private User createMockUser() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setClientId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setAssignedRoles(Collections.singleton(createMockRole()));
        // Set other properties as needed
        return mockUser;
    }
    private User createMockUser1() {
        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setClientId(2L);
        mockUser.setFirstName("Joe");
        mockUser.setLastName("Dais");
        mockUser.setEmail("joe.dais@example.com");
        mockUser.setAssignedRoles(Collections.singleton(createMockRole()));
        // Set other properties as needed
        return mockUser;
    }
    
       
    @Test
    void testGetAllUser_notFound() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserModel> allUsers = userService.getAllUser();

        // Assert
        assertNotNull(allUsers);
        assertEquals(0, allUsers.size());
    }
    
    @Test
    void testGetAllUser_NonEmptyList() {
        // Arrange
        List<User> mockUserList = Arrays.asList(
                createMockUser(), createMockUser1()
        );

        when(userRepository.findAll()).thenReturn(mockUserList);

        // Act
        List<UserModel> allUsers = userService.getAllUser();

        // Assert
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());

        // Add more assertions based on your actual mapping logic
        UserModel expectedUserModel1 = createMockUserModel();
        UserModel expectedUserModel2 = createMockUserModel1();

        assertEquals(expectedUserModel1.getFirstName(), allUsers.get(0).getFirstName());
        assertEquals(expectedUserModel1.getLastName(), allUsers.get(0).getLastName());
        assertEquals(expectedUserModel1.getEmail(), allUsers.get(0).getEmail());

        assertEquals(expectedUserModel2.getFirstName(), allUsers.get(1).getFirstName());
        assertEquals(expectedUserModel2.getLastName(), allUsers.get(1).getLastName());
        assertEquals(expectedUserModel2.getEmail(), allUsers.get(1).getEmail());

        // Reset mock interactions for a clean state in case you have more tests
        reset(userRepository);
    }
    
    @Test
    void testCreateUser() {
        // Arrange
    	 // Arrange
        UserService userServiceMock = mock(UserService.class);
        HttpServletRequest requestMock = mock(HttpServletRequest.class);

        // Create an instance of UserModel that you expect to be returned by the mock
        UserModel expectedUserModel = new UserModel();
        expectedUserModel.setFirstName("John");
        expectedUserModel.setLastName("Doe");

        // Mock the behavior of userService.createUser
        when(userServiceMock.createUser(any(UserModel.class), any(HttpServletRequest.class)))
            .thenReturn(expectedUserModel);

        // Act
        UserModel responseEntity = userServiceMock.createUser(new UserModel(), requestMock);

        // Assert
        // Verify that the method was called with the expected parameters
        verify(userServiceMock, times(1)).createUser(any(UserModel.class), any(HttpServletRequest.class));

        // Verify the result or perform additional assertions
        // For example, you can assert that the returned UserModel matches the expectedUserModel
        assertEquals(expectedUserModel.getFirstName(), responseEntity.getFirstName());
    }
    
    @Test
    void testGetUserById_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomException.class, () -> userService.getUserById(userId),
                "Expected CustomException when user is not found");
    }
    
    @Test
    void testGetUserById_found() {
    	// Arrange
	    Long userId = 1L;
	    User mockUser = createMockUser(); // Create a mock User for testing
	    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

	    // Act
	    UserModel userById = userService.getUserById(userId);

	    // Assert
	    assertNotNull(userById);
	    // Add assertions based on your actual mapping logic
	    assertEquals(mockUser.getId(), userById.getId());
	    assertEquals(mockUser.getFirstName(), userById.getFirstName());
	    assertEquals(mockUser.getLastName(), userById.getLastName());
	    assertEquals(mockUser.getEmail(), userById.getEmail());
    }
    
    @Test
    void testUpdateUser() {
        // Arrange
        Long userId = 1L;
        UserModel userModel = createMockUserModel();
        Role mockRole = createMockRole();

        when(userRepository.findById(userId)).thenReturn(Optional.of(createMockUser()));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(userRepository.save(any())).thenReturn(createMockUser());
        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        when(jwtUtils.extractClientId("mockToken")).thenReturn(1L);
        when(jwtUtils.extractClientName("mockToken")).thenReturn("clientName");

        // Set assignedRoles property in userModel
        Set<Long> userRoles = new HashSet<>();
        userRoles.add(1L); // Assuming role ID 1 exists
        userModel.setAssignedRoles(userRoles);

        // Act
        UserModel updatedUser = userService.updateUser(userId, userModel, request);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("John", updatedUser.getFirstName());
        // Add more assertions based on your requirements
    }
    
    @Test
    void testGetAllClientIds_notFound() {
        // Arrange
        when(userRepository.findDistinctClientIds()).thenReturn(Collections.emptyList());

        // Act
        List<UserModel> allClientIds = userService.getAllClientIds();

        // Assert
        assertNotNull(allClientIds);
        assertEquals(0, allClientIds.size());
    }
    
   
    @Test
    void testGetAllUserByClientId_notFound() {
        // Arrange
        Long clientId = 1L;
        when(userRepository.findAllByClientId(clientId)).thenReturn(Collections.emptyList());

        // Act
        List<UserModel> allUsersByClientId = userService.getAllUserByClientId(clientId);

        // Assert
        assertNotNull(allUsersByClientId);
        assertEquals(0, allUsersByClientId.size());
    }
    
    @Test
    void testDeleteUser() {
        // Arrange
        Long userId = 1L;
        User userToDelete = createMockUser();

        // Mock the behavior of userRepository to return the user when findById is called
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.ofNullable(userToDelete));

        // Act
        userService.deleteUser(userId);

        // Assert
        // Verify that the method userRepository.deleteById was called with the expected userId
        verify(userRepository, times(1)).deleteById(userId);

    }
}
**/