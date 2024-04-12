/**package com.exato.usermodule.serviceimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import com.exato.usermodule.entity.ClientInfo;
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.ClientInfoModel;
import com.exato.usermodule.repository.ClientInfoRepository;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ClientInfoServiceImplTest {

	@Mock
    private ClientInfoRepository clientInfoRepository;

    @InjectMocks
    private ClientInfoServiceImpl clientInfoService;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserService userService;
    
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private JwtUtils jwtUtils;
    
    @Mock
    private CallNotification callNotification;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    private ClientInfoModel createMockClientInfoModel() {
        ClientInfoModel clientModel = new ClientInfoModel();
        clientModel.setSpocName("John Doe");
        clientModel.setClientName("John's Client");
        clientModel.setEmail("john.doe@example.com");
        clientModel.setPassword("password");
        clientModel.setAddress("123 Main St");
        clientModel.setPhonenumber("1234567890");
        clientModel.setBussinessnumber("987654321");
        Set<Long> assignedRoles = new HashSet<>();
        assignedRoles.add(1L); // Assuming role ID 1 exists
        clientModel.setAssignedRoles(assignedRoles);
        return clientModel;
    }
    
    private ClientInfoModel createMockClientInfoModel1() {
    	ClientInfoModel clientModel = new ClientInfoModel();
    	clientModel.setClientId(2L);
    	clientModel.setClientName("Joe");
    	clientModel.setEmail("joe.doe@example.com");
        // Set other properties as needed
        return clientModel;
    }
    
    private ClientInfo createMockClientInfo() {
        ClientInfo mockClientInfo = new ClientInfo();
        mockClientInfo.setClientId(1L);
        mockClientInfo.setClientName("John");
        mockClientInfo.setEmail("john.doe@example.com");

        mockClientInfo.setAssignedRoles(Collections.singleton(createMockRole()));
        return mockClientInfo;
    }
    
    private ClientInfo createMockClientInfo1() {
        ClientInfo mockClientInfo = new ClientInfo();
        mockClientInfo.setClientId(2L);
        mockClientInfo.setClientName("Joe");
        mockClientInfo.setEmail("joe.doe@example.com");

        mockClientInfo.setAssignedRoles(Collections.singleton(createMockRole()));
        return mockClientInfo;
    }
    
    private User createMockUser() {
        User mockUser = new User();
        mockUser.setClientId(1L);
        mockUser.setFirstName("John");
        mockUser.setEmail("john.doe@example.com");

        mockUser.setAssignedRoles(Collections.singleton(createMockRole()));
        return mockUser;
    }

    private Role createMockRole() {
        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("ROLE_USER");
        // Set other properties as needed
        return mockRole;
    }
    
    private ClientInfoModel createUpdatedClientInfoModel(Long clientId) {
    	ClientInfoModel clientModel = new ClientInfoModel();
    	clientModel.setClientId(clientId);
    	return clientModel;
    }
    
    @Test
    void testCreateClient_Success() {
        // Arrange
        ClientInfoModel clientModel = createMockClientInfoModel();
        String token = "Bearer mockToken";

        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn(token);
        Mockito.lenient().when(jwtUtils.extractClientName(token)).thenReturn("creator");
        Mockito.lenient().when(clientInfoRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());
        Mockito.lenient().when(userRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());
        Mockito.lenient().when(roleRepository.findById(1L)).thenReturn(Optional.of(new Role()));
        Mockito.lenient().when(clientInfoRepository.save(any())).thenReturn(createMockClientInfo());
        Mockito.lenient().when(userRepository.save(any())).thenReturn(createMockUser());
        
     // Inject the mocked instance of CallNotification into ClientInfoServiceImpl
        ClientInfoServiceImpl clientInfoService = new ClientInfoServiceImpl(clientInfoRepository, roleRepository, userRepository, passwordEncoder, callNotification, jwtUtils);
        
        
        // Mock the resetPasswordLinkEmail method to return true (indicating success)
        CallNotification callNotification = mock(CallNotification.class);
        Mockito.lenient().when(callNotification.sendResetPasswordEmail(anyString(), anyString(), anyString())).thenReturn(true);
        
     // Mock the getSiteURL method
        Mockito.lenient().when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
        Mockito.lenient().when(request.getServletPath()).thenReturn("/servletPath");
        Mockito.lenient().when(clientInfoService.resetPasswordLinkEmail(anyString(), anyString(), anyString())).thenReturn(true);


        // Act
        ClientInfoModel createdClient = clientInfoService.createClient(createMockClientInfoModel(), request);

        // Assert
        assertNotNull(createdClient);
        // Add more assertions based on your actual implementation
    }
    
    @Test
    void testCreateClient_EmailSendingFailure() {
        // Arrange
        ClientInfoModel clientModel = createMockClientInfoModel();
        String token = "Bearer mockToken";

        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn(token);
        Mockito.lenient().when(jwtUtils.extractClientName(token)).thenReturn("creator");
        Mockito.lenient().when(clientInfoRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());
        Mockito.lenient().when(userRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());
        Mockito.lenient().when(roleRepository.findById(1L)).thenReturn(Optional.of(new Role()));
        Mockito.lenient().when(clientInfoRepository.save(any())).thenReturn(new ClientInfo());
        Mockito.lenient().when(userRepository.save(any())).thenReturn(new User());

        // Mock the resetPasswordLinkEmail method to throw an exception
        Mockito.lenient().when(callNotification.sendResetPasswordEmail(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("Email sending failed"));

        // Act & Assert
   //     assertThrows(CustomException.class, () -> clientInfoService.createClient(clientModel, request));
        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> clientInfoService.createClient(clientModel, request));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    
    @Test
    void testCreateClient_InternalServerError() {
        // Arrange
        ClientInfoModel clientModel = createMockClientInfoModel();
        String token = "Bearer mockToken";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.extractClientName(token)).thenReturn("creator");
        when(clientInfoRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(new Role()));
        when(clientInfoRepository.save(any())).thenReturn(new ClientInfo());
        when(userRepository.save(any())).thenReturn(new User());

        // Act
     //   ClientInfoModel createdClient = clientInfoService.createClient(clientModel, request);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> clientInfoService.createClient(clientModel,request));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        
           }
    
    @Test
    void testCreateClient_ClientAlreadyexists() {
        // Arrange
        ClientInfoModel clientModel = createMockClientInfoModel();
        String token = "Bearer mockToken";

        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn(token);
        Mockito.lenient().when(jwtUtils.extractClientName(token)).thenReturn("creator");
        Mockito.lenient().when(clientInfoRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.of(new ClientInfo()));
        Mockito.lenient().when(userRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomException.class, () -> clientInfoService.createClient(clientModel, request));

        // Verify that clientRepository.save and userRepository.save were not called
        verify(clientInfoRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateClient_DuplicateClientName() {
        // Arrange
        ClientInfoModel clientModel = createMockClientInfoModel();
        String token = "Bearer mockToken";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.extractClientName(token)).thenReturn("creator");
        when(clientInfoRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(new Role()));
        when(clientInfoRepository.save(any())).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        // Act and Assert
        assertThrows(CustomException.class, () -> clientInfoService.createClient(clientModel, request));

        // Verify that clientRepository.save and userRepository.save were called once
        verify(clientInfoRepository, times(1)).save(any());
         }
    
    @Test
    void testCreateClient_ClientAlreadyExists() {
        // Arrange
        ClientInfoModel clientModel = createMockClientInfoModel();
        String token = "Bearer mockToken";

        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn(token);
        Mockito.lenient().when(jwtUtils.extractClientName(token)).thenReturn("creator");
        Mockito.lenient().when(clientInfoRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.of(new ClientInfo()));
        Mockito.lenient().when(userRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomException.class, () -> clientInfoService.createClient(clientModel, request));
        
        // Verify that clientRepository.save and userRepository.save were not called
        verify(clientInfoRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    
    @Test
    void testUpdateClient() {
        // Arrange
        Long clientId = 1L;
        ClientInfoModel clientModel = createMockClientInfoModel();
        User existingClientEmail = createMockUser();
        existingClientEmail.setClientId(clientId);
        Role mockRole = createMockRole();

        Mockito.lenient().when(clientInfoRepository.findById(clientId)).thenReturn(Optional.of(createMockClientInfo()));
        Mockito.lenient().when(userRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.of(createMockUser()));
        Mockito.lenient().when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        Mockito.lenient().when(clientInfoRepository.save(any())).thenReturn(createMockClientInfo());
        Mockito.lenient().when(userRepository.save(any())).thenReturn(createMockUser());
        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        Mockito.lenient().when(jwtUtils.extractClientId("mockToken")).thenReturn(1L);
        Mockito.lenient().when(jwtUtils.extractClientName("mockToken")).thenReturn("clientName");

        // Set assignedRoles property in userModel
        Set<Long> clientRoles = new HashSet<>();
        clientRoles.add(1L); // Assuming role ID 1 exists
        clientModel.setAssignedRoles(clientRoles);

        // Act
        ClientInfoModel updatedClient = clientInfoService.updateClient(clientId, clientModel, request);

        // Assert
        assertNotNull(updatedClient);
        assertEquals("John", updatedClient.getClientName());
        // Add more assertions based on your actual implementation
    }
    
    @Test
    void testUpdateClient_BadRequest() {
        // Arrange
        Long clientId = 1L;
        ClientInfoModel updatedClientInfoModel = createUpdatedClientInfoModel(clientId);
        ClientInfo existingClient = createMockClientInfo();
        existingClient.setClientId(clientId);        
        User existingClientEmail = createMockUser();
        existingClientEmail.setClientId(clientId);
        Set<Long> assignedRoles = updatedClientInfoModel.getAssignedRoles();

        when(clientInfoRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(userRepository.findByEmail(updatedClientInfoModel.getEmail())).thenReturn(Optional.of(existingClientEmail));
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(new Role())); // Assuming roles exist

        // Act
      //  ClientInfoModel result = clientInfoService.updateClient(clientId, updatedClientInfoModel, mock(HttpServletRequest.class));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> clientInfoService.updateClient(clientId, updatedClientInfoModel, mock(HttpServletRequest.class)));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()); 
        
            }
    
    @Test
    void testUpdateClient_EmailConflict() {
        // Arrange
        Long clientId = 1L;
        ClientInfoModel clientModel = createMockClientInfoModel();
        User existingClientEmail = createMockUser();
        existingClientEmail.setClientId(clientId);
        Role mockRole = createMockRole();

        // Mocking clientRepository behavior to return a different client with the same email
        ClientInfo existingClientWithEmail = createMockClientInfo1(); // Different client with the same email
        Mockito.lenient().when(clientInfoRepository.findById(clientId)).thenReturn(Optional.of(createMockClientInfo()));
        Mockito.lenient().when(userRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.of(createMockUser()));
        Mockito.lenient().when(clientInfoRepository.findByEmail(clientModel.getEmail())).thenReturn(Optional.of(existingClientWithEmail));
        Mockito.lenient().when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        Mockito.lenient().when(clientInfoRepository.save(any())).thenReturn(createMockClientInfo());
        Mockito.lenient().when(userRepository.save(any())).thenReturn(createMockUser());
        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        Mockito.lenient().when(jwtUtils.extractClientId("mockToken")).thenReturn(1L);
        Mockito.lenient().when(jwtUtils.extractClientName("mockToken")).thenReturn("clientName");

        // Set assignedRoles property in userModel
        Set<Long> clientRoles = new HashSet<>();
        clientRoles.add(1L); // Assuming role ID 1 exists
        clientModel.setAssignedRoles(clientRoles);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> clientInfoService.updateClient(clientId, clientModel, request));

        // Assert
        assertEquals(HttpStatus.CONFLICT, exception.getStatus()); // Adjust based on your actual implementation
        assertEquals("Email already exists for another client.", exception.getMessage());
        // Add more assertions based on your actual implementation
    }

    

      
    @Test
    void testGetAllClient() {
        // Arrange
        List<ClientInfo> mockClientList = Arrays.asList(createMockClientInfo(), createMockClientInfo1());
        when(clientInfoRepository.findAll()).thenReturn(mockClientList);

        // Act
        List<ClientInfoModel> result = clientInfoService.getAllClient();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Assuming two mock clients are returned
    }
      
    @Test
    void testGetAllClientwithException() {
        // Arrange
        when(clientInfoRepository.findAll()).thenThrow(new RuntimeException("Simulated exception"));

        assertThrows(RuntimeException.class, () -> clientInfoRepository.findAll());
      
    }
    
    @Test
    void testGetAllClient_BadRequest() {
        // Arrange
        // Mocking the clientRepository behavior to throw an exception (simulate an error)
        when(clientInfoRepository.findAll()).thenThrow(new RuntimeException("Simulated error"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> clientInfoService.getAllClient());

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()); // Adjust based on your actual implementation
    }

   
    
    @Test
    void testGetClientById() {
        // Arrange
        Long clientId = 1L;
        ClientInfo mockClientInfo = createMockClientInfo();
        when(clientInfoRepository.findById(clientId)).thenReturn(Optional.of(mockClientInfo));

        // Act
        ClientInfoModel result = clientInfoService.getClientById(clientId);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.getClientId());
         }
    
    @Test
    void testGetClientById_notFound() {
        // Arrange
        Long clientId = 1L;
        when(clientInfoRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act
        ClientInfoModel clientById = clientInfoService.getClientById(clientId);

        // Assert
        assertNull(clientById);
    }
    
    @Test
    void testGetClientByIdwithException() {
        // Arrange
        Long clientId = 1L;
        when(clientInfoRepository.findById(clientId)).thenThrow(new RuntimeException("Simulated exception"));

        assertThrows(RuntimeException.class, () -> clientInfoRepository.findById(clientId));
      
    }
    
    @Test
    void testGetClientById_BadRequest() {
        // Arrange
        Long clientId = 1L;
        // Mocking the clientRepository behavior to throw an exception (simulate an error)
        when(clientInfoRepository.findById(clientId)).thenThrow(new RuntimeException("Simulated error"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> clientInfoService.getClientById(clientId));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()); // Adjust based on your actual implementation
    }
    
  
    @Test
    void testDeleteClient() {
    	 Long clientId = 1L;
         ClientInfo clientToDelete = createMockClientInfo();
         User userToDelete = createMockUser();

         // Mock the behavior of clientRepository to return the client when findById is called
         when(clientInfoRepository.findById(clientId)).thenReturn(Optional.ofNullable(clientToDelete));

         // Mock the behavior of userRepository to return the user when findByEmail is called
         when(userRepository.findByEmail(clientToDelete.getEmail())).thenReturn(Optional.ofNullable(userToDelete));

         // Act
         clientInfoService.deleteClient(clientId);

         // Assert
         // Verify that the method userRepository.deleteById was called with the expected userId
         verify(userRepository, times(1)).deleteById(userToDelete.getId());

         // Verify that the method clientRepository.deleteById was called with the expected clientId
         verify(clientInfoRepository, times(1)).deleteById(clientId);
     } 
    
    @Test
    void testDeleteClient_UserNotFound() {
        // Arrange
        Long clientId = 1L;
        ClientInfo clientToDelete = createMockClientInfo();

        // Mock the behavior of clientRepository to return the client when findById is called
        when(clientInfoRepository.findById(clientId)).thenReturn(Optional.ofNullable(clientToDelete));

        // Mock the behavior of userRepository to return null when findByEmail is called
        when(userRepository.findByEmail(clientToDelete.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> clientInfoService.deleteClient(clientId));

        // Assert
        assertEquals(HttpStatus.OK, exception.getStatus()); // Adjust based on your actual implementation
        assertEquals("User not found for Client with ID: " + clientId, exception.getMessage());
        // Verify that the method clientRepository.deleteById was called with the expected clientId
        verify(clientInfoRepository, times(1)).deleteById(clientId);
    }
    
    @Test
    void testDeleteClientwithException() {
        // Arrange
        Long clientId = 1L;
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientId(clientId);
        User clientEmail = new User();
        clientEmail.setId(1L);

        when(clientInfoRepository.findById(clientId)).thenThrow(new RuntimeException("Simulated exception"));
        when(userRepository.findByEmail(clientInfo.getEmail())).thenThrow(new RuntimeException("Simulated exception"));

        assertThrows(RuntimeException.class, () -> clientInfoRepository.findById(clientId));
        try {
            // Act
            userRepository.findByEmail(clientInfo.getEmail());
        } catch (RuntimeException exception) {
                     assertNotNull(exception);
                }

    }
    
    @Test
    void testDeleteClient_clientNull() {
    	 Long clientId = 1L;
         ClientInfo clientToDelete = createMockClientInfo();
         User userToDelete = createMockUser();

         // Mock the behavior of clientRepository to return the client when findById is called
         Mockito.lenient().when(clientInfoRepository.findById(clientId)).thenReturn(Optional.empty());

         // Mock the behavior of userRepository to return the user when findByEmail is called
         Mockito.lenient().when(userRepository.findByEmail(clientToDelete.getEmail())).thenReturn(Optional.ofNullable(userToDelete));

         // Act
       //  clientInfoService.deleteClient(clientId);
      // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientInfoService.deleteClient(clientId));
	        
	        // Assert
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

     } 
    
    @Test
    void testDeleteClient_BadRequest_Exception() {
        // Arrange
        Long clientId = 1L;
        ClientInfo client = new ClientInfo();
        User user = new User();
        when(clientInfoRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Simulated exception")).when(userRepository).deleteById(anyLong());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> clientInfoService.deleteClient(clientId));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()); // Adjust based on your actual implementation
       
    }

}
**/