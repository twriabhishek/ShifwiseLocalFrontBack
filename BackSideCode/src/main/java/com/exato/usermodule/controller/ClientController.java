package com.exato.usermodule.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.model.ClientInfoModel;
import com.exato.usermodule.service.AuditLogService;
import com.exato.usermodule.service.ClientInfoService;
import com.exato.usermodule.serviceimpl.ClientInfoServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@RequestMapping("/client")
@Slf4j
@Validated
public class ClientController {
	
	    private final CheckTokenValidOrNot checkTokenValidOrNot;
	    private final ClientInfoService clientService;
	    private final AuditLogService auditLogService;
	    private final ClientInfoServiceImpl clientServiceImpl;

	    public ClientController(CheckTokenValidOrNot checkTokenValidOrNot, ClientInfoService clientService, AuditLogService auditLogService,ClientInfoServiceImpl clientServiceImpl) {
	        this.checkTokenValidOrNot = checkTokenValidOrNot;
	        this.clientService = clientService;
	        this.auditLogService = auditLogService;
	        this.clientServiceImpl = clientServiceImpl;
	    }
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 
	
	@PostMapping("/createclient")
	public ResponseEntity<ClientInfoModel> createClient(@Valid @RequestBody ClientInfoModel clientModel, HttpServletRequest request)
			throws MethodArgumentNotValidException {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Creating client: {}", clientModel.toString());
			ClientInfoModel createdClient = clientService.createClient(clientModel, request);

			if (createdClient != null) {
                 auditLogService.createAuditLog(createdClient.getEmail(), "Registered", createdClient.getClientId());
				log.info("Client created successfully: {}", createdClient.toString());
				return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
			} else {
				throw new CustomException("Client creation failed.", HttpStatus.BAD_REQUEST);
			}
		} catch (CustomException e) {
			String errorMessage = " CustomException: " ;
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("An error occurred during client creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during client creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getAllClient")
	public ResponseEntity<List<ClientInfoModel>> getAllClient(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Fetching all client.");

			List<ClientInfoModel> client = clientService.getAllClient();
			if (!client.isEmpty()) {
				log.info("All client retrieved successfully.");
				return ResponseEntity.status(HttpStatus.OK).body(client);
			} else {
				log.info("No client exists !");
				throw new CustomException("No client exists !!", HttpStatus.NOT_FOUND);
			}
		} catch (CustomException e) {
			String errorMessage = " CustomException: " ;
			log.error(errorMessage, e);
			throw e;
		}  catch (Exception e) {
			String errorMessage = "An error occurred while retrieving all client: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ClientInfoModel> getClientById(@PathVariable Long id,HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			ClientInfoModel client = clientService.getClientById(id);
			if (client != null) {
				return ResponseEntity.status(HttpStatus.OK).body(client);
			} else {
				throw new CustomException("Client not found with ID: " + id, HttpStatus.NOT_FOUND);
			}
		} catch (CustomException e) {
			log.error("CustomException: {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("An error occurred while retrieving client by ID {}: {}", id, e.getMessage(), e);
			throw new CustomException("An error occurred while retrieving client by ID: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ClientInfoModel> updateClient(@PathVariable Long id, @Valid @RequestBody ClientInfoModel clientModel, HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Updating user with ID {}: {}", id, clientModel.toString());

			ClientInfoModel updatedClient = clientService.updateClient(id, clientModel,request);

			if (updatedClient != null) {
				auditLogService.createAuditLog(clientModel.getEmail(), "Client Edited", clientModel.getClientId());
				log.info("Client with ID {} updated successfully.", id);
				return ResponseEntity.status(HttpStatus.OK).body(updatedClient);
			} else {
				log.info("Client not found with ID:", id);
				throw new CustomException("Client not found with ID: " + id, HttpStatus.NOT_FOUND);
			}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			String errorMessage = "An error occurred while updating Client with ID " + id + ": " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException("An error occurred while updating Client with ID: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteClient(@PathVariable Long id, HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Deleting client with ID: {}", id);

			ClientInfoModel clientById = clientService.getClientById(id);
			if (clientById != null) {
				clientService.deleteClient(id);
				auditLogService.createAuditLog(clientById.getEmail(), "Client Deleted", clientById.getClientId());
				log.info("Client with ID {} deleted successfully.", id);
				return ResponseEntity.status(HttpStatus.OK).body("Client with ID deleted successfully."+ id);
			} else {
	            log.error("Client does not exist for ID: {}", id);
	            throw new CustomException("Client with ID does not exist: " + id, HttpStatus.NOT_FOUND);
	        }
		} catch (CustomException e) {
			log.error("CustomException: {}", e.getMessage(), e);
			throw e;
		}  catch (Exception e) {
			log.error("An error occurred while deleting Client by ID {}: {}", id, e.getMessage(), e);
			throw new CustomException("An error occurred while deleting Client by ID: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/resendResetPasswordMail/{id}")
	public ResponseEntity<ClientInfoModel> resendClientMail(@PathVariable Long id,HttpServletRequest request) {
		
		if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) {
			throw new CustomException(INVALID_TOKEN_MSG, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Resending email for reset the password with ID {}:", id);
			    ClientInfoModel clientById = clientService.getClientById(id);
			    String token = request.getHeader("Authorization");
			 boolean linkEmail = clientServiceImpl.resetPasswordLinkEmail(clientById.getEmail(), getSiteURL(request), token);

			if (linkEmail) {
				auditLogService.createAuditLog(clientById.getEmail(), "Email resent to client for reset password", clientById.getClientId());
				return ResponseEntity.status(HttpStatus.OK).body(clientById);
		
			} else {
				log.info("Failed to send Email ID:", id);
				throw new CustomException("Failed to send Email to ID: " + id, HttpStatus.BAD_REQUEST);
			}
		
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			String errorMessage = "An error occurred while sending email to Client with ID " + id + ": " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException("An error occurred while sending email to  Client with ID: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

}
