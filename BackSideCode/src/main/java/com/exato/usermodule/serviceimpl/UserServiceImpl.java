package com.exato.usermodule.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import com.exato.usermodule.model.RoleModel;
import com.exato.usermodule.model.UserModel;
import com.exato.usermodule.repository.ClientInfoRepository;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.service.EmailService;
import com.exato.usermodule.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
	
	private final JwtUtils jwtUtils;
	private final UserRepository userRepository;
	private final ClientInfoRepository clientInfoRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	
	public UserServiceImpl(JwtUtils jwtUtils,UserRepository userRepository,ClientInfoRepository clientInfoRepository,RoleRepository roleRepository,PasswordEncoder passwordEncoder,EmailService emailService) {

		this.jwtUtils = jwtUtils;
		this.userRepository = userRepository;
		this.clientInfoRepository = clientInfoRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
	}
	
	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public UserModel createUser(UserModel userModel, HttpServletRequest request) {
		try {
			Set<Long> userRoles = userModel.getAssignedRoles();
			String token = request.getHeader("Authorization");
			log.info("Fetching token {}", token);
			String jwtToken = token.substring(7);
			Long clientId = jwtUtils.extractClientId(jwtToken);
            String clientName = jwtUtils.extractClientName(jwtToken);
			if (clientId != null && clientId != 0) {
				// Create the user associated with the provided clientId
				User user = new User();
				user.setClientId(clientId);
				user.setClientName(clientName);
				user.setFirstName(userModel.getFirstName());
				user.setLastName(userModel.getLastName());
				boolean existUserEmail = userRepository.findByEmail(userModel.getEmail()).isPresent();
				if (existUserEmail) {
					throw new IllegalStateException("Email already exists!!");
				}

				user.setEmail(userModel.getEmail());
				user.setPassword(passwordEncoder.encode(userModel.getPassword()));
				user.setAddress(userModel.getAddress());
				user.setState(userModel.getState());
				user.setCountry(userModel.getCountry());
				user.setPhonenumber(userModel.getPhonenumber());
				user.setBussinessnumber(userModel.getBussinessnumber());
				user.setCreatedBy(clientName);
				user.setCreatedDate(new Date(System.currentTimeMillis()));

				Set<Role> roleList = new HashSet<>();
				for (Long roleId : userRoles) {
					Role role = roleRepository.findById(roleId).orElse(null);
					if (role != null) {
						roleList.add(role);
					}
				}

				user.setAssignedRoles(roleList);
							
				user.setBusinessUnit(userModel.getBusinessUnit());
				user.setProcessUnit(userModel.getProcessUnit());
				user.setTeam(userModel.getTeam());
				user.setGroupid(userModel.getGroup());
				User savedUser = userRepository.save(user);

				UserModel savedUserModel = new UserModel();
				BeanUtils.copyProperties(savedUser, savedUserModel);
				log.info("User created successfully with email: {}", userModel.getEmail());
				boolean isEmailSent =resetPasswordLinkEmail(userModel.getEmail(), getSiteURL(request), token);
				if (!isEmailSent) {
	                log.error("Email notification failed. Rolling back the transaction.");

	                // Rollback the transaction, which will undo changes in the database
	                throw new CustomException("Email notification failed. Transaction rolled back.", HttpStatus.INTERNAL_SERVER_ERROR);
	            }
				savedUserModel.setAssignedRoles(userRoles);
				savedUserModel.setAddress(userModel.getAddress());
				savedUserModel.setPhonenumber(userModel.getPhonenumber());
				savedUserModel.setState(userModel.getState());
				savedUserModel.setCountry(userModel.getCountry());
				savedUserModel.setBussinessnumber(userModel.getBussinessnumber());
				savedUserModel.setBusinessUnit(userModel.getBusinessUnit());
				savedUserModel.setProcessUnit(userModel.getProcessUnit());
				savedUserModel.setTeam(userModel.getTeam());
				savedUserModel.setGroup(userModel.getGroup());
				return savedUserModel;
			}

			throw new IllegalStateException("Invalid user creation request.");
		} catch (IllegalStateException e) {
			log.error(e.getMessage());
			throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
		} catch (CustomException e) {
			log.error("Email notification failed. Transaction rolled back.", e.getMessage());
			throw e;
		}catch (Exception e) {
			log.error("Error occurred while creating user: {}", e.getMessage());
			throw new CustomException("Error occurred while creating user: {}"+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public List<UserModel> getAllUser() {
		try {
			List<UserModel> users = new ArrayList<>();
			List<User> findAll = userRepository.findAll();

			for (User userList : findAll) {
				UserModel model = new UserModel();
				BeanUtils.copyProperties(userList, model);

				// Copy assignedRoles from User entity to UserModel
				Set<Role> assignedRoles = userList.getAssignedRoles();
				Set<Long> roleId = new HashSet<>();
				Set<String> roleName = new HashSet<>();
				String businessName = userList.getBusinessUnit();
				String processame = userList.getProcessUnit();
				String groupName= userList.getGroupid();
				String teamName = userList.getTeam();

				for (Role role : assignedRoles) {
					RoleModel roleModel = new RoleModel();
					BeanUtils.copyProperties(role, roleModel);
					roleId.add(roleModel.getId());
					roleName.add(roleModel.getName());
				}

				model.setAssignedRoles(roleId);
				model.setAssignedRoleName(roleName);
				model.setBusinessUnit(businessName);
				model.setProcessUnit(processame);
				model.setTeam(teamName);
				model.setGroup(groupName);
				
				users.add(model);
			}

			return users;
		} catch (Exception e) {
			log.error("Error occurred while retrieving all Users: {}", e.getMessage());
			throw new CustomException("Error occurred while retrieving all Users: " + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public UserModel getUserById(Long id) {
		try {
			Thread.sleep(5000);
			User findById = userRepository.findById(id).orElse(null);
			if (findById != null) {
				UserModel userModel = new UserModel();
				BeanUtils.copyProperties(findById, userModel);

				// Copy assignedRoles from User entity to UserModel
				Set<Role> assignedRoles = findById.getAssignedRoles();
				Set<Long> roleModels = new HashSet<>();
				Set<String> roleName = new HashSet<>();
				
				String businessName = findById.getBusinessUnit();
				String processame = findById.getProcessUnit();
				String groupName= findById.getGroupid();
				String teamName = findById.getTeam();

				for (Role role : assignedRoles) {
					RoleModel roleModel = new RoleModel();
					BeanUtils.copyProperties(role, roleModel);
					roleModels.add(roleModel.getId());
					roleName.add(roleModel.getName());
				}

				userModel.setAssignedRoles(roleModels);
				userModel.setAssignedRoleName(roleName);
				userModel.setBusinessUnit(businessName);
				userModel.setProcessUnit(processame);
				userModel.setTeam(teamName);
				userModel.setGroup(groupName);
				return userModel;
			} else {
				throw new CustomException("User not found", HttpStatus.NOT_FOUND);
			}

		}  catch (InterruptedException e) {
	        Thread.currentThread().interrupt(); // Preserve interrupted status
	        log.error("Thread interrupted while retrieving user by Id: {}", e.getMessage());
	        throw new CustomException("Thread interrupted while retrieving user by Id", HttpStatus.INTERNAL_SERVER_ERROR);
	    } catch (CustomException e) {
	        log.error("User not found for Id: {}", id);
	        throw e;
	    } catch (Exception e) {
	        log.error("Error occurred while retrieving user by Id: {}", e.getMessage());
	        throw new CustomException("Error occurred while retrieving user by Id: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	}

	@Override
	public UserModel updateUser(Long id, UserModel userModel,HttpServletRequest request) {
		try {
			User user = userRepository.findById(id).orElse(null);
			
			String token = request.getHeader("Authorization");
			log.info("Fetching token {}", token);
			String jwtToken = token.substring(7);

			// Check if the updated email already exists
			User existingUserWithEmail = userRepository.findByEmail(userModel.getEmail()).orElse(null);
			if (existingUserWithEmail != null && !existingUserWithEmail.getId().equals(id)) {
				log.error("Email already exists for another user.");
				throw new CustomException("Email already exists for another user.", HttpStatus.CONFLICT);
			}

			Set<Long> assignedRoles = userModel.getAssignedRoles();
			if (user != null) {
				user.setFirstName(userModel.getFirstName());
				user.setLastName(userModel.getLastName());
				user.setEmail(userModel.getEmail());
				user.setPassword(passwordEncoder.encode(userModel.getPassword()));
				Set<Role> roleList = new HashSet<>();
				if (!assignedRoles.isEmpty()) {
					for (Long roleId : assignedRoles) {
						Role role = roleRepository.findById(roleId).orElse(null);
						if (role != null) {
							roleList.add(role);
						}
					}
				}
				user.setAssignedRoles(roleList);
				user.setBusinessUnit(userModel.getBusinessUnit());
				user.setProcessUnit(userModel.getProcessUnit());
				user.setTeam(userModel.getTeam());
				user.setGroupid(userModel.getGroup());
				user.setAddress(userModel.getAddress());
				user.setPhonenumber(userModel.getPhonenumber());
				user.setState(userModel.getState());
				user.setCountry(userModel.getCountry());
				user.setBussinessnumber(userModel.getBussinessnumber());
				user.setUpdatedBy(jwtUtils.extractClientName(jwtToken));
				user.setUpdatedDate(new Date(System.currentTimeMillis()));
				User save = userRepository.save(user);
				BeanUtils.copyProperties(save, userModel);
				return userModel;
			} else {
				return null;
			}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while updating Users: {}", e.getMessage());
			throw new CustomException("Error occurred while updating Users: " + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteUser(Long id) {
		 try {
		        User user = userRepository.findById(id).orElse(null);

		        if (user != null) {
		            userRepository.deleteById(id);
		            log.info("User with ID {} deleted successfully", id);
		            throw new SuccessException("User deleted successfully");
		        } else {
		            log.error("Error occurred while deleting user: User not found with ID: {}", id);
		            throw new CustomException("User not found with ID: " + id, HttpStatus.OK);
		        }
		    } catch (SuccessException e) {
		        // Log success information or handle accordingly
		        log.info(e.getMessage());
		    } catch (CustomException e) {
		        // Log custom exception information or handle accordingly
		        log.error(e.getMessage(), e);
		        throw e;  // Re-throw the custom exception for further handling if needed
		    } catch (Exception e) {
		        // Log general exception information or handle accordingly
		        log.error("Error occurred while deleting user with ID: {}", e.getMessage(), e);
		        throw new CustomException("Error occurred while deleting user with ID: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		    }

	}

	public boolean resetPasswordLinkEmail(String userEmail, String urlLink , String token) {
		
		return emailService.sendResetPasswordEmail(userEmail, urlLink, token);
	}

	@Override
	public List<UserModel> getAllClientIds() {
		try {
			List<UserModel> clientInfos = new ArrayList<>();
			List<ClientInfo> clientInfoList = clientInfoRepository.findAll();

			for (ClientInfo clientInfo : clientInfoList) {
				UserModel model = new UserModel();
				BeanUtils.copyProperties(clientInfo, model);
				clientInfos.add(model);
			}
			return clientInfos;
		} catch (Exception e) {
			log.error("Error occurred while retrieving all client info: {}", e.getMessage());
			throw new CustomException("Error occurred while retrieving all client info: " + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
	
	@Override
	public List<UserModel> getAllUserByClientId(Long clientId) {
	    try {
	        List<UserModel> users = new ArrayList<>();
	        List<User> allUsers = userRepository.findAllByClientId(clientId);

	        for (User user : allUsers) {
	            UserModel userModel = new UserModel();
	            BeanUtils.copyProperties(user, userModel);

	            // Copy assignedRoles from User entity to UserModel
	            Set<Long> roleModels = new HashSet<>();
	            Set<String> roleName = new HashSet<>();
	            String businessName = user.getBusinessUnit();
				String processame = user.getProcessUnit();
				String groupName= user.getGroupid();
				String teamName = user.getTeam();
	            
	            for (Role role : user.getAssignedRoles()) {
	                RoleModel roleModel = new RoleModel();
	                BeanUtils.copyProperties(role, roleModel);
	                roleModels.add(roleModel.getId());
	                roleName.add(roleModel.getName());
	            }

	            userModel.setAssignedRoles(roleModels);
	            userModel.setAssignedRoleName(roleName);
	            userModel.setBusinessUnit(businessName);
	            userModel.setProcessUnit(processame);
	            userModel.setTeam(teamName);
	            userModel.setGroup(groupName);
	            
	            users.add(userModel);
	        }
	        return users;
	    } catch (Exception e) {
	        log.error("Error occurred while retrieving users by clientId: {}", e.getMessage());
	        throw new CustomException("Error occurred while retrieving users by clientId: " + e.getMessage(),HttpStatus.BAD_REQUEST);
	    }
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Optional<User> userOptional = userRepository.findByEmail(username);
		if (userOptional.isEmpty()) {
			throw new UsernameNotFoundException("User not valid");
		}

		return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not valid"));

	}
}
