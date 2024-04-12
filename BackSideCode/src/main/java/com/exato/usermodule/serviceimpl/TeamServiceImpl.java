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
import com.exato.usermodule.entity.Teams;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.TeamModel;
import com.exato.usermodule.repository.TeamRepository;
import com.exato.usermodule.service.TeamService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TeamServiceImpl implements TeamService {

	private final TeamRepository teamRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public TeamServiceImpl(TeamRepository teamRepository,JwtUtils jwtUtils) {
		this.teamRepository = teamRepository;
		this.jwtUtils = jwtUtils;
	}

	@Override
	public TeamModel createTeam(TeamModel teamModel, HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String teamName = teamModel.getTeamName();
				if (teamRepository.existsByTeamNameAndClientId(teamName, clientId)) {
					log.warn("[{}] Team already exists with the same name", new Date());
					throw new CustomException("Team already exists with the same name",HttpStatus.CONFLICT);
				}
				Teams teamEntity = new Teams();
				teamModel.setClientId(clientId);
				BeanUtils.copyProperties(teamModel, teamEntity);
				teamEntity.setCreatedBy("exato");
				teamEntity.setCreatedDate(new Date());
				teamEntity = teamRepository.save(teamEntity);

				TeamModel createdTeamModel = new TeamModel();
				BeanUtils.copyProperties(teamEntity, createdTeamModel);
				log.info("[{}] Team with name '{}' created successfully", new Date(), teamModel.getTeamName());

				// Return the created team model.
				return createdTeamModel;
		
		}catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating teams : {}", e.getMessage());

	        // Check if the exception is related to a duplicate business unit 
	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("Teams name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating teams : " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating teams: {}", e.getMessage());
	        throw new CustomException("Error occurred while creating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<TeamModel> getAllTeams(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<TeamModel> listOfTeam = new ArrayList<>();
			List<Teams> teams = null;
				log.info("[{}] Retrieving all teams", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					teams = teamRepository.findAll();
				} else if (clientId != null) {
					teams = teamRepository.findByClientId(clientId);
				}
			if (teams.isEmpty()) {
				log.warn("[{}] No Team records found.", new Date());
				throw new CustomException("No Team records found.",HttpStatus.NO_CONTENT);
			} else {
				for (Teams team : teams) {
					TeamModel teamModel = new TeamModel();
					BeanUtils.copyProperties(team, teamModel);
					listOfTeam.add(teamModel);
				}
				log.info("[{}] Retrieved {} teams", new Date(), listOfTeam.size());
				return listOfTeam;
			}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all teams: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve teams "+e.getMessage(),HttpStatus.BAD_REQUEST);
		} 
		
	}

	@Override
	public TeamModel getTeamById(Long id, HttpServletRequest request) {
		try {
				Teams teamEntity = teamRepository.findById(id)
						.orElseThrow(() -> new CustomException("Team not found with ID: " + id,HttpStatus.NOT_FOUND));
				TeamModel teamModel = new TeamModel();
				BeanUtils.copyProperties(teamEntity, teamModel);
				log.info("Retrieved Team with ID {}: {}", new Date(), id, teamModel);
				return teamModel;
	
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving team by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve team by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@Override
	public TeamModel updateTeam(Long id, TeamModel teamModel, HttpServletRequest request) {
		try {
	
			Teams existingTeam = teamRepository.findById(id).orElse(null);

				if (existingTeam == null) {
					log.warn("[{}] Team with ID {} not found for update", new Date(), id);
					throw new CustomException("Team not found with ID: " + id,HttpStatus.NOT_FOUND);
				} else if (teamRepository.existsByTeamNameAndClientId(teamModel.getTeamName(),
						teamModel.getClientId())) {
					log.warn("[{}] Team already exists with the same name", new Date());
					throw new CustomException("Team already exists with the same name",HttpStatus.CONFLICT);
				} else {
					BeanUtils.copyProperties(teamModel, existingTeam, "id");
					existingTeam.setUpdatedBy("exato");
					existingTeam.setUpdatedDate(new Date());
					existingTeam = teamRepository.save(existingTeam);

					TeamModel updatedTeamModel = new TeamModel();
					BeanUtils.copyProperties(existingTeam, updatedTeamModel);
					log.info("[{}] Team with ID {} updated successfully", new Date(), id);

					// Return the updated team model.
					return updatedTeamModel;
				}
	
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating team: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update team "+e.getMessage() ,HttpStatus.BAD_REQUEST);
		} 
		
	}

	@Override
	public void deleteTeam(Long id, HttpServletRequest request) {
		try {

				Optional<Teams> team = teamRepository.findById(id);
				if (team.isPresent()) {
					teamRepository.deleteById(id);
					log.info("[{}] Team with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] Team with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Team not found with ID: " + id,HttpStatus.NOT_FOUND);
				}

		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting team with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete team "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		
	}
}
