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
import com.exato.usermodule.model.SkillWeightageModel;
import com.exato.usermodule.service.SkillWeightageService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/skill-weightages")
@Slf4j
public class SkillWeightageController {

	@Autowired
	private SkillWeightageService skillWeightageService;
	@Autowired
	private CheckTokenValidOrNot checkTokenValidOrNot;
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header";

	@PostMapping
	public ResponseEntity<SkillWeightageModel> createSkillWeightage(
			@Valid @RequestBody SkillWeightageModel skillWeightageModel,HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			SkillWeightageModel createdSkillWeightage = skillWeightageService.createSkillWeightage(skillWeightageModel,
					request);
			log.info("Created a new skill weightage: {}", new Date(), createdSkillWeightage.getSkillWeightageName());
			return new ResponseEntity<>(createdSkillWeightage, HttpStatus.CREATED);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred during skill weightage creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during skill weightage creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<SkillWeightageModel>> getAllSkillWeightages(
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			List<SkillWeightageModel> skillWeightageList = skillWeightageService.getAllSkillWeightages(request);
			log.info("Retrieved {} skill weightages", skillWeightageList.size());
			return new ResponseEntity<>(skillWeightageList, HttpStatus.OK);
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while getting all skill weightages: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while getting all skill weightages: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<SkillWeightageModel> getSkillWeightageById(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			SkillWeightageModel skillWeightage = skillWeightageService.getSkillWeightageById(id);
			log.info("Retrieved skill weightage with ID {}: {}", id, skillWeightage);
			return new ResponseEntity<>(skillWeightage, HttpStatus.OK);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while reteiving skill weightage by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while reteiving skill weightage by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<SkillWeightageModel> updateSkillWeightage(@PathVariable Long id,
			@Valid @RequestBody SkillWeightageModel skillWeightageModel, HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			SkillWeightageModel updatedSkillWeightage = skillWeightageService.updateSkillWeightage(id,
					skillWeightageModel);
			log.info("Updated skill weightage with ID {}: {}", id, updatedSkillWeightage);
			return new ResponseEntity<>(updatedSkillWeightage, HttpStatus.OK);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred while updating skill weightage by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating skill weightage by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<SkillWeightageModel> deleteSkillWeightage(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			skillWeightageService.deleteSkillWeightage(id);
			log.info("Deleted skill weightage with ID {}", id);
			String successMessage = "skill weightage with ID " + id + " has been deleted successfully.";
			throw new SuccessException(successMessage);
		}catch (SuccessException e) {
	        log.info(e.getMessage());
	        throw new SuccessException(e.getMessage());
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while deleting skill weightage by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while deleting skill weightage by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
