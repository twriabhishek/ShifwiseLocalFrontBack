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
import com.exato.usermodule.entity.SkillEntity;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.SkillModel;
import com.exato.usermodule.repository.SkillRepository;
import com.exato.usermodule.service.SkillService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SkillServiceImpl implements SkillService {

	private final SkillRepository skillRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public SkillServiceImpl(SkillRepository skillRepository, JwtUtils jwtUtils) {
		this.skillRepository = skillRepository;
		this.jwtUtils = jwtUtils;
	}

	@Override
	public SkillModel createSkill(SkillModel skillModel, HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				String skillName = skillModel.getSkillName();
				if (skillRepository.existsBySkillNameAndClientId(skillName, clientId)) {
					log.warn("[{}] Skill already exists with the same name", new Date());
					throw new CustomException("Skill already exists with the same name",HttpStatus.CONFLICT);
				}

				SkillEntity skillEntity = new SkillEntity();
				skillModel.setClientId(clientId);
				BeanUtils.copyProperties(skillModel, skillEntity);
				skillEntity.setCreatedBy("exato");
				skillEntity.setCreatedDate(new Date());

				skillEntity = skillRepository.save(skillEntity);
				BeanUtils.copyProperties(skillEntity, skillModel);

				log.info("Skill created successfully: {}", skillModel);

				return skillModel;
			} catch (IllegalStateException e) {
		        log.error(e.getMessage());
		        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
		    } catch (DataIntegrityViolationException e) {
		        log.error("Error occurred while creating skill : {}", e.getMessage());

		        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
		            // Handle the case of duplicate clientName
		            throw new CustomException("Skill  name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
		        } else {
		            // Handle other types of DataIntegrityViolationException
		            throw new CustomException("Error occurred while creating skill : " + e.getMessage(), HttpStatus.CONFLICT);
		        }
		    }catch (CustomException e) {
				String errorMessage = "CustomException: " + e.getMessage();
				log.error(errorMessage, e);
				throw e;
			} catch (Exception e) {
		        log.error("Error occurred while creating skill : {}", e.getMessage());
		        throw new CustomException("Error occurred while creating skill: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		    } 
	}

	@Override
	public List<SkillModel> getAllSkills(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<SkillModel> listOfSkills = new ArrayList<>();
			List<SkillEntity> skills = null;
				log.info("[{}] Retrieving all skills", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					skills = skillRepository.findAll();
				} else if (clientId != null) {
					skills = skillRepository.findByClientId(clientId);
				}
			if (skills.isEmpty()) {
				log.warn("[{}] No skill records found.", new Date());
				throw new CustomException("No skill records found.",HttpStatus.NO_CONTENT);
			} else {
				for (SkillEntity skill : skills) {
					SkillModel skillModel = new SkillModel();
					BeanUtils.copyProperties(skill, skillModel);
					listOfSkills.add(skillModel);
				}
				log.info("[{}] Retrieved {} skills", new Date(), listOfSkills.size());
				return listOfSkills;
			}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all skills: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve skills: "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public SkillModel getSkillById(Long id) {
		try {
				SkillEntity skill = skillRepository.findById(id)
						.orElseThrow(() -> new CustomException("Skill not found with ID: " + id,HttpStatus.NO_CONTENT));
				SkillModel skillModel = new SkillModel();
				BeanUtils.copyProperties(skill, skillModel);
				log.info("[{}] Retrieved skill with ID {}: {}", new Date(), id, skillModel);
				return skillModel;
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving shill by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve skill by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public SkillModel updateSkill(Long id, SkillModel skillModel) {
		try {
			
				SkillEntity existingSkill = skillRepository.findById(id).orElse(null);
				if (existingSkill == null) {
					log.warn("[{}] Skill with ID {} not found for update", new Date(), id);
					throw new CustomException("Skill not found with ID: " + id,HttpStatus.NO_CONTENT);
				} else if (skillRepository.existsBySkillNameAndClientId(skillModel.getSkillName(),
						skillModel.getClientId())) {
					log.warn("[{}] Skill already exists with the same name", new Date());
					throw new CustomException("Skill already exists with the same name",HttpStatus.CONFLICT);
				} else {
					BeanUtils.copyProperties(skillModel, existingSkill, "id");
					existingSkill.setUpdatedBy("exato");
					existingSkill.setUpdatedDate(new Date());
					skillRepository.save(existingSkill);

					SkillModel updatedSkillModel = new SkillModel();
					BeanUtils.copyProperties(existingSkill, updatedSkillModel);
					log.info("[{}] Skill with ID {} updated successfully", new Date(), id);
					return updatedSkillModel;
				}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating skill: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update skill : "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteSkill(Long id) {
		try {
				Optional<SkillEntity> optionalSkill = skillRepository.findById(id);
				if (optionalSkill.isPresent()) {
					skillRepository.deleteById(id);
					log.info("[{}] Skill with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] Skill with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Skill not found with ID: " + id,HttpStatus.NO_CONTENT);
				}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting skill with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete skill: "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
