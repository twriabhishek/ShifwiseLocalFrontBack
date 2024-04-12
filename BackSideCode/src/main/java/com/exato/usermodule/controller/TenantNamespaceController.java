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
import com.exato.usermodule.model.TenantNamespaceModel;
import com.exato.usermodule.service.TenantNamespaceService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tenant-namespaces")
@Slf4j
public class TenantNamespaceController {

	@Autowired
	private TenantNamespaceService tenantNamespaceService;
	@Autowired 
	private CheckTokenValidOrNot checkTokenValidOrNot; 
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 

	@PostMapping
	public ResponseEntity<TenantNamespaceModel> createTenantNamespaceModel(
			@Valid @RequestBody TenantNamespaceModel tenantNamespaceModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			TenantNamespaceModel createdTenantNamespaceModel = tenantNamespaceService
					.createTenantNamespaceModel(tenantNamespaceModel, request);
			log.info("Created Tenant Namespace with ID: {}", new Date(),
					createdTenantNamespaceModel.getNamespaceName());
			return new ResponseEntity<>(createdTenantNamespaceModel, HttpStatus.CREATED);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred during Tenant Namespace creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during Tenant Namespace creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<TenantNamespaceModel>> getAllTenantNamespaceModels(
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			List<TenantNamespaceModel> tenantNamespaceModelList = tenantNamespaceService
					.getAllTenantNamespaceModels(request);
			log.info("Retrieved {} Tenant Namespaces", new Date(), tenantNamespaceModelList.size());
			return new ResponseEntity<>(tenantNamespaceModelList, HttpStatus.OK);
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while getting all Tenant Namespaces: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while getting all Tenant Namespaces: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<TenantNamespaceModel> getTenantNamespaceModelById(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			TenantNamespaceModel tenantNamespaceModel = tenantNamespaceService.getTenantNamespaceModelById(id);
			log.info("Retrieved Tenant Namespace with ID: {}", id);
			return new ResponseEntity<>(tenantNamespaceModel, HttpStatus.OK);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while reteiving Tenant Namespace by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while reteiving Tenant Namespace by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<TenantNamespaceModel> updateTenantNamespaceModel(@PathVariable Long id,
			@Valid @RequestBody TenantNamespaceModel tenantNamespaceModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			TenantNamespaceModel updatedTenantNamespaceModel = tenantNamespaceService.updateTenantNamespaceModel(id,
					tenantNamespaceModel, request);
			log.info("Updated Tenant Namespace with ID: {}", id);
			return new ResponseEntity<>(updatedTenantNamespaceModel, HttpStatus.OK);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred while updating Tenant Namespace by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating Tenant Namespace by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTenantNamespaceModel(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			tenantNamespaceService.deleteTenantNamespaceModel(id);
			log.info("Deleted Tenant Namespace with ID: {}", id);
			String successMessage = "Tenant Namespace with ID " + id + " has been deleted successfully.";
			throw new SuccessException(successMessage);
		}catch (SuccessException e) {
	        log.info(e.getMessage());
	        throw new SuccessException(e.getMessage());
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while deleting Tenant Namespace by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while deleting Tenant Namespace by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
