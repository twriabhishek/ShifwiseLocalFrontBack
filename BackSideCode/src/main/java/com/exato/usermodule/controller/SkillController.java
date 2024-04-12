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
import com.exato.usermodule.model.SkillModel;
import com.exato.usermodule.service.SkillService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/skills")
@Slf4j
public class SkillController {

	@Autowired
	private SkillService skillService;
	@Autowired
	private CheckTokenValidOrNot checkTokenValidOrNot;

	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 

	@PostMapping
	public ResponseEntity<SkillModel> createSkill(@Valid @RequestBody SkillModel skillModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			SkillModel createdSkill = skillService.createSkill(skillModel, request);
			log.info("Created a new skill: {}", new Date(), createdSkill.getSkillName());
			return new ResponseEntity<>(createdSkill, HttpStatus.CREATED);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred during skill creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during skill creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<SkillModel>> getAllSkills(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			List<SkillModel> skillList = skillService.getAllSkills(request);
			log.info("Retrieved {} skills", skillList.size());
			return new ResponseEntity<>(skillList, HttpStatus.OK);
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while getting all skills: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while getting all skills: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<SkillModel> getSkillById(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			SkillModel skill = skillService.getSkillById(id);
			log.info("Retrieved skill with ID {}: {}", id, skill);
			return new ResponseEntity<>(skill, HttpStatus.OK);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while reteiving skill by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while reteiving skill by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<SkillModel> updateSkill(@PathVariable Long id, @Valid @RequestBody SkillModel skillModel,
			HttpServletRequest request) {
		

		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			SkillModel updatedSkill = skillService.updateSkill(id, skillModel);
			log.info("Updated skill with ID {}: {}", id, updatedSkill);
			return new ResponseEntity<>(updatedSkill, HttpStatus.OK);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred while updating skill by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating skill by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSkill(@PathVariable Long id, HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			skillService.deleteSkill(id);
			log.info("Deleted skill with ID {}", id);
			String successMessage = "skill with ID " + id + " has been deleted successfully.";
			throw new SuccessException(successMessage);
		}catch (SuccessException e) {
	        log.info(e.getMessage());
	        throw new SuccessException(e.getMessage());
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while deleting skill by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while deleting skill by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
