package com.exato.usermodule.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.exato.usermodule.config.CallNotification;
import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.config.SuccessException;
import com.exato.usermodule.entity.ClientInfo;
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.ClientInfoModel;
import com.exato.usermodule.model.RoleModel;
import com.exato.usermodule.repository.ClientInfoRepository;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.service.ClientInfoService;
import com.exato.usermodule.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClientInfoServiceImpl implements ClientInfoService {
	
	private final ClientInfoRepository clientRepository;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final JwtUtils jwtUtils;
	
	public ClientInfoServiceImpl(ClientInfoRepository clientRepository,RoleRepository roleRepository,UserRepository userRepository,PasswordEncoder passwordEncoder,EmailService emailService,JwtUtils jwtUtils) {
		this.clientRepository = clientRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.jwtUtils = jwtUtils;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ClientInfoModel createClient(ClientInfoModel clientInfoModel, HttpServletRequest request) {
		try {
			Optional<User> existClientuser = userRepository.findByEmail(clientInfoModel.getEmail());
			Optional<ClientInfo> existClient = clientRepository.findByEmail(clientInfoModel.getEmail());
			if (!existClient.isEmpty() || !existClientuser.isEmpty()) {
				throw new IllegalStateException("Client already exists !!");
			}
            String token = request.getHeader("Authorization");
            String jwtToken = token.substring(7);
            String creator = jwtUtils.extractClientName(jwtToken);
			Set<Long> roles = clientInfoModel.getAssignedRoles();
			ClientInfo client = new ClientInfo();
			/**
			 * Long generateClientId = generateClientId(); ClientInfo present =
			 * clientRepository.findById(generateClientId).orElse(null); if (present ==
			 * null) { client.setClientId(generateClientId); } else { throw new
			 * IllegalStateException("ClientId already exists !!"); }
			 **/
			client.setSpocName(clientInfoModel.getSpocName());
			client.setClientName(clientInfoModel.getClientName());
			client.setEmail(clientInfoModel.getEmail());
			client.setPassword(passwordEncoder.encode(clientInfoModel.getPassword()));
			client.setAddress(clientInfoModel.getAddress());
			client.setPhonenumber(clientInfoModel.getPhonenumber());
			client.setBussinessnumber(clientInfoModel.getBussinessnumber());
			Set<Role> roleList = new HashSet<>();
			if (roles != null) {
				for (Long roleId : roles) {
					Role role = roleRepository.findById(roleId).orElse(null);
					if (role != null) {
						roleList.add(role);
					}
				}
			}
			client.setAssignedRoles(roleList);
			client.setCreatedBy(creator);
			client.setCreatedDate(new Date(System.currentTimeMillis()));
			ClientInfo save = clientRepository.save(client);

			// inserting data of client into user table
			log.info("Entering client data into the user table ");
			User user = new User();
			user.setClientName(clientInfoModel.getClientName());
			user.setFirstName(clientInfoModel.getSpocName());
			user.setLastName("");
			user.setEmail(clientInfoModel.getEmail());
			user.setPassword(passwordEncoder.encode(clientInfoModel.getPassword()));
			user.setAddress(clientInfoModel.getAddress());
			user.setPhonenumber(clientInfoModel.getPhonenumber());
			user.setBussinessnumber(clientInfoModel.getBussinessnumber());
			user.setBusinessUnit("");
			user.setProcessUnit("");
			user.setTeam("");
			user.setGroupid("");
			user.setCreatedBy(creator);
			user.setCreatedDate(new Date(System.currentTimeMillis()));
			user.setAssignedRoles(roleList);
			userRepository.save(user);

			BeanUtils.copyProperties(save, clientInfoModel);
			log.info("Client created successfully with email: {}", clientInfoModel.getEmail());
			boolean isEmailSent =resetPasswordLinkEmail(clientInfoModel.getEmail(), getSiteURL(request) ,token);
			if (!isEmailSent) {
                log.error("Email notification failed. Rolling back the transaction.");

                // Rollback the transaction, which will undo changes in the database
                throw new CustomException("Email notification failed. Transaction rolled back.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
			clientInfoModel.setAssignedRoles(roles);
			return clientInfoModel;

		} catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating client: {}", e.getMessage());

	        // Check if the exception is related to a duplicate clientName
	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("Client name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating client: " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    } catch (Exception e) {
	        log.error("Error occurred while creating client: {}", e.getMessage());
	        throw new CustomException("Error occurred while creating client: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	
	@Override
	public List<ClientInfoModel> getAllClient() {
		try {
			List<ClientInfoModel> client = new ArrayList<>();
			List<ClientInfo> findAll = clientRepository.findAll();

			for (ClientInfo clientList : findAll) {
				ClientInfoModel model = new ClientInfoModel();
				BeanUtils.copyProperties(clientList, model);

				// Copy assignedRoles from Client entity to ClientModel
				Set<Role> assignedRoles = clientList.getAssignedRoles();
				Set<Long> roleId = new HashSet<>();
				Set<String> roleName = new HashSet<>();

				for (Role role : assignedRoles) {
					RoleModel roleModel = new RoleModel();
					BeanUtils.copyProperties(role, roleModel);
					roleId.add(roleModel.getId());
					roleName.add(roleModel.getName());
				}
                model.setAssignedRoles(roleId);
				model.setAssignedRoleName(roleName);
				client.add(model);
			}

			return client;
		} catch (Exception e) {
			log.error("Error occurred while retrieving all Client: {}", e.getMessage());
			throw new CustomException("Error occurred while retrieving all Clients: " + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ClientInfoModel getClientById(Long id) {
		try {
			ClientInfo findById = clientRepository.findById(id).orElse(null);
			if (findById != null) {
				ClientInfoModel clientModel = new ClientInfoModel();
				BeanUtils.copyProperties(findById, clientModel);

				// Copy assignedRoles from Client entity to ClientModel
				Set<Role> assignedRoles = findById.getAssignedRoles();
				Set<Long> roleModels = new HashSet<>();
                Set<String> roleName = new HashSet<>();
				for (Role role : assignedRoles) {
					RoleModel roleModel = new RoleModel();
					BeanUtils.copyProperties(role, roleModel);
					roleModels.add(roleModel.getId());
					roleName.add(roleModel.getName());
				}

				clientModel.setAssignedRoles(roleModels);
                clientModel.setAssignedRoleName(roleName);
				return clientModel;
			} else {
				return null;
			}

		} catch (Exception e) {
			log.error("Error occurred while retrieving all Client by Id : {}", e.getMessage());
			throw new CustomException("Error occurred while retrieving all Client by Id: " + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ClientInfoModel updateClient(Long id, ClientInfoModel clientInfoModel,HttpServletRequest request) {
		try {
			ClientInfo client = clientRepository.findById(id).orElse(null);
			User clientEmail = userRepository.findByEmail(client.getEmail()).orElse(null);
			
			String token = request.getHeader("Authorization");
			log.info("Fetching token {}", token);
			String jwtToken = token.substring(7);
			
   			// Check if the updated email already exists
			ClientInfo existingClientWithEmail = clientRepository.findByEmail(clientInfoModel.getEmail()).orElse(null);
			if (existingClientWithEmail != null && !existingClientWithEmail.getClientId().equals(id)) {
				log.error("Email already exists for another client.");
				throw new CustomException("Email already exists for another client.", HttpStatus.CONFLICT);
			}

			Set<Long> assignedRoles = clientInfoModel.getAssignedRoles();
			client.setSpocName(clientInfoModel.getSpocName());
			client.setClientName(clientInfoModel.getClientName());
			client.setEmail(clientInfoModel.getEmail());
			client.setPassword(passwordEncoder.encode(clientInfoModel.getPassword()));
			client.setAddress(clientInfoModel.getAddress());
			client.setPhonenumber(clientInfoModel.getPhonenumber());
			client.setBussinessnumber(clientInfoModel.getBussinessnumber());
			client.setBusinessUnit(clientInfoModel.getBusinessUnit());
			client.setProcessUnit(clientInfoModel.getProcessUnit());
			client.setTeam(clientInfoModel.getTeam());
			client.setGroupid(clientInfoModel.getGroup());
			client.setUpdatedBy(jwtUtils.extractClientName(jwtToken));
			client.setUpdatedDate(new Date(System.currentTimeMillis()));
			Set<Role> roleList = new HashSet<>();
			if (!assignedRoles.isEmpty()) {
				for (Long roleId : assignedRoles) {
					Role role = roleRepository.findById(roleId).orElse(null);
					if (role != null) {
						roleList.add(role);
					}
				}
			}
			client.setAssignedRoles(roleList);
			ClientInfo save = clientRepository.save(client);
			
			if (clientEmail != null) {
				clientEmail.setFirstName(clientInfoModel.getClientName());
				clientEmail.setLastName("");
				clientEmail.setEmail(clientInfoModel.getEmail());
				clientEmail.setPassword(passwordEncoder.encode(clientInfoModel.getPassword()));
				clientEmail.setActive(clientInfoModel.isActive());
				clientEmail.setAddress(clientInfoModel.getAddress());
				clientEmail.setPhonenumber(clientInfoModel.getPhonenumber());
				clientEmail.setBussinessnumber(clientInfoModel.getBussinessnumber());
				clientEmail.setBusinessUnit(clientInfoModel.getBusinessUnit());
				clientEmail.setProcessUnit(clientInfoModel.getProcessUnit());
				clientEmail.setTeam(clientInfoModel.getTeam());
				clientEmail.setGroupid(clientInfoModel.getGroup());
				clientEmail.setAssignedRoles(roleList);
				
			    userRepository.save(clientEmail);
			
			}
			BeanUtils.copyProperties(save, clientInfoModel);
			return clientInfoModel;
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while updating Client: {}", e.getMessage());
			throw new CustomException("Error occurred while updating Client: " + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteClient(Long id) {
		try {
			ClientInfo client = clientRepository.findById(id).orElse(null);
	        User clientEmail = null;

	        if (client != null) {
	            clientEmail = userRepository.findByEmail(client.getEmail()).orElse(null);
	        }

	        if (client != null && clientEmail != null) {
	            userRepository.deleteById(clientEmail.getId());
	            clientRepository.deleteById(id);
	            log.info("Client with ID {} and corresponding user deleted successfully", id);

	            // Create a success response
	            throw new SuccessException("Client and user deleted successfully");
	        } else if (client != null && clientEmail == null) {
	            // Client is present, but user is not found
	        	clientRepository.deleteById(id);
	            log.error("Client not present as a user", id);
	            throw new CustomException("User not found for Client with ID: " + id, HttpStatus.OK);
	        } else {
	            // Client not found
	            log.error("Error occurred while deleting client: Client not found with ID: {}", id);
	            throw new CustomException("Client not found with ID: " + id, HttpStatus.NOT_FOUND);
	        }
		    
		    
		}catch (SuccessException e) {
	        // Log success information or handle accordingly
	        log.info(e.getMessage());
	    } catch (CustomException e) {
	        log.error("Error occurred while deleting client: {}", e.getMessage());
	        throw e;

	    } catch (Exception e) {
			log.error("Error occurred while deleting Client with ID: {}", e.getMessage());
			throw new CustomException("Error occurred while deleting client with ID: " + e.getMessage(),HttpStatus.BAD_REQUEST);
		}

	}

	public boolean resetPasswordLinkEmail(String userEmail, String urlLink,String token) {
		return emailService.sendResetPasswordEmail(userEmail, urlLink, token);
		}
		
}
