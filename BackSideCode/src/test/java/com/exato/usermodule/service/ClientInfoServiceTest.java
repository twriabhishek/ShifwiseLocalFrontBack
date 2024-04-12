package com.exato.usermodule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
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

import com.exato.usermodule.entity.ClientInfo;
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.ClientInfoModel;
import com.exato.usermodule.repository.ClientInfoRepository;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.serviceimpl.ClientInfoServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
class ClientInfoServiceTest {

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
	    private PasswordEncoder passwordEncoder;
	    
	    private ClientInfoModel createMockClientInfoModel() {
	    	ClientInfoModel clientModel = new ClientInfoModel();
	    	clientModel.setClientId(1L);
	    	clientModel.setClientName("John");
	    	clientModel.setEmail("john.doe@example.com");
	        // Set other properties as needed
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
	    
	    @Test
	    void testUpdateClient() {
	        // Arrange
	        Long userId = 1L;
	        ClientInfoModel clientModel = createMockClientInfoModel();
	        Role mockRole = createMockRole();

	        when(clientInfoRepository.findById(userId)).thenReturn(Optional.of(createMockClientInfo()));
	        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
	        when(clientInfoRepository.save(any())).thenReturn(createMockClientInfo());
	        when(userRepository.save(any())).thenReturn(createMockClientInfo());
	        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
	        when(jwtUtils.extractClientId("mockToken")).thenReturn(1L);
	        when(jwtUtils.extractClientName("mockToken")).thenReturn("clientName");

	        // Set assignedRoles property in userModel
	        Set<Long> clientRoles = new HashSet<>();
	        clientRoles.add(1L); // Assuming role ID 1 exists
	        clientModel.setAssignedRoles(clientRoles);
	        // Act
	        ClientInfoModel updatedClient = clientInfoService.updateClient(userId, clientModel, request);;
	        
	        assertNotNull(updatedClient);
	        assertEquals("John", updatedClient.getClientName());
	           
	    }

	    @Test
	    void testGetAllClient_found() {
	        // Arrange
	        List<ClientInfo> mockClients = Arrays.asList(
	                createMockClientInfo(),
	                createMockClientInfo1()
	        );

	        when(clientInfoRepository.findAll()).thenReturn(mockClients);

	        // Act
	        List<ClientInfoModel> allClients = clientInfoService.getAllClient();

	        // Assert
	        assertNotNull(allClients);
	        assertEquals(2, allClients.size());

	        // Add more assertions based on your actual mapping logic
	        ClientInfoModel expectedClientModel1 = createMockClientInfoModel();
	        ClientInfoModel expectedClientModel2 = createMockClientInfoModel1();

	        assertEquals(expectedClientModel1.getClientId(), allClients.get(0).getClientId());
	        assertEquals(expectedClientModel1.getClientName(), allClients.get(0).getClientName());
	        assertEquals(expectedClientModel1.getEmail(), allClients.get(0).getEmail());

	        assertEquals(expectedClientModel2.getClientId(), allClients.get(1).getClientId());
	        assertEquals(expectedClientModel2.getClientName(), allClients.get(1).getClientName());
	        assertEquals(expectedClientModel2.getEmail(), allClients.get(1).getEmail());

	        // Reset mock interactions for a clean state in case you have more tests
	        reset(clientInfoRepository);
	    }
	    
	    @Test
	    void testGetAllClient_notFound() {
	        // Arrange
	        when(clientInfoRepository.findAll()).thenReturn(Collections.emptyList());

	        // Act
	        List<ClientInfoModel> allClients = clientInfoService.getAllClient();

	        // Assert
	        assertNotNull(allClients);
	        assertEquals(0, allClients.size());
	    }
	    
	    @Test
	    void testGetClientById_Found() {
	        // Arrange
	        Long clientId = 1L;
	        ClientInfo mockClient = createMockClientInfo();

	        when(clientInfoRepository.findById(clientId)).thenReturn(Optional.of(mockClient));

	        // Act
	        ClientInfoModel clientById = clientInfoService.getClientById(clientId);

	        // Assert
	        assertNotNull(clientById);

	        // Add more assertions based on your actual mapping logic
	        ClientInfoModel expectedClientModel = createMockClientInfoModel();

	        assertEquals(expectedClientModel.getClientId(), clientById.getClientId());
	        assertEquals(expectedClientModel.getClientName(), clientById.getClientName());
	        assertEquals(expectedClientModel.getEmail(), clientById.getEmail());

	        // Reset mock interactions for a clean state in case you have more tests
	        reset(clientInfoRepository);
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
	    
	 
	   
}
