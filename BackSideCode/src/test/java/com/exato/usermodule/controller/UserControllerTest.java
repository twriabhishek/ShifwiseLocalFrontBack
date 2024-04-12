/**package com.exato.usermodule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.exato.usermodule.config.CallNotification;
import com.exato.usermodule.config.ClientCallCMS;
import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.ClientInfo;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.BusinessUnitModel;
import com.exato.usermodule.model.GroupModel;
import com.exato.usermodule.model.ProcessUnitModel;
import com.exato.usermodule.model.TeamModel;
import com.exato.usermodule.model.UserModel;
import com.exato.usermodule.repository.ClientInfoRepository;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.service.AuditLogService;
import com.exato.usermodule.service.UserService;
import com.exato.usermodule.serviceimpl.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

class UserControllerTest {
	
	 @Mock
	    private UserRepository userRepository;

	    @Mock
	    private RoleRepository roleRepository;

	    @Mock
	    private PasswordEncoder passwordEncoder;

	    @Mock
	    private JwtUtils jwtUtils;
 
	    @Mock
	    private CheckTokenValidOrNot checkTokenValidOrNot;

	    @Mock
	    private ClientInfoRepository clientInfoRepository;

	    @Mock
	    private CallNotification callNotification;
	    
	    @Mock
	    private ClientCallCMS clientCallCMS;

	    @InjectMocks
	    private UserServiceImpl userService;

	    @Mock
	    private HttpServletRequest request;
	    

	    private List<BusinessUnitModel> createMockBusinessUnits() {
	    	BusinessUnitModel businessModel = new BusinessUnitModel();
	    	businessModel.setBusinessUnitId(1L);
	    	businessModel.setClientId(1L);
	    	businessModel.setBusinessUnitName("mockBusiness1");
	    	BusinessUnitModel businessModel1 = new BusinessUnitModel();
	    	businessModel1.setBusinessUnitId(2L);
	    	businessModel1.setClientId(1L);
	    	businessModel1.setBusinessUnitName("mockBusiness11");
	    	
	        return Arrays.asList(businessModel, businessModel1);
	    }
	    

	    private List<ProcessUnitModel> createMockProcessUnits() {
	    	ProcessUnitModel processModel = new ProcessUnitModel();
	    	processModel.setClientId(1L);
	    	processModel.setProcessUnitId(1L);
	    	processModel.setProcessUnitName("mockProcess");
	    	ProcessUnitModel processModel1 = new ProcessUnitModel();
	    	processModel1.setClientId(1L);
	    	processModel1.setProcessUnitId(2L);
	    	processModel1.setProcessUnitName("mockProcess1");
	        return Arrays.asList(processModel, processModel1);
	    }
	    
	    private List<TeamModel> createMockTeams() {
	    	TeamModel teamModel = new TeamModel();
	    	teamModel.setClientId(1L);
	    	teamModel.setTeamId(1L);
	    	teamModel.setTeamName("mockTeam");
	    	TeamModel teamModel1 = new TeamModel();
	    	teamModel1.setClientId(1L);
	    	teamModel1.setTeamId(2L);
	    	teamModel1.setTeamName("mockTeam1");
	        return Arrays.asList(teamModel,teamModel1);
	    }
	    
	    private List<GroupModel> createMockGroups() {
	        // Create and return a list of mock GroupModel objects for testing
	        // Modify this based on your actual GroupModel structure
	        return Arrays.asList(new GroupModel(), new GroupModel());
	    }
	    
	 // Utility methods for creating mock objects
	    private UserModel createMockUserModel() {
	        UserModel userModel = new UserModel();
	        userModel.setId(1L);
	        userModel.setFirstName("John");
	        userModel.setLastName("Doe");
	        userModel.setEmail("john.doe@example.com");
	        // Set other properties as needed
	        return userModel;
	    }
	    
		    

	@Test
    void testGetAssociatedUsers_ValidClientId() {
        // Arrange
        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
        UserService userService = mock(UserService.class);
        ClientInfoRepository clientInfoRepository = mock(ClientInfoRepository.class);

        UserController yourController = new UserController(userService, null, clientInfoRepository, null, null, checkTokenValidOrNot);

        HttpServletRequest request = mock(HttpServletRequest.class);
        Long clientId = 1L;

        // Mocking the valid token scenario
        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

        // Mocking the service response with a valid client
        ClientInfo mockClient = new ClientInfo();
        when(clientInfoRepository.findById(clientId)).thenReturn(java.util.Optional.of(mockClient));

        // Mocking the service response with associated users
        List<UserModel> mockAssociatedUsers = Collections.singletonList(new UserModel());
        when(userService.getAllUserByClientId(clientId)).thenReturn(mockAssociatedUsers);

        // Act
        ResponseEntity<List<UserModel>> responseEntity = yourController.getAssociatedUsers(clientId, request);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockAssociatedUsers, responseEntity.getBody());

        
    }
	
	@Test
	void testGetAssociatedUsers_Exceptions() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);
	    ClientInfoRepository clientInfoRepository = mock(ClientInfoRepository.class);

	    UserController yourController = new UserController(userService, null, clientInfoRepository, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    Long clientId = 1L;

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with an exception when fetching the client
	    when(clientInfoRepository.findById(clientId)).thenThrow(new RuntimeException("Database error"));

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> yourController.getAssociatedUsers(clientId, request),
	            "Expected CustomException to be thrown");

	    assertEquals("An error occurred while getting associated users for ID: " + clientId, exception.getMessage());
	    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
	}
	
	@Test
	void testGetAssociatedUsers_ClientIdNotFound() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);
	    ClientInfoRepository clientInfoRepository = mock(ClientInfoRepository.class);

	    UserController userController = new UserController(userService, null, clientInfoRepository, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    Long clientId = 1L;

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with no client found
	    when(clientInfoRepository.findById(clientId)).thenReturn(Optional.empty());

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.getAssociatedUsers(clientId, request),
	            "Expected CustomException to be thrown");

	  //  assertEquals("No client exists with ID: " + clientId, exception.getMessage());
	    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}
	
	@Test
	void testGetAssociatedUsers_NoUsersFound() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);
	    ClientInfoRepository clientInfoRepository = mock(ClientInfoRepository.class);

	    UserController userController = new UserController(userService, null, clientInfoRepository, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    Long clientId = 1L;

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with a valid client
	    ClientInfo mockClient = new ClientInfo();
	    when(clientInfoRepository.findById(clientId)).thenReturn(java.util.Optional.of(mockClient));

	    // Mocking the service response with no associated users
	    when(userService.getAllUserByClientId(clientId)).thenReturn(Collections.emptyList());

	    // Act
	  //  ResponseEntity<List<UserModel>> responseEntity = userController.getAssociatedUsers(clientId, request);
	    
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.getAssociatedUsers(clientId, request),
	            "Expected CustomException to be thrown");

	    // Assert
	    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	   // assertNull(responseEntity.getBody());
	}
	
	@Test
	void testGetAssociatedUsers_InvalidToken() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);
	    ClientInfoRepository clientInfoRepository = mock(ClientInfoRepository.class);

	    UserController userController = new UserController(userService, null, clientInfoRepository, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    Long clientId = 1L;

	    // Mocking the invalid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.getAssociatedUsers(clientId, request),
	            "Expected CustomException to be thrown");

	 //   assertEquals("Invalid token or not present in the header", exception.getMessage());
	    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}

	@Test
	void testGetDistinctClientIds_ValidTokenAndNonEmptyList() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with non-empty client IDs list
	    List<UserModel> mockClientIds = Arrays.asList(new UserModel(), new UserModel());
	    when(userService.getAllClientIds()).thenReturn(mockClientIds);

	    // Act
	    ResponseEntity<List<UserModel>> responseEntity = userController.getDistinctClientIds(request);

	    // Assert
	    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	    assertEquals(mockClientIds, responseEntity.getBody());
	}

	@Test
	void testGetDistinctClientIds_ValidTokenAndEmptyList() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with an empty client IDs list
	    when(userService.getAllClientIds()).thenReturn(Collections.emptyList());

	    // Act
	 //   ResponseEntity<List<UserModel>> responseEntity = userController.getDistinctClientIds(request);
	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.getDistinctClientIds( request),
	            "Expected CustomException to be thrown");

	    // Assert
	    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	  //  assertNull(responseEntity.getBody());
	}
	
	@Test
	void testGetDistinctClientIds_InvalidToken() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);

	    // Mocking the invalid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.getDistinctClientIds(request),
	            "Expected CustomException to be thrown");

	//    assertEquals("Invalid token or not present in the header", exception.getMessage());
	    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}
	
	 @Test
	    void testGetDistinctClientIds_InternalServerError() {
		 
		    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);

		    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);
	        // Arrange
	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service to throw an exception
	        when(userService.getAllClientIds()).thenThrow(new RuntimeException("Some internal error"));

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> userController.getDistinctClientIds(request),
	                "Expected CustomException to be thrown");

	        assertEquals("An error occurred while retrieving distinct client IDs: Some internal error", exception.getMessage());
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());

	    }
	
	@Test
	void testCreateUser_SuccessfulCreation() throws MethodArgumentNotValidException {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);
	    AuditLogService auditLogService = mock(AuditLogService.class);

	    UserController userController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    UserModel userModel = createMockUserModel();

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with a created user
	    UserModel mockCreatedUser = createMockUserModel();
	    when(userService.createUser(userModel, request)).thenReturn(mockCreatedUser);

	    // Act
	    ResponseEntity<UserModel> responseEntity = userController.createUser(mockCreatedUser, request);

	    // Assert
	    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	    assertEquals(mockCreatedUser, responseEntity.getBody());

	   
	}
	
	@Test
	void testCreateUser_TokenValidationFailure() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController userController = new UserController(userService, null, clientInfoRepository, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    UserModel userModel = createMockUserModel();

	    // Mocking the invalid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.createUser(userModel, request),
	            "Expected CustomException to be thrown");

	   // assertEquals("Invalid token or not present in the header", exception.getMessage());
	    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	  
	}
	
	@Test
	void testCreateUser_UserCreationFailure() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);
	    AuditLogService auditLogService = mock(AuditLogService.class);

	    UserController userController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    UserModel userModel = createMockUserModel();

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with a null (failed) user creation
	    when(userService.createUser(userModel, request)).thenReturn(null);

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.createUser(userModel, request),
	            "Expected CustomException to be thrown");

	    assertEquals("User creation failed.", exception.getMessage());
	    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	   
	}
	
	 @Test
	    void testCreateUser_InternalServerError() throws MethodArgumentNotValidException {
		 
		 CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);

		    UserController userController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);
	        // Arrange
	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service to throw an exception
	        when(userService.createUser(any(), any())).thenThrow(new RuntimeException("Some internal error"));

	        try {
	            // Act
	            userController.createUser(createMockUserModel(), request);
	        } catch (CustomException exception) {
	          
	            assertEquals("Some internal error", exception.getMessage());
	            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
	            // Add more assertions as needed
	        }
	 }
	
	@Test
	void testGetAllUser_SuccessfulRetrieval() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with a list of users
	    List<UserModel> mockUsers = Arrays.asList(createMockUserModel(), createMockUserModel());
	    when(userService.getAllUser()).thenReturn(mockUsers);

	    // Act
	    ResponseEntity<List<UserModel>> responseEntity = userController.getAllUser(request);

	    // Assert
	    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	    assertEquals(mockUsers, responseEntity.getBody());
	}
	
	@Test
	void testGetAllUser_TokenValidationFailure() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);

	    // Mocking the invalid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.getAllUser(request),
	            "Expected CustomException to be thrown");

	    //assertEquals("Invalid token or not present in the header", exception.getMessage());
	    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	    
	}
	
	@Test
	void testGetAllUser_NoUsersFound() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with an empty list of users
	    when(userService.getAllUser()).thenReturn(Collections.emptyList());

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.getAllUser(request),
	            "Expected CustomException to be thrown");

	    assertEquals("No user exists !!", exception.getMessage());
	    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}
	
	 @Test
	    void testGetAllUser_InternalServerError() {
		    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);

		    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);
	        // Arrange
	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service to throw an exception
	        when(userService.getAllUser()).thenThrow(new RuntimeException("Some internal error"));

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> userController.getAllUser(request),
	                "Expected CustomException to be thrown");

	      //  assertEquals("Some internal error", exception.getMessage());
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());

	     	    }
	
	@Test
	void testGetUserById_SuccessfulRetrieval() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    Long userId = 1L;

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with a user
	    UserModel mockUser = createMockUserModel();
	    when(userService.getUserById(userId)).thenReturn(mockUser);

	    // Act
	    ResponseEntity<UserModel> responseEntity = userController.getUserById(userId, request);

	    // Assert
	    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	    assertEquals(mockUser, responseEntity.getBody());
	}
	
	@Test
	void testGetUserById_TokenValidationFailure() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController userController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    Long userId = 1L;

	    // Mocking the invalid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> userController.getUserById(userId, request),
	            "Expected CustomException to be thrown");

	   // assertEquals("Invalid token or not present in the header", exception.getMessage());
	    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	  
	}
	
	@Test
	void testGetUserById_UserNotFound() {
	    // Arrange
	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    UserService userService = mock(UserService.class);

	    UserController yourController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);

	    HttpServletRequest request = mock(HttpServletRequest.class);
	    Long userId = 1L;

	    // Mocking the valid token scenario
	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	    // Mocking the service response with no user found
	    when(userService.getUserById(userId)).thenReturn(null);

	    // Act and Assert
	    CustomException exception = assertThrows(CustomException.class,
	            () -> yourController.getUserById(userId, request),
	            "Expected CustomException to be thrown");

	 //   assertEquals("User not found with ID: " + userId, exception.getMessage());
	    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}
	
	 @Test
	    void testGetUserById_InternalServerError() {
		    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
            
		    UserController yourController = new UserController(userService, null, null, null, null, checkTokenValidOrNot);
		 
	        // Arrange
	        Long userId = 1L;

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service to throw an exception
	        when(userService.getUserById(userId)).thenThrow(new RuntimeException("Some internal error"));

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.getUserById(userId, request),
	                "Expected CustomException to be thrown");

	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());

	    }
	 
	    @Test
	    void testUpdateUser_SuccessfulUpdate() {
		    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);
	        // Arrange
	        Long userId = 1L;
	        UserModel userModel = createMockUserModel(); // Provide valid user model for update

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a successful update
	        when(userService.updateUser(userId, userModel, request)).thenReturn(userModel);

	        // Act
	        ResponseEntity<UserModel> responseEntity = yourController.updateUser(userId, userModel, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals(userModel, responseEntity.getBody());

	    }
	    
	    @Test
	    void testUpdateUser_UserNotFound() {
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);
	    	
	        // Arrange
	        Long userId = 1L;
	        UserModel userModel = new UserModel(); // Provide valid user model for update

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with user not found
	        when(userService.updateUser(userId, userModel, request)).thenReturn(null);

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.updateUser(userId, userModel, request),
	                "Expected CustomException to be thrown");

	        assertEquals("User not found with ID: " + userId, exception.getMessage());
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

	     
	    }
	    
	    @Test
	    void testUpdateUser_InternalServerError() {
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);
	    	
	        // Arrange
	        Long userId = 1L;
	        UserModel userModel = createMockUserModel(); // Provide valid user model for update

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service to throw an exception
	        when(userService.updateUser(userId, userModel, request)).thenThrow(new RuntimeException("Some internal error"));

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.updateUser(userId, userModel, request),
	                "Expected CustomException to be thrown");

	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());

	    }
	    
	    @Test
	    void testUpdateUser_InvalidToken() {
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);
	    	
	        // Arrange
	        Long userId = 1L;
	        UserModel userModel = createMockUserModel();  // Create a mock UserModel for testing

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.updateUser(userId, userModel, request),
	                "Expected CustomException to be thrown");

	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	    }
	    
	    @Test
	    void testDeleteUser_SuccessfulDeletion() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);
	        // Arrange
	        Long userId = 1L;

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a successful deletion
	        when(userService.getUserById(userId)).thenReturn(new UserModel()); // User found
	        doNothing().when(userService).deleteUser(userId);

	        // Act
	        ResponseEntity<String> responseEntity = yourController.deleteUser(userId, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("User with ID deleted successfully." + userId, responseEntity.getBody());

	    }
	    
	    @Test
	    void testDeleteUser_UserNotFound() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);
	        // Arrange
	        Long userId = 1L;

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with user not found
	        when(userService.getUserById(userId)).thenReturn(null);

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.deleteUser(userId, request),
	                "Expected CustomException to be thrown");

	        assertEquals("User with ID does not exist: " + userId, exception.getMessage());
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

	    }
	    
	    @Test
	    void testDeleteUser_InternalServerError() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);
	        // Arrange
	        Long userId = 1L;
	        UserModel userModel = createMockUserModel();

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service to throw an exception
	        when(userService.getUserById(userId)).thenReturn(userModel); // User found
	        doThrow(new RuntimeException("Some internal error")).when(userService).deleteUser(userId);

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.deleteUser(userId, request),
	                "Expected CustomException to be thrown");

	      //  assertEquals("Some internal error", exception.getMessage());
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());

	    }
	    
	    @Test
	    void testDeleteUser_InvalidToken() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		    UserService userService = mock(UserService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, null, auditLogService, checkTokenValidOrNot);
	        // Arrange
	        Long userId = 1L;

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act and Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.deleteUser(userId, request),
	                "Expected CustomException to be thrown");

	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	        
	    }
	    
	    @Test
	    void testGetBusinessUnits_Success() {
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	    	
	        // Arrange
		    String token = "mockToken";

	        // Mocking the HttpServletRequest
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
	      
	        List<BusinessUnitModel> mockBusinessUnits = createMockBusinessUnits();
	   
	        // Mocking the successful scenario
	        when(clientCallCMS.getAllBusinessUnits(token)).thenReturn(mockBusinessUnits);
	        
	        // Act
	        ResponseEntity<List<BusinessUnitModel>> responseEntity = yourController.getBusinessUnits(token);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals(mockBusinessUnits, responseEntity.getBody());

	    }
	
	    @Test
	    void testGetBusinessUnits_NoBusinessUnitsFound() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	    		    	
	        // Arrange
	        String token = "mockToken";

	        // Mocking the HttpServletRequest
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

	        // Mocking scenario where no business units are found
	        when(clientCallCMS.getAllBusinessUnits(token)).thenReturn(Collections.emptyList());

	        // Act
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.getBusinessUnits(token),
	                "Expected CustomException to be thrown");

	        // Assert
	        assertEquals("No business units found for the given token", exception.getMessage());
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

	      	    }
	    
	    @Test
	    void testGetBusinessUnits_InternalServerError() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	    		    	
	    	
	        // Arrange
	        String token = "mockToken";

	        // Mocking the HttpServletRequest
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

	        // Mocking scenario where an internal server error occurs
	        when(clientCallCMS.getAllBusinessUnits(token)).thenThrow(new RuntimeException("Internal server error"));

	        // Act
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.getBusinessUnits(token),
	                "Expected CustomException to be thrown");

	        // Assert
	        assertEquals("An error occurred while fetching business units", exception.getMessage());
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());

	    }
	    
	    @Test
	    void testGetProcessUnits_Successful() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	       
	        // Mocking the token
	        String token = "mockToken";

	        // Mocking the successful scenario
	        List<ProcessUnitModel> mockProcessUnits = createMockProcessUnits();
	        when(clientCallCMS.getProcessUnits(token)).thenReturn(mockProcessUnits);

	        // Act
	        ResponseEntity<List<ProcessUnitModel>> responseEntity = yourController.getProcessUnits(token);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals(mockProcessUnits, responseEntity.getBody());
	    }

	    @Test
	    void testGetProcessUnits_EmptyList() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	       

	        // Mocking the token
	        String token = "mockToken";

	        // Mocking the scenario where an empty list is returned
	        when(clientCallCMS.getProcessUnits(token)).thenReturn(null);

	        // Act
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.getProcessUnits(token),
	                "Expected CustomException to be thrown");

	        // Assert
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	       
	    }
	    
	    @Test
	    void testGetProcessUnits_Exception() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	        // Mocking the token
	        String token = "mockToken";

	        // Mocking the scenario where an exception is thrown
	        when(clientCallCMS.getProcessUnits(token)).thenThrow(new RuntimeException("Test exception"));

	        // Act and Assert
	        assertThrows(CustomException.class, () -> yourController.getProcessUnits(token));
	    }
	    
	    @Test
	    void testGetTeams_Successful() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);

	        // Mocking the token
	        String token = "mockToken";

	        // Mocking the successful scenario
	        List<TeamModel> mockTeams = createMockTeams();
	        when(clientCallCMS.getTeams(token)).thenReturn(mockTeams);

	        // Act
	        ResponseEntity<List<TeamModel>> responseEntity = yourController.getTeam(token);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals(mockTeams, responseEntity.getBody());
	    }
	    
	    @Test
	    void testGetTeams_EmptyList() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	        // Mocking the token
	        String token = "mockToken";

	        // Mocking the scenario where an empty list is returned
	        when(clientCallCMS.getTeams(token)).thenReturn(Collections.emptyList());

	        // Act
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.getProcessUnits(token),
	                "Expected CustomException to be thrown");

	        // Assert
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	    
	    }
	    
	    @Test
	    void testGetTeamNoTeamsFound() {
	        // Arrange
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	       
	        String token = "your-token";
	        when(clientCallCMS.getTeams(token)).thenReturn(Collections.emptyList());

	        // Act & Assert
	        CustomException customException = assertThrows(CustomException.class,
	                () -> yourController.getTeam(token),
	                "Expected CustomException to be thrown");
	        assertEquals("No teams found for the given token", customException.getMessage());
	        assertEquals(HttpStatus.NOT_FOUND, customException.getStatus());
	    }
	    
	    @Test
	    void testGetTeams_Exception() {
	    	
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);

	        // Mocking the token
	        String token = "mockToken";

	        // Mocking the scenario where an exception is thrown
	        when(clientCallCMS.getTeams(token)).thenThrow(new RuntimeException("Test exception"));

	        // Act and Assert
	        assertThrows(CustomException.class, () -> yourController.getTeam(token));
	    }
	    
	    @Test
	    void testGetGroups_Successful() {
	        // Arrange
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);

	        // Mocking the token
	        String token = "mockToken";

	        // Mocking the successful scenario
	        List<GroupModel> mockGroups = createMockGroups();
	        when(clientCallCMS.getGroups(token)).thenReturn(mockGroups);

	        // Act
	        ResponseEntity<List<GroupModel>> responseEntity = yourController.getGroup(token);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals(mockGroups, responseEntity.getBody());
	    }
	    
	    @Test
	    void testGetGroups_EmptyList() {
	        // Arrange
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);

	        // Mocking the token
	        String token = "mockToken";

	        // Mocking the scenario where an empty list is returned
	        when(clientCallCMS.getGroups(token)).thenReturn(Collections.emptyList());

	        // Act
	        CustomException exception = assertThrows(CustomException.class,
	                () -> yourController.getGroup(token),
	                "Expected CustomException to be thrown");

	        // Assert
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	    }

	    @Test
	    void testGetGroups_Exception() {
	        // Arrange
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);

	        // Mocking the token
	        String token = "mockToken";

	        // Mocking the scenario where an exception is thrown
	        when(clientCallCMS.getGroups(token)).thenThrow(new RuntimeException("Test exception"));

	        // Act and Assert
	        assertThrows(CustomException.class, () -> yourController.getGroup(token));
	    }

	    @Test
	    void resendUserMail_InvalidToken() {
	        // Arrange
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserService userService = mock(UserService.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	        Long userId = 1L;
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act & Assert
	        assertThrows(CustomException.class, () -> yourController.resendUserMail(userId, request));
	    }
	    
	    @Test
	    void resendUserMail_FailedToSendEmail() {
	        // Arrange
	        Long userId = 1L;
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	ClientCallCMS clientCallCMS = mock(ClientCallCMS.class);
		    UserServiceImpl userService = mock(UserServiceImpl.class);
		   AuditLogService auditLogService = mock(AuditLogService.class);
		    UserController yourController = new UserController(userService, null, clientInfoRepository, clientCallCMS, auditLogService, checkTokenValidOrNot);
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        UserModel userById = new UserModel(); // Set up your user object accordingly
	        when(userService.getUserById(userId)).thenReturn(userById);

	        when(request.getHeader("Authorization")).thenReturn("your_mocked_token");
	        when(userService.resetPasswordLinkEmail(anyString(), anyString(), anyString())).thenReturn(false);

	        // Act & Assert
	        assertThrows(CustomException.class, () -> yourController.resendUserMail(userId, request));
	    }
	    
	    @Test
	    void testGetSiteURL() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/context/path"));
	        when(request.getServletPath()).thenReturn("/path");

	        UserController yourClass = new UserController(userService, userService, clientInfoRepository, clientCallCMS, null, checkTokenValidOrNot); // Replace YourClass with the actual class containing getSiteURL method

	        // Act
	        String siteURL = yourClass.getSiteURL(request);

	        // Assert
	        assertEquals("http://example.com/context", siteURL);
	    }
	

}
**/
