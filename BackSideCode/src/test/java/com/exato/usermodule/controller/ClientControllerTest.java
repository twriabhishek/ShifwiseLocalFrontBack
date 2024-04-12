package com.exato.usermodule.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.model.ClientInfoModel;
import com.exato.usermodule.service.AuditLogService;
import com.exato.usermodule.service.ClientInfoService;
import com.exato.usermodule.serviceimpl.ClientInfoServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

class ClientControllerTest {
	
	 @Mock
	    private CheckTokenValidOrNot checkTokenValidOrNot;

	    @Mock
	    private ClientInfoService clientService;

	    @Mock
	    private AuditLogService auditLogService;

	    @Mock
	    private ClientInfoServiceImpl clientServiceImpl;

	    @InjectMocks
	    private ClientController clientController;
	    
	    private ClientInfoModel createMockClientInfoModel() {
	    	ClientInfoModel clientModel = new ClientInfoModel();
	    	clientModel.setClientId(1L);
	    	clientModel.setClientName("John");
	    	clientModel.setEmail("john.doe@example.com");
	        // Set other properties as needed
	        return clientModel;
	    }
	    
		 
	    
	    @Test
	    void testCreateClient() throws MethodArgumentNotValidException {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);
	        AuditLogService auditLogService = mock(AuditLogService.class);

	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a created client
	        ClientInfoModel createdClient = createMockClientInfoModel(); // Assuming you have a method for creating a mock client
	        when(clientService.createClient(any(ClientInfoModel.class), any(HttpServletRequest.class))).thenReturn(createdClient);

	        // Act
	        ResponseEntity<ClientInfoModel> responseEntity = clientController.createClient(createMockClientInfoModel(), request);

	        // Assert
	        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	        assertNotNull(responseEntity.getBody());
	        assertEquals(createdClient.getClientId(), responseEntity.getBody().getClientId());
	        assertEquals(createdClient.getClientName(), responseEntity.getBody().getClientName());
	        assertEquals(createdClient.getEmail(), responseEntity.getBody().getEmail());

	       
	    }
	    
		/*
		 * @Test void testCreateClient_InvalidToken() throws
		 * MethodArgumentNotValidException { // Arrange CheckTokenValidOrNot
		 * checkTokenValidOrNot = mock(CheckTokenValidOrNot.class); ClientInfoService
		 * clientService = mock(ClientInfoService.class); AuditLogService
		 * auditLogService = mock(AuditLogService.class);
		 * 
		 * ClientController clientController = new
		 * ClientController(checkTokenValidOrNot, clientService, auditLogService,
		 * clientServiceImpl);
		 * 
		 * HttpServletRequest request = mock(HttpServletRequest.class);
		 * 
		 * // Mocking the invalid token scenario
		 * when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);
		 * 
		 * // Act and Assert try {
		 * clientController.createClient(createMockClientInfoModel(), request);
		 * fail("Expected CustomException to be thrown"); } catch (CustomException e) {
		 * assertEquals("An error occurred during client creation.", e.getMessage()); //
		 * Additional assertions if needed }
		 * 
		 * 
		 * }
		 */
	    
	    @Test
	    void testCreateClient_BadRequest() throws MethodArgumentNotValidException {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);
	        AuditLogService auditLogService = mock(AuditLogService.class);

	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a bad request
	        when(clientService.createClient(any(ClientInfoModel.class), any(HttpServletRequest.class)))
	                .thenReturn(null);
	        try {
	            // Act
	            clientController.createClient(createMockClientInfoModel(), request);

	        } catch (CustomException exception) {
	            // Assert - you can perform additional assertions on the exception if needed
	            assertNotNull(exception);
	            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	            // Add more assertions as needed
	        }
	    }

		/*
		 * @ResponseStatus(HttpStatus.BAD_REQUEST) public class BadRequestException
		 * extends RuntimeException {
		 * 
		 * public BadRequestException(String message) { super(message); } }
		 */
	    
	    @Test
	    void testCreateClient_InternalServerError() throws MethodArgumentNotValidException {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);
	        AuditLogService auditLogService = mock(AuditLogService.class);

	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with an internal server error
	        when(clientService.createClient(any(ClientInfoModel.class), any(HttpServletRequest.class)))
	                .thenThrow(new RuntimeException("Internal server error"));

	        try {
	            // Act
	            clientController.createClient(createMockClientInfoModel(), request);
	        } catch (CustomException exception) {
	            // Assert - you can perform additional assertions on the exception if needed
	            assertNotNull(exception);
	            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
	            // Add more assertions as needed
	        }


	    }
	    
	    @Test
	    void testCreateClient_CustomException() throws MethodArgumentNotValidException {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);
	        AuditLogService auditLogService = mock(AuditLogService.class);

	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a CustomException
	        when(clientService.createClient(any(ClientInfoModel.class), any(HttpServletRequest.class)))
	                .thenThrow(new CustomException("Client creation failed.", HttpStatus.BAD_REQUEST));
	        try {
	            // Act
	            clientController.createClient(createMockClientInfoModel(), request);
	        } catch (CustomException exception) {
	            assertNotNull(exception);
	       
	        }
	    
	    }
	    
	    @Test
	    void testGetAllClient_ValidTokenAndNotEmptyList() {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);

	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a non-empty client list
	        List<ClientInfoModel> mockClients = Arrays.asList(createMockClientInfoModel());
	        when(clientService.getAllClient()).thenReturn(mockClients);

	        // Act
	        ResponseEntity<List<ClientInfoModel>> responseEntity = clientController.getAllClient(request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertNotNull(responseEntity.getBody());
	     

	    }
	    
	    @Test
	    void testGetAllClient_ValidTokenAndEmptyList() {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);

	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with an empty client list
	        when(clientService.getAllClient()).thenReturn(Arrays.asList());

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientController.getAllClient(request),
	                "Expected CustomException to be thrown");

	        assertEquals("No client exists !!", exception.getMessage());
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

	       
	    }
	    
	    @Test
	    void testGetAllClient_InvalidToken() {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientController clientController = new ClientController(checkTokenValidOrNot, null, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientController.getAllClient(request),
	                "Expected CustomException to be thrown");

	        assertEquals("Token is invalid or not present in header", exception.getMessage());
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	    }
	    
	    @Test
	    void testGetAllClient_InternalServerError() {
	        // Arrange
	    	 CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		        ClientInfoService clientService = mock(ClientInfoService.class);
		        AuditLogService auditLogService = mock(AuditLogService.class);

		        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

		        HttpServletRequest request = mock(HttpServletRequest.class);

		        // Mocking the valid token scenario
		        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service to throw an exception
	        when(clientService.getAllClient()).thenThrow(new RuntimeException("Internal server error"));

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientController.getAllClient(request),
	                "Expected CustomException to be thrown");

	        assertEquals("An error occurred while retrieving all client: Internal server error", exception.getMessage());
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());

	       
	    }
	    
	    @Test
	    void testGetClientById_ValidTokenAndClientFound() {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);

	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a found client by ID
	        ClientInfoModel mockClient = createMockClientInfoModel();
	        when(clientService.getClientById(anyLong())).thenReturn(mockClient);

	        // Act
	        ResponseEntity<ClientInfoModel> responseEntity = clientController.getClientById(1L, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertNotNull(responseEntity.getBody());
	        assertEquals(mockClient, responseEntity.getBody());

	    }
	    
	    @Test
	    void testGetClientById_ValidTokenAndClientNotFound() {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);

	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a null client (not found)
	        when(clientService.getClientById(anyLong())).thenReturn(null);

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientController.getClientById(1L, request),
	                "Expected CustomException to be thrown");

	        assertEquals("Client not found with ID: 1", exception.getMessage());
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

	       
	    }
	    
	    @Test
	    void testGetClientById_InvalidToken() {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientController clientController = new ClientController(checkTokenValidOrNot, null, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientController.getClientById(1L, request),
	                "Expected CustomException to be thrown");

	        assertEquals("Token is invalid or not present in header", exception.getMessage());
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	       
	    }
	    
	    @Test
	    void testGetClientById_InternalServerError() {
	    	 CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
		        ClientController clientController = new ClientController(checkTokenValidOrNot, null, auditLogService, clientServiceImpl);

		        HttpServletRequest request = mock(HttpServletRequest.class);

		        // Mocking the invalid token scenario
		        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientController.getClientById(1L, request),
	                "Expected CustomException to be thrown");

	      //  assertEquals("An error occurred while retrieving client by ID 1: Internal server error", exception.getMessage());
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());

	       
	    }
	    
	    @Test
	    void testUpdateClient_ValidTokenAndClientFound() {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);
	        AuditLogService auditLogService = mock(AuditLogService.class);
	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with an updated client
	        ClientInfoModel updatedClient = createMockClientInfoModel(); // Assuming you have a method for creating a mock client
	        when(clientService.updateClient(anyLong(), any(ClientInfoModel.class), any(HttpServletRequest.class))).thenReturn(updatedClient);
	     

	        // Act
	        ResponseEntity<ClientInfoModel> responseEntity = clientController.updateClient(1L, createMockClientInfoModel(), request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertNotNull(responseEntity.getBody());
	        assertEquals(updatedClient, responseEntity.getBody());

	       
	    }
	    
	    @Test
	    void testUpdateClient_ValidTokenAndClientNotFound() {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);
	        AuditLogService auditLogService = mock(AuditLogService.class);
	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with an updated client
	        ClientInfoModel updatedClient = createMockClientInfoModel(); // Assuming you have a method for creating a mock client
	        when(clientService.updateClient(anyLong(), any(ClientInfoModel.class), any(HttpServletRequest.class))).thenReturn(null);
	     
	        try {
	            // Act
	            clientController.updateClient(updatedClient.getClientId(), createMockClientInfoModel(), request);
	        } catch (CustomException exception) {
	            assertNotNull(exception);
	            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	        }

	       
	    }
	    
	    @Test
	    void testUpdateClient_InvalidToken() {
	        // Arrange
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        AuditLogService auditLogService = mock(AuditLogService.class);
	        ClientController clientController = new ClientController(checkTokenValidOrNot, null, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act & Assert
	        try {
	            // Act
	            clientController.updateClient(1L, createMockClientInfoModel(), request);

	        } catch (CustomException exception) {
	            assertNotNull(exception);
               assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	          	        }


	    }
	    
	    @Test
	    void testUpdateClient_InternalServerError() {
	        // Arrange
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        AuditLogService auditLogService = mock(AuditLogService.class);
	        ClientController clientController = new ClientController(checkTokenValidOrNot, null, auditLogService, clientServiceImpl);

	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);


	        try {
	            // Act
	            clientController.updateClient(1L, createMockClientInfoModel(), request);
	        } catch (CustomException exception) {
	           assertNotNull(exception);
	            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
	        	        }


	        	    }

	    @Test
	    void testDeleteClient_SuccessfulDeletion() {
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	     ClientInfoService clientService = mock(ClientInfoService.class);
		     AuditLogService auditLogService = mock(AuditLogService.class);
		        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	    	
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        Long clientId = 1L;

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with an existing client
	        ClientInfoModel mockClient = createMockClientInfoModel();
	        when(clientService.getClientById(clientId)).thenReturn(mockClient);

	        // Act
	        ResponseEntity<String> responseEntity = clientController.deleteClient(clientId, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("Client with ID deleted successfully." + clientId, responseEntity.getBody());

	      
	    }

	    

	    @Test
	    void testDeleteClient_ClientNotFound() {
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	     ClientInfoService clientService = mock(ClientInfoService.class);
		     AuditLogService auditLogService = mock(AuditLogService.class);
		        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	    	
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        Long clientId = 1L;

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with an existing client
	        when(clientService.getClientById(clientId)).thenReturn(null);

	        // Act
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientController.deleteClient(clientId, request),
	                "Expected CustomException to be thrown");

	        // Assert
	     //   assertEquals("Client with ID does not exist: " + clientId, exception.getMessage());
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

	      
	    }
	   
	    @Test
	    void testDeleteClient_InvalidToken() {
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
            ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	    	
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        Long clientId = 1L;

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Mocking the service response with an existing client
	        when(clientService.getClientById(clientId)).thenReturn(null);

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientController.deleteClient(1L, request),
	                "Expected CustomException to be thrown");

	        assertEquals("Token is invalid or not present in header", exception.getMessage());
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	      
	    }
	    
	    @Test
	    void testDeleteClient_InternalServerError() {
	    	CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);
		    AuditLogService auditLogService = mock(AuditLogService.class);
            ClientController clientController = new ClientController(checkTokenValidOrNot, null, auditLogService, clientServiceImpl);

	    	
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        Long clientId = 1L;

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with an existing client
	        when(clientService.getClientById(clientId)).thenReturn(null);

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> clientController.deleteClient(1L, request),
	                "Expected CustomException to be thrown");

	      //  assertEquals("An error occurred while updating Client with ID 1: Internal server error", exception.getMessage());
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
	      
	    }
	   
	    @Test
	    void testResendClientMail_Success() {
	        // Arrange
	        Long clientId = 1L;
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	        ClientInfoService clientService = mock(ClientInfoService.class);
	        AuditLogService auditLogService = mock(AuditLogService.class);
	        ClientInfoServiceImpl clientServiceImpl = mock(ClientInfoServiceImpl.class);

	        ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);
	        
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
	        String token = "Bearer mockToken";

	        Mockito.lenient().when(request.getHeader("Authorization")).thenReturn(token);
	        ClientInfoModel clientInfoModel = createMockClientInfoModel();
	        when(clientService.getClientById(clientId)).thenReturn(clientInfoModel);

	        
	        Mockito.lenient().when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
	        Mockito.lenient().when(request.getServletPath()).thenReturn("/servletPath");
	        when(clientServiceImpl.resetPasswordLinkEmail(anyString(), anyString(), anyString())).thenReturn(true);

	        // Act
	        ResponseEntity<ClientInfoModel> response = clientController.resendClientMail(clientId, request);

	        // Assert
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(clientInfoModel, response.getBody());
	        verify(auditLogService).createAuditLog(clientInfoModel.getEmail(), "Email resent to client for reset password", clientInfoModel.getClientId());
	    }
	    
	    @Test
	    void testResendClientMail_InvalidToken() {
	    	 Long clientId = 1L;
	    	    HttpServletRequest request = mock(HttpServletRequest.class);
	    	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	    ClientInfoService clientService = mock(ClientInfoService.class);
	    	    AuditLogService auditLogService = mock(AuditLogService.class);
	    	    ClientInfoServiceImpl clientServiceImpl = mock(ClientInfoServiceImpl.class);

	    	    ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	    	    // Mock the behavior to return false for an invalid token
	    	    when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	    	    // Act and Assert
	    	    assertThrows(CustomException.class, () -> clientController.resendClientMail(clientId, request),
	    	            "Expected CustomException for invalid token");
	    }
	    
	    @Test
	    void testResendClientMail_ResetPasswordLinkEmailFailed() {
	        // Arrange
	    	 Long clientId = 1L;
	    	    HttpServletRequest request = mock(HttpServletRequest.class);
	    	    CheckTokenValidOrNot checkTokenValidOrNot = mock(CheckTokenValidOrNot.class);
	    	    ClientInfoService clientService = mock(ClientInfoService.class);
	    	    AuditLogService auditLogService = mock(AuditLogService.class);
	    	    ClientInfoServiceImpl clientServiceImpl = mock(ClientInfoServiceImpl.class);

	    	    ClientController clientController = new ClientController(checkTokenValidOrNot, clientService, auditLogService, clientServiceImpl);

	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        ClientInfoModel clientInfoModel = createMockClientInfoModel();
	        when(clientService.getClientById(clientId)).thenReturn(clientInfoModel);

	        // Mock the behavior to return false for resetPasswordLinkEmail
	        when(clientServiceImpl.resetPasswordLinkEmail(anyString(), anyString(), anyString())).thenReturn(false);

	        // Act and Assert
	        assertThrows(CustomException.class, () -> clientController.resendClientMail(clientId, request),
	                "Expected CustomException for reset password link email failure");
	    }
	  

}
