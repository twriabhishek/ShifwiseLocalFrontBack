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
import com.exato.usermodule.model.BusinessUnitModel;
import com.exato.usermodule.service.BusinessUnitService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/business-units")
@Slf4j
public class BusinessUnitController {

	@Autowired
	private BusinessUnitService businessUnitService;
	@Autowired 
	private CheckTokenValidOrNot checkTokenValidOrNot; 
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 

	@PostMapping
	public ResponseEntity<BusinessUnitModel> createBusinessUnit(@Valid @RequestBody BusinessUnitModel businessUnitModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			BusinessUnitModel createdBusinessUnit = businessUnitService.createBusinessUnit(businessUnitModel, request);
			log.info("[{}] Created business unit with name '{}'", new Date(),
					createdBusinessUnit.getBusinessUnitName());
			return ResponseEntity.status(HttpStatus.CREATED).body(createdBusinessUnit);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred during business unit creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during business unit creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<BusinessUnitModel>> getAllBusinessUnits(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			List<BusinessUnitModel> businessUnitList = businessUnitService.getAllBusinessUnits(request);
			log.info("[{}] Retrieved {} business units", new Date(), businessUnitList.size());
			return ResponseEntity.ok(businessUnitList);
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while getting all business units: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while getting all business units: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<BusinessUnitModel> getBusinessUnitById(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			BusinessUnitModel businessUnit = businessUnitService.getBusinessUnitById(id);
			log.info("[{}] Retrieved business unit with ID {}", new Date(), id);
			return ResponseEntity.ok(businessUnit);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while reteiving business units by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while reteiving business units by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<BusinessUnitModel> updateBusinessUnit(@PathVariable Long id,
			@Valid @RequestBody BusinessUnitModel businessUnitModel, HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			BusinessUnitModel updatedBusinessUnit = businessUnitService.updateBusinessUnit(id, businessUnitModel);
			log.info("[{}] Updated business unit with ID {}", new Date(), id);
			return ResponseEntity.ok(updatedBusinessUnit);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred while updating business units by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating business units by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<BusinessUnitModel> deleteBusinessUnit(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			businessUnitService.deleteBusinessUnit(id);
			log.info("[{}] Deleted business unit with ID {}", new Date(), id);
			String successMessage = "Business unit with ID " + id + " has been deleted successfully.";
			throw new SuccessException(successMessage);
		}catch (SuccessException e) {
	        // Log success information or handle accordingly
	        log.info(e.getMessage());
	        throw new SuccessException(e.getMessage());
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while deleting business units by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while deleting business units by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}