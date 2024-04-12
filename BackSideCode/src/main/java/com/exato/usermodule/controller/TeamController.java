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
import com.exato.usermodule.model.TeamModel;
import com.exato.usermodule.service.TeamService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/teams")
@Slf4j
public class TeamController {

	@Autowired
	private TeamService teamService;
	@Autowired 
	private CheckTokenValidOrNot checkTokenValidOrNot; 
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 

	@PostMapping
	public ResponseEntity<TeamModel> createTeam(@Valid @RequestBody TeamModel teamModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			TeamModel createdTeam = teamService.createTeam(teamModel, request);
			log.info("Created team with ID: {}", new Date(), createdTeam.getTeamName());
			return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred during team creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during team creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<TeamModel>> getAllTeams(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			List<TeamModel> teamList = teamService.getAllTeams(request);
			log.info("Retrieved {} teams", teamList.size());
			return new ResponseEntity<>(teamList, HttpStatus.OK);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while getting all teams: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while getting all business units: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@GetMapping("/{id}")
	public ResponseEntity<TeamModel> getTeamById(@PathVariable Long id, HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			TeamModel team = teamService.getTeamById(id, request);
			log.info("Retrieved team with ID: {}", id);
			return new ResponseEntity<>(team, HttpStatus.OK);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while reteiving teams by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while reteiving teams by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PutMapping("/{id}")
	public ResponseEntity<TeamModel> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamModel teamModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			TeamModel updatedTeam = teamService.updateTeam(id, teamModel, request);
			log.info("Updated team with ID: {}", id);
			return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred while updating teams by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating teams by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTeam(@PathVariable Long id, HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			teamService.deleteTeam(id, request);
			log.info("Deleted team with ID: {}", id);
			String successMessage = "Teams with ID " + id + " has been deleted successfully.";
			throw new SuccessException(successMessage);
		}catch (SuccessException e) {
	        log.info(e.getMessage());
	        throw new SuccessException(e.getMessage());
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while updating teams by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating teams by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
