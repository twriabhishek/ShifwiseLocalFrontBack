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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.config.SuccessException;
import com.exato.usermodule.model.ProcessUnitModel;
import com.exato.usermodule.service.ProcessUnitService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/process-units")
@Slf4j
public class ProcessUnitController {

	@Autowired
	private ProcessUnitService processUnitService;

	@PostMapping
	public ResponseEntity<ProcessUnitModel> createProcessUnit(@Valid @RequestBody ProcessUnitModel processUnitModel,
			HttpServletRequest request) {
		try {
			ProcessUnitModel createdProcessUnit = processUnitService.createProcessUnit(processUnitModel, request);
			log.info("Created a new process unit: {}", new Date(), createdProcessUnit.getProcessUnitName());
			return new ResponseEntity<>(createdProcessUnit, HttpStatus.CREATED);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred during process unit creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during process unit creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<ProcessUnitModel>> getAllGroups(HttpServletRequest request) {
		try {
			List<ProcessUnitModel> processUnitsList = processUnitService.getAllProcessUnits(request);
			log.info("Retrieved {} processUnits", processUnitsList.size());
			return ResponseEntity.ok(processUnitsList);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while retrieving processUnits: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while retrieving processUnits: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProcessUnitModel> getProcessUnitById(@PathVariable Long id) {
		try {
			ProcessUnitModel processUnit = processUnitService.getProcessUnitById(id);
			log.info("Retrieved process unit with ID {}: {}", id, processUnit);
			return new ResponseEntity<>(processUnit, HttpStatus.OK);
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while retrieving processUnit:  {}", e.getMessage(), e);
			throw new CustomException("An error occurred while retrieving processUnit:  " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProcessUnitModel> updateProcessUnit(@PathVariable Long id,
			@Valid @RequestBody ProcessUnitModel processUnitModel, HttpServletRequest request) {
		try {
			ProcessUnitModel updatedProcessUnit = processUnitService.updateProcessUnit(id, processUnitModel, request);
			log.info("Updated process unit with ID {}: {}", id, updatedProcessUnit);
			return new ResponseEntity<>(updatedProcessUnit, HttpStatus.OK);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred while retrieving processUnit:  {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating processUnit:  " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ProcessUnitModel> deleteProcessUnit(@PathVariable Long id,
			@RequestHeader("Authorization") String token) {
		try {
			processUnitService.deleteProcessUnit(id, token);
			log.info("Deleted process unit with ID {}", id);
			String successMessage = "Process unit with ID " + id + " has been deleted successfully.";
			throw new SuccessException(successMessage);
		} catch (SuccessException e) {
	        // Log success information or handle accordingly
	        log.info(e.getMessage());
	        throw new SuccessException(e.getMessage());
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while deleting processUnit:  {}", e.getMessage(), e);
			throw new CustomException("An error occurred while deleting processUnit:  " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
