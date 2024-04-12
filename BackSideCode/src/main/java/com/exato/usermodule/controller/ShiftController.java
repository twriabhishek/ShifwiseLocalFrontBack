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
import com.exato.usermodule.model.ShiftModel;
import com.exato.usermodule.service.ShiftService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/shifts")
@Slf4j
public class ShiftController {

	@Autowired
	private ShiftService shiftService;
	
	@Autowired
	private CheckTokenValidOrNot checkTokenValidOrNot;
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 

	@PostMapping
	public ResponseEntity<ShiftModel> createShift(@Valid @RequestBody ShiftModel shiftModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			ShiftModel createdShift = (ShiftModel) shiftService.createShift(shiftModel, request);
			log.info("[{}] Created shift with name '{}'", new Date(), createdShift.getShiftName());
			return ResponseEntity.status(HttpStatus.CREATED).body(createdShift);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred during Shift creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during shift creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<ShiftModel>> getAllShifts(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			List<ShiftModel> shiftModels = shiftService.getAllShifts(request);
			log.info("[{}] Retrieved {} shifts", new Date(), shiftModels.size());
			return ResponseEntity.ok(shiftModels);
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while getting all shifts: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while getting all shifts: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ShiftModel> getShiftById(@PathVariable Long id,
			HttpServletRequest request) {
		try {
			ShiftModel shiftModel = shiftService.getShiftById(id);
			log.info("[{}] Retrieved shift with ID {}", new Date(), id);
			return ResponseEntity.ok(shiftModel);
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while reteiving shifts by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while reteiving shifts by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ShiftModel> updateShift(@PathVariable Long id, @Valid @RequestBody ShiftModel shiftModel,
			HttpServletRequest request) {
		try {
			ShiftModel updatedShift = shiftService.updateShift(id, shiftModel, request);
			log.info("[{}] Updated shift with ID {}", new Date(), id);
			return ResponseEntity.ok(updatedShift);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred while updating shifts by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating shifts by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ShiftModel> deleteShift(@PathVariable Long id, HttpServletRequest request) {
		try {
			shiftService.deleteShift(id);
			log.info("[{}] Deleted shift with ID {}", new Date(), id);
			String successMessage = "Shift with ID " + id + " has been deleted successfully.";
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
			throw new CustomException("An error occurred while deleting shifts by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
