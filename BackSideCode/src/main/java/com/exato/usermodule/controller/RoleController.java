package com.exato.usermodule.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.model.RoleModel;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.service.RoleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/role")
@CrossOrigin
@Slf4j
public class RoleController {
	
	private final CheckTokenValidOrNot checkTokenValidOrNot;
	private final RoleService roleService;
	private final RoleRepository roleRepository;
	
	public RoleController(CheckTokenValidOrNot checkTokenValidOrNot,RoleService roleService,RoleRepository roleRepository) {
		this.checkTokenValidOrNot = checkTokenValidOrNot;
		this.roleService = roleService;
		this.roleRepository = roleRepository;
	}
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 

	@PostMapping("/createrole")
	public ResponseEntity<RoleModel> createRole(@Valid @RequestBody RoleModel roleModel,HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Creating role: {}", roleModel.toString());
			RoleModel createdRole = roleService.createRole(roleModel);

			if (createdRole != null) {
				log.info("Role created successfully: {}", createdRole.toString());
				return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);		
				} else {
				throw new CustomException("Role creation failed.",HttpStatus.BAD_REQUEST);
				
			}
			
		} catch (CustomException e) {
			String errorMessage = " CustomException:" ;
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("An error occurred during role creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during role creation.",HttpStatus.INTERNAL_SERVER_ERROR);
					
		}
	}

	@GetMapping
	public ResponseEntity<List<RoleModel>> getAllRoles(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			List<RoleModel> roles = roleService.getAllRole();

			if (roles != null) {
				return ResponseEntity.status(HttpStatus.OK).body(roles);	
			} else {
				throw new CustomException("Error occurred while retrieving all roles.",HttpStatus.OK);
			}
		} catch (CustomException e) {
			String errorMessage = " CustomException: ";
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("An error occurred while retrieving all roles: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while retrieving all roles.",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<RoleModel> getRoleById(@PathVariable Long id,HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			RoleModel role = roleService.getRoleById(id);

			if (role != null) {
				return ResponseEntity.status(HttpStatus.OK).body(role);	
			} else {
				throw new CustomException("Role not found with ID: " + id, HttpStatus.OK);
			}
		} catch (CustomException e) {
			String errorMessage = " CustomException: " ;
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("An error occurred while retrieving role by ID {}: {}", id, e.getMessage(), e);
			throw new CustomException("An error occurred while retrieving role by ID ",HttpStatus.INTERNAL_SERVER_ERROR);
					
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<RoleModel> updateRole(@PathVariable Long id, @RequestBody RoleModel roleModel,HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			RoleModel updatedRole = roleService.updateRole(id, roleModel);

			if (updatedRole != null) {
				return ResponseEntity.status(HttpStatus.OK).body(updatedRole);
			} else {
				throw new CustomException("Role not found with ID: " + id,HttpStatus.OK);
			}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("An error occurred while updating role: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating role with ID: ",HttpStatus.INTERNAL_SERVER_ERROR);
				
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteRole(@PathVariable Long id,HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info("Deleting role with ID: {}", id);
			
			Role findById = roleRepository.findById(id).orElse(null);
			
			if (findById != null) {
				roleService.deleteRole(id);
				return ResponseEntity.status(HttpStatus.OK).body("Role deleted successfylly !!" + id);	
			
			} else {
				log.error("Role does not exist !! " + id);
				throw new CustomException("Role does not exist with ID: " + id,HttpStatus.OK);
			}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			String errorMessage = "An error occurred while deleting Role with ID " + id + ": " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException("An error occurred while deleting Role with ID: " + id,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
