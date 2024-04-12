/**package com.exato.usermodule.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
import com.exato.usermodule.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
	
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
	    void createUser_ValidInput_ShouldCreateUserSuccessfully() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        UserModel userModel = createMockUserModel();
	        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
	        Mockito.lenient().when(jwtUtils.extractClientId(anyString())).thenReturn(1L);
	        Mockito.lenient().when(jwtUtils.extractClientName(anyString())).thenReturn("Client1");
	        Mockito.lenient().when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.empty());
	      
	        Role mockRole = createMockRole();
	        Mockito.lenient().when(roleRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockRole));
	        userModel.setAssignedRoles(Collections.singleton(mockRole.getId()));
	        
	     // Mocking a different interface that contains resetPasswordLinkEmail method
	        CallNotification callNotification = mock(CallNotification.class);
	        Mockito.lenient().when(callNotification.sendResetPasswordEmail(anyString(), anyString(), anyString())).thenReturn(true);

	        // Mock the getSiteURL method
	        Mockito.lenient().when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
	        Mockito.lenient().when(request.getServletPath()).thenReturn("/servletPath");
	     // Mocking the resetPasswordLinkEmail method
	        Mockito.lenient().when(userService.resetPasswordLinkEmail(anyString(), anyString(), anyString())).thenReturn(true);

	        // Mocking the log.error method
	     //   doNothing().when(log).error(anyString(), any(Throwable.class));

	        // Mocking the CustomException
	     //   when(userService.createCustomException(anyString(), any(HttpStatus.class))).thenReturn(new CustomException("Email notification failed. Transaction rolled back.", HttpStatus.INTERNAL_SERVER_ERROR));
	        Mockito.lenient().when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
	        Mockito.lenient().when(userRepository.save(any(User.class))).thenReturn(createMockUser());

	        // Act
	        UserModel result = userService.createUser(userModel, request);

	        // Assert
	        assertNotNull(result);
	        assertEquals(userModel.getEmail(), result.getEmail());
	        assertEquals(userModel.getAssignedRoles(), result.getAssignedRoles());
	        // Add more assertions based on your expected behavior

	        // Verify interactions
	        verify(userRepository, times(1)).findByEmail(anyString());
	        verify(roleRepository, atLeastOnce()).findById(anyLong());
	        verify(userRepository, times(1)).save(any(User.class));
	       
	    }
	    
	    @Test
	    void testCreateUser_Exception() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        UserModel userModel = createMockUserModel();

	        // Mocking dependencies
	        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
	        when(jwtUtils.extractClientId("mockToken")).thenReturn(1L);
	        when(jwtUtils.extractClientName("mockToken")).thenReturn("Client1");
	        when(userRepository.findByEmail(userModel.getEmail())).thenReturn(java.util.Optional.of(new User()));

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> userService.createUser(userModel, request),
	                "Expected CustomException to be thrown");

	        assertEquals("Email already exists!!", exception.getMessage());
	        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

	        // Verify that userRepository.findByEmail was called with the correct argument
	        verify(userRepository, times(1)).findByEmail(userModel.getEmail());
	    }
	    
	    @Test
	    void testCreateUser_BadRequest() {
	    	 UserModel userModel = createMockUserModel();

		        // Mocking dependencies
		        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
		        when(jwtUtils.extractClientId("mockToken")).thenReturn(1L);
		        when(jwtUtils.extractClientName("mockToken")).thenReturn("Client1");
		        when(userRepository.findByEmail(userModel.getEmail())).thenReturn(java.util.Optional.empty());

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> userService.createUser(userModel, request),
	                "Expected CustomException for email notification failure");
	        
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	    	    }
	    
	    @Test
	    void createUser_ValidInput_EmailNotSend() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        UserModel userModel = createMockUserModel();
	        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
	        Mockito.lenient().when(jwtUtils.extractClientId(anyString())).thenReturn(1L);
	        Mockito.lenient().when(jwtUtils.extractClientName(anyString())).thenReturn("Client1");
	        Mockito.lenient().when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.empty());
	      
	        Role mockRole = createMockRole();
	        Mockito.lenient().when(roleRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockRole));
	        userModel.setAssignedRoles(Collections.singleton(mockRole.getId()));
	        
	     // Mocking a different interface that contains resetPasswordLinkEmail method
	        CallNotification callNotification = mock(CallNotification.class);
	        Mockito.lenient().when(callNotification.sendResetPasswordEmail(anyString(), anyString(), anyString())).thenReturn(true);

	        // Mock the getSiteURL method
	        Mockito.lenient().when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
	        Mockito.lenient().when(request.getServletPath()).thenReturn("/servletPath");
	     // Mocking the resetPasswordLinkEmail method
	        Mockito.lenient().when(userService.resetPasswordLinkEmail(anyString(), anyString(), anyString())).thenReturn(false);

	   	        Mockito.lenient().when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
	        Mockito.lenient().when(userRepository.save(any(User.class))).thenReturn(createMockUser());

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> userService.createUser(userModel, request),
	                "Expected CustomException to be thrown");

	      //  assertEquals("An error occurred while updating Client with ID 1: Internal server error", exception.getMessage());
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
	              
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
	    void testGetAllUser_Exception() {
	        // Arrange
	        when(userRepository.findAll()).thenThrow(new RuntimeException("An error occurred while fetching users"));

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> userService.getAllUser(),
	                "Expected CustomException to be thrown");

	        assertEquals("Error occurred while retrieving all Users: An error occurred while fetching users", exception.getMessage());
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	        // Verify interactions
	        verify(userRepository, times(1)).findAll();
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
	    void testGetUserById_ExceptionAfterRepositoryCall() {
	        // Arrange
	        Long userId = 1L;
	        when(userRepository.findById(userId)).thenThrow(RuntimeException.class);

	        // Act and Assert
	        assertThrows(CustomException.class, () -> userService.getUserById(userId),
	                "Expected CustomException when an exception occurs after the repository call");
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
	    void testGetUserById_InternalServerError() {
	        // Arrange
	        Long userId = 1L;
	        Mockito.lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(createMockUser()));
	        doThrow(new RuntimeException("Simulating internal server error")).when(userRepository).findById(userId);

	        // Act and Assert
	        assertThrows(CustomException.class, () -> userService.getUserById(userId),
	                "Expected CustomException for internal server error");
	    }
	    
	 	    
	    @Test
	    void testUpdateUser() {
	        // Arrange
	        Long userId = 1L;
	        UserModel userModel = createMockUserModel();
	        Role mockRole = createMockRole();

	        Mockito.lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(createMockUser()));
	        Mockito.lenient().when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
	        Mockito.lenient().when(userRepository.save(any())).thenReturn(createMockUser());
	        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
	        Mockito.lenient().when(jwtUtils.extractClientId("mockToken")).thenReturn(1L);
	        Mockito.lenient().when(jwtUtils.extractClientName("mockToken")).thenReturn("clientName");

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
	    void testUpdateUser_Conflict() {
	        // Arrange
	        Long userId = 1L;
	        UserModel userModel = createMockUserModel(); // Create a mock UserModel for testing
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        String token = "Bearer mockToken";
	        
	        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn(token);
	        Mockito.lenient().when(jwtUtils.extractClientName(token)).thenReturn("clientName");

	        User existingUser = createMockUser1();
	        Mockito.lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
	        Mockito.lenient().when(userRepository.findByEmail(userModel.getEmail())).thenReturn(Optional.of(createMockUser1())); // Existing user with the same email

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class, () -> userService.updateUser(userId, userModel, request),
	                "Expected CustomException for email conflict");
	        
	        assertEquals("Email already exists for another user.", exception.getMessage());
	        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
	    }
	    
	    @Test
	    void testUpdateUser_BadRequest() {
	        // Arrange
	        Long userId = 1L;
	        UserModel userModel = createMockUserModel(); // Create a mock UserModel for testing
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        String token = "Bearer mockToken";
	        
	        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn(token);
	        Mockito.lenient().when(jwtUtils.extractClientName(token)).thenReturn("clientName");

	        Mockito.lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(createMockUser()));
	        Mockito.lenient().when(userRepository.findByEmail(userModel.getEmail())).thenReturn(Optional.empty()); // No conflict

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class, () -> userService.updateUser(userId, userModel, request),
	                "Expected CustomException for bad request");
	        
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	    }
	    
	    @Test
	    void testGetAllClientIds_notFound() {
	        // Arrange
	    	Mockito.lenient().when(userRepository.findDistinctClientIds()).thenReturn(Collections.emptyList());

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