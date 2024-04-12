package com.exato.usermodule.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.SystemModel;
import com.exato.usermodule.entity.System;
import com.exato.usermodule.repository.SystemRepository;
import com.exato.usermodule.service.SystemService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SystemServiceImpl implements SystemService {

	private final SystemRepository systemRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public SystemServiceImpl(SystemRepository systemRepository,JwtUtils jwtUtils) {
		this.systemRepository = systemRepository;
		this.jwtUtils=jwtUtils;
		}

	@Override
	public SystemModel createSystem(SystemModel systemModel,HttpServletRequest request) {
		try {
			   String token = request.getHeader("Authorization");
			   String jwtToken = token.substring(7); 
		    	Long clientId = jwtUtils.extractClientId(jwtToken);
				String systemName = systemModel.getSystemName();
				if (systemRepository.existsBySystemNameAndClientId(systemName, clientId)) {
					log.warn("[{}] System already exists with the same name", new Date());
					throw new CustomException("System already exists with the same name",HttpStatus.CONFLICT);
				}
				System system = new System();
				systemModel.setClientId(clientId);
				BeanUtils.copyProperties(systemModel, system);
				system.setCreatedBy("exato");
				system.setCreatedDate(new Date());
				system = systemRepository.save(system);
				BeanUtils.copyProperties(system, systemModel);
				log.info("[{}] System with name '{}' created successfully", new Date(), systemModel.getSystemName());

				// Return the system model.
				return systemModel;
		}catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating System : {}", e.getMessage());

	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("System  name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating System : " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating System : {}", e.getMessage());
	        throw new CustomException("Error occurred while creating System: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<SystemModel> getAllSystems(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<SystemModel> listOfSystem = new ArrayList<>();
			List<System> systems = null;

				log.info("[{}] Retrieving all systems", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					systems = systemRepository.findAll();
				} else if (clientId != null) {
					systems = systemRepository.findByClientId(clientId);
				}
			if (systems.isEmpty()) {
				log.warn("[{}] No System records found.", new Date());
				throw new CustomException("No System records found.",HttpStatus.NOT_FOUND);
			} else {
				for (System system : systems) {
					SystemModel systemModel = new SystemModel();
					BeanUtils.copyProperties(system, systemModel);
					listOfSystem.add(systemModel);
				}
				log.info("[{}] Retrieved {} systems", new Date(), listOfSystem.size());

				// Return the list of system models.
				return listOfSystem;
			}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all systems: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve systems: "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public SystemModel getSystemById(Long id) {
		try {
			
				System system = systemRepository.findById(id)
						.orElseThrow(() -> new CustomException("System not found with ID: " + id,HttpStatus.NOT_FOUND));
				SystemModel systemModel = new SystemModel();
				BeanUtils.copyProperties(system, systemModel);
				log.info("[{}] Retrieved system with ID {}: {}", new Date(), id, systemModel);
				return systemModel;
			
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving System by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve System by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public SystemModel updateSystem(Long id, SystemModel systemModel,HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				System existingSystem = systemRepository.findById(id).orElse(null);
				if (existingSystem == null) {
					log.warn("[{}] System with ID {} not found for update", new Date(), id);
					throw new CustomException("System not found with ID: " + id,HttpStatus.NOT_FOUND);
				} else if (systemRepository.existsBySystemNameAndClientIdAndSystemIdNot(systemModel.getSystemName(),
						clientId, id)) {
					log.warn("[{}] System already exists with the same name", new Date());
					throw new CustomException("System already exists with the same name",HttpStatus.CONFLICT);
				} else {
					existingSystem.setUpdatedBy("exato");
					existingSystem.setUpdatedDate(new Date());
					BeanUtils.copyProperties(systemModel, existingSystem, "systemId", "clientId", "createdBy",
							"createdDate");
					systemRepository.save(existingSystem);

					SystemModel updatedSystemModel = new SystemModel();
					BeanUtils.copyProperties(existingSystem, updatedSystemModel);
					log.info("System updated successfully: {}", updatedSystemModel);

					// Return the updated system model.
					return updatedSystemModel;
				}
		
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating System: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update System : "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteSystem(Long id) {
		try {
		
				Optional<System> system = systemRepository.findById(id);
				if (system.isPresent()) {
					systemRepository.deleteById(id);
					log.info("[{}] System with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] System with ID {} not found for deletion", new Date(), id);
					throw new CustomException("System not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
			
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting business unit with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete business unit "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
