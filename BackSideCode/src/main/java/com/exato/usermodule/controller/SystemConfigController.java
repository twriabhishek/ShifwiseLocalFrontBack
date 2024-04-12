package com.exato.usermodule.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.config.SuccessException;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.model.SystemConfigModel;
import com.exato.usermodule.service.SystemConfigService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/system-configs")
@Slf4j
public class SystemConfigController {

	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired 
	private CheckTokenValidOrNot checkTokenValidOrNot; 
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 

	@PostMapping
	public ResponseEntity<SystemConfigModel> createSystemConfig(@Valid @RequestBody SystemConfigModel systemConfigModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			SystemConfigModel createdSystemConfig = systemConfigService.createSystemConfig(systemConfigModel, request);
			log.info("Created a new system config: {}", new Date(), createdSystemConfig.getServiceName());
			return new ResponseEntity<>(createdSystemConfig, HttpStatus.CREATED);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred during system config creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during system config creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<SystemConfigModel>> getAllSystemConfigs(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			List<SystemConfigModel> systemConfigList = systemConfigService.getAllSystemConfigs(request);
			log.info("Retrieved {} system configs", systemConfigList.size());
			return new ResponseEntity<>(systemConfigList, HttpStatus.OK);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while getting all system configs: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while getting all system configs: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<SystemConfigModel> getSystemConfigById(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			SystemConfigModel systemConfig = systemConfigService.getSystemConfigById(id);
			log.info("Retrieved system config with ID {}: {}", id, systemConfig);
			return new ResponseEntity<>(systemConfig, HttpStatus.OK);
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while reteiving system config by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while reteiving system config by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<SystemConfigModel> updateSystemConfig(@PathVariable Long id,
			@Valid @RequestBody SystemConfigModel systemConfigModel, HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			SystemConfigModel updatedSystemConfig = systemConfigService.updateSystemConfig(id, systemConfigModel,
					request);
			log.info("Updated system config with ID {}: {}", id, updatedSystemConfig);
			return new ResponseEntity<>(updatedSystemConfig, HttpStatus.OK);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred while updating system config by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating system config by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSystemConfig(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			systemConfigService.deleteSystemConfig(id);
			log.info("Deleted system config with ID {}", id);
			String successMessage = "System config with ID " + id + " has been deleted successfully.";
			throw new SuccessException(successMessage);
		}catch (SuccessException e) {
	        log.info(e.getMessage());
	        throw new SuccessException(e.getMessage());
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while deleting System config by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while deleting System config by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
