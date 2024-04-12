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

//import com.exato.usermodule.config.ClientCallCMS;
import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.ClientInfo;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.model.UserModel;
import com.exato.usermodule.repository.ClientInfoRepository;
import com.exato.usermodule.service.AuditLogService;
import com.exato.usermodule.service.UserService;
import com.exato.usermodule.serviceimpl.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
@Validated
@CrossOrigin
public class UserController {
	
	private final UserService userService;
	private final UserServiceImpl userServiceImpl;
	private final ClientInfoRepository clientInfoRepository;
	private final AuditLogService auditLogService;
	private final CheckTokenValidOrNot checkTokenValidOrNot;
	
	public UserController(UserService userService,UserServiceImpl userServiceImpl,ClientInfoRepository clientInfoRepository,AuditLogService auditLogService,CheckTokenValidOrNot checkTokenValidOrNot) {
		this.userService = userService;
		this.userServiceImpl = userServiceImpl;
		this.clientInfoRepository = clientInfoRepository;
		this.auditLogService = auditLogService;
		this.checkTokenValidOrNot = checkTokenValidOrNot;
	}
    
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 
	
	@GetMapping("/associatedUsers/{id}")
	public ResponseEntity<List<UserModel>> getAssociatedUsers(@PathVariable Long id, HttpServletRequest request) {

		if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) {
			throw new CustomException(INVALID_TOKEN_MSG, HttpStatus.BAD_REQUEST);
		}
		try {
			log.info("Fetching associated users for client ID: {}", id);
			ClientInfo findById = clientInfoRepository.findById(id).orElse(null);
			if (findById != null) {

				List<UserModel> associatedUsers = userService.getAllUserByClientId(id);
				if (!associatedUsers.isEmpty()) {
					log.info("Associated users fetched successfully for ID: {}", id);
					return ResponseEntity.status(HttpStatus.OK).body(associatedUsers);
				} else {
					log.info("No users associated with this client ID: {}", id);
					throw new CustomException("No users associated with this client ID: " + id, HttpStatus.NOT_FOUND);
				}
			} else {
				log.info("No client exists with ID: {}", id);
				throw new CustomException("No client exists with ID: {}\" " + id, HttpStatus.NOT_FOUND);
			}
		} catch (CustomException e) {
			String errorMessage = " CustomException: " ;
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			String errorMessage = "An error occurred while getting associated users for ID: " + id;
			log.error(errorMessage, e);
			throw new CustomException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllClientIds")
	public ResponseEntity<List<UserModel>> getDistinctClientIds(HttpServletRequest request) {

		if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) {
			throw new CustomException(INVALID_TOKEN_MSG, HttpStatus.BAD_REQUEST);
		}
		try {
			log.info("Fetching distinct client IDs.");

			List<UserModel> distinctClientIds = userService.getAllClientIds();
			if (!distinctClientIds.isEmpty()) {
				log.info("Distinct client IDs fetched successfully.");
				return ResponseEntity.status(HttpStatus.OK).body(distinctClientIds);
			} else {
				log.info("No client IDs exists !!");
				throw new CustomException("No Client exists !!", HttpStatus.NOT_FOUND);
			}
		} catch (CustomException e) {
			String errorMessage = " CustomException:" ;
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			String errorMessage = "An error occurred while retrieving distinct client IDs: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/createuser")
	public ResponseEntity<UserModel> createUser(@Valid @RequestBody UserModel userModel, HttpServletRequest request)
			throws MethodArgumentNotValidException {

		if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) {
			throw new CustomException(INVALID_TOKEN_MSG, HttpStatus.BAD_REQUEST);
		}

		try {
			log.info("Creating user: {}", userModel.toString());
			UserModel createdUser = userService.createUser(userModel, request);

			if (createdUser != null) {
				auditLogService.createAuditLog(createdUser.getEmail(), "Registered", createdUser.getClientId());
				log.info("User created successfully: {}", createdUser.toString());
				return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
			} else {
				throw new CustomException("User creation failed.", HttpStatus.BAD_REQUEST);
			}

		} catch (CustomException e) {
			String errorMessage = " CustomException: ";
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("An error occurred during user creation: {}", e.getMessage(), e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	

	@GetMapping("/getAllUser")
	public ResponseEntity<List<UserModel>> getAllUser(HttpServletRequest request) {
		
		if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) {
			throw new CustomException(INVALID_TOKEN_MSG, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Fetching all users.");
			List<UserModel> users = userService.getAllUser();
			if (!users.isEmpty()) {
				log.info("All users retrieved successfully.");
				return ResponseEntity.status(HttpStatus.OK).body(users);
			} else {
				log.info("No users exists !");
				throw new CustomException("No user exists !!", HttpStatus.NOT_FOUND);
			}
		
		} catch (CustomException e) {
			String errorMessage = " CustomException:";
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			String errorMessage = "An error occurred while retrieving all users: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserModel> getUserById(@PathVariable Long id, HttpServletRequest request) {
		
		if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) {
			throw new CustomException(INVALID_TOKEN_MSG, HttpStatus.BAD_REQUEST);
		}
		
		try {
			UserModel user = userService.getUserById(id);
			if (user != null) {
				return ResponseEntity.status(HttpStatus.OK).body(user);
			} else {
				throw new CustomException("User not found with ID: " + id, HttpStatus.NOT_FOUND);
			}
		
		} catch (CustomException e) {
			log.error(" CustomException: {} ", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("An error occurred while retrieving User by ID {}: {}", id, e.getMessage(), e);
			throw new CustomException("An error occurred while retrieving User by ID: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}
	
	@PostMapping("/resendResetPasswordMail/{id}")
	public ResponseEntity<UserModel> resendUserMail(@PathVariable Long id,HttpServletRequest request) {
		
		if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) {
			throw new CustomException(INVALID_TOKEN_MSG, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Resending email for reset the password with ID {}:", id);
			    UserModel userById = userService.getUserById(id);
			    String token = request.getHeader("Authorization");
			 boolean linkEmail = userServiceImpl.resetPasswordLinkEmail(userById.getEmail(), getSiteURL(request), token);

			if (linkEmail) {
				auditLogService.createAuditLog(userById.getEmail(), "Email resent to user for reset password", userById.getClientId());
				log.info("User with ID {} updated successfully.", id);
				return ResponseEntity.status(HttpStatus.OK).body(userById);
			} else {
				log.info("Failed to send Email ID:", id);
				throw new CustomException("Failed to send Email to ID: " + id, HttpStatus.BAD_REQUEST);
			}
		
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			String errorMessage = "An error occurred while sending email to User with ID " + id + ": " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException("An error occurred while sending email to  User with ID: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserModel> updateUser(@PathVariable Long id, @Valid @RequestBody UserModel userModel,HttpServletRequest request) {
		
		if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) {
			throw new CustomException(INVALID_TOKEN_MSG, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Updating user with ID {}: {}", id, userModel.toString());

			UserModel updatedUser = userService.updateUser(id, userModel,request);

			if (updatedUser != null) {
				auditLogService.createAuditLog(userModel.getEmail(), "User Edited", userModel.getClientId());
				log.info("User with ID {} updated successfully.", id);
				return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
			} else {
				log.info("User not found with ID:", id);
				throw new CustomException("User not found with ID: " + id, HttpStatus.NOT_FOUND);
			}
		
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			String errorMessage = "An error occurred while updating User with ID " + id + ": " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException("An error occurred while updating User with ID: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id, HttpServletRequest request) {
		
		try {
	        if (!checkTokenValidOrNot.checkTokenValidOrNot(request)) {
	            throw new CustomException(INVALID_TOKEN_MSG, HttpStatus.BAD_REQUEST);
	        }

	        log.info("Deleting user with ID: {}", id);

	        UserModel userById = userService.getUserById(id);
	        if (userById != null) {
	        	userService.deleteUser(id);
	            auditLogService.createAuditLog(userById.getEmail(), "User Deleted", userById.getClientId());
	            log.info("User with ID {} deleted successfully.", id);
	            return ResponseEntity.status(HttpStatus.OK).body("User with ID deleted successfully." + id);
	        } else {
	            log.error("User does not exist for ID: {}", id);
	            throw new CustomException("User with ID does not exist: " + id, HttpStatus.NOT_FOUND);
	        }
	    }  catch (CustomException e) {
			log.error(" CustomException: {}", e.getMessage(), e);
			throw e;
		}  catch (Exception e) {
			log.error("An error occurred while deleting User by ID {}: {}", id, e.getMessage(), e);
			throw new CustomException("An error occurred while deleting User by ID: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
/**	@GetMapping("/businessUnits")
	public ResponseEntity<List<BusinessUnitModel>> getBusinessUnits(@RequestHeader("Authorization") String token) {
		 try {
		        log.info("Fetching business units with token: {}", token);
		        
		        List<BusinessUnitModel> businessUnits = clientCallCMS.getAllBusinessUnits(token);
		        
		        if (businessUnits != null && !businessUnits.isEmpty()) {
		            log.info("Fetched business units successfully");
		            return ResponseEntity.ok(businessUnits);
		        } else {
		            log.info("No business units found for token: {}", token);
		            throw new CustomException("No business units found for the given token", HttpStatus.NOT_FOUND);
		        }
		    } catch (CustomException e) {
		        log.error(" CustomException: {}", e.getMessage(), e);
		        throw e;
		    } catch (Exception e) {
		        log.error("An error occurred while fetching business units: {}", e.getMessage(), e);
		        throw new CustomException("An error occurred while fetching business units", HttpStatus.INTERNAL_SERVER_ERROR);
		    }
	}
	
	@GetMapping("/processUnits")
    public ResponseEntity<List<ProcessUnitModel>> getProcessUnits(@RequestHeader("Authorization") String token) {
		  try {
		        log.info("Fetching process units with token: {}", token);
		        
		        List<ProcessUnitModel> processUnits = clientCallCMS.getProcessUnits(token);
		        
		        if (processUnits != null && !processUnits.isEmpty()) {
		            log.info("Fetched process units successfully");
		            return ResponseEntity.ok(processUnits);
		        } else {
		            log.info("No process units found for token: {}", token);
		            throw new CustomException("No process units found for the given token", HttpStatus.NOT_FOUND);
		        }
		    } catch (CustomException e) {
		        log.error(" CustomException: {} ", e.getMessage(), e);
		        throw e;
		    } catch (Exception e) {
		        log.error("An error occurred while fetching process units: {}", e.getMessage(), e);
		        throw new CustomException("An error occurred while fetching process units", HttpStatus.INTERNAL_SERVER_ERROR);
		    }
    }
	
	@GetMapping("/teams")
    public ResponseEntity<List<TeamModel>> getTeam(@RequestHeader("Authorization") String token) {
		 try {
		        log.info("Fetching teams with token: {}", token);
		        
		        List<TeamModel> teams = clientCallCMS.getTeams(token);
		        
		        if (teams != null && !teams.isEmpty()) {
		            log.info("Fetched teams successfully");
		            return ResponseEntity.ok(teams);
		        } else {
		            log.info("No teams found for token: {}", token);
		            throw new CustomException("No teams found for the given token", HttpStatus.NOT_FOUND);
		        }
		    } catch (CustomException e) {
		        log.error("CustomException: {}", e.getMessage(), e);
		        throw e;
		    } catch (Exception e) {
		        log.error("An error occurred while fetching teams: {}", e.getMessage(), e);
		        throw new CustomException("An error occurred while fetching teams", HttpStatus.INTERNAL_SERVER_ERROR);
		    }
    }
	
	@GetMapping("/groups")
    public ResponseEntity<List<GroupModel>> getGroup(@RequestHeader("Authorization") String token) {
		 try {
		        log.info("Fetching groups with token: {}", token);
		        
		        List<GroupModel> groups = clientCallCMS.getGroups(token);
		        
		        if (groups != null && !groups.isEmpty()) {
		            log.info("Fetched groups successfully");
		            return ResponseEntity.ok(groups);
		        } else {
		            log.info("No groups found for token: {}", token);
		            throw new CustomException("No groups found for the given token", HttpStatus.NOT_FOUND);
		        }
		    } catch (CustomException e) {
		        log.error("CustomException: {}", e.getMessage(), e);
		        throw e;
		    } catch (Exception e) {
		        log.error("An error occurred while fetching groups: {}", e.getMessage(), e);
		        throw new CustomException("An error occurred while fetching groups", HttpStatus.INTERNAL_SERVER_ERROR);
		    }
    }
**/
}
