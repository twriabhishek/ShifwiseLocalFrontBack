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
import com.exato.usermodule.entity.SkillWeightageEntity;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.SkillWeightageModel;
import com.exato.usermodule.repository.SkillWeightageRepository;
import com.exato.usermodule.service.SkillWeightageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SkillWeightageServiceImpl implements SkillWeightageService {

	private final SkillWeightageRepository skillWeightageRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public SkillWeightageServiceImpl(SkillWeightageRepository skillWeightageRepository,JwtUtils jwtUtils) {
		this.skillWeightageRepository = skillWeightageRepository;
		this.jwtUtils=jwtUtils;
		}

	@Override
	public SkillWeightageModel createSkillWeightage(SkillWeightageModel skillWeightageModel, HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				
			String skillWeightageName = skillWeightageModel.getSkillWeightageName();
				if (skillWeightageRepository.existsBySkillWeightageNameAndClientId(skillWeightageName, clientId)) {
					log.warn("[{}] Skill weightageName  already exists with the same name", new Date());
					throw new CustomException("Skill weightageName already exists with the same name",HttpStatus.CONFLICT);
				}
				SkillWeightageEntity skillWeightageEntity = new SkillWeightageEntity();
				skillWeightageModel.setClientId(clientId);
				BeanUtils.copyProperties(skillWeightageModel, skillWeightageEntity);
				skillWeightageEntity.setCreatedBy("exato");
				skillWeightageEntity.setCreatedDate(new Date());

				skillWeightageEntity = skillWeightageRepository.save(skillWeightageEntity);
				BeanUtils.copyProperties(skillWeightageEntity, skillWeightageModel);

				log.info("[{}] Skill weightageName with name '{}' created successfully", new Date(),
						skillWeightageModel.getSkillWeightageName());
				return skillWeightageModel;
		} catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating skillWeightage : {}", e.getMessage());

	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("skillWeightage name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating skillWeightage : " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating skillWeightage : {}", e.getMessage());
	        throw new CustomException("Error occurred while creating skillWeightage: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<SkillWeightageModel> getAllSkillWeightages(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<SkillWeightageModel> listOfSkillWeightages = new ArrayList<>();
			List<SkillWeightageEntity> skillWeightages = null;
				log.info("[{}] Retrieving all skillWeightages", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					skillWeightages = skillWeightageRepository.findAll();
				} else if (clientId != null) {
					skillWeightages = skillWeightageRepository.findByClientId(clientId);
				}
			
			if (skillWeightages.isEmpty()) {
				log.warn("[{}] No skillWeightages found.", new Date());
				throw new CustomException("No skillWeightages found.",HttpStatus.NOT_FOUND);
			} else {
				for (SkillWeightageEntity skillWeightage : skillWeightages) {
					SkillWeightageModel skillWeightageModel = new SkillWeightageModel();
					BeanUtils.copyProperties(skillWeightage, skillWeightageModel);
					listOfSkillWeightages.add(skillWeightageModel);
				}
				log.info("[{}] Retrieved {} Skill Weightages", new Date(), listOfSkillWeightages.size());
				return listOfSkillWeightages;
			}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all skillWeightage: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve skillWeightage: "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public SkillWeightageModel getSkillWeightageById(Long id) {
		try {
				SkillWeightageEntity skillWeightageEntity = skillWeightageRepository.findById(id)
						.orElseThrow(() -> new CustomException("Skill Weightage not found with ID: " + id,HttpStatus.NOT_FOUND));

				SkillWeightageModel skillWeightageModel = new SkillWeightageModel();
				BeanUtils.copyProperties(skillWeightageEntity, skillWeightageModel);

				log.info("[{}] Retrieved Skill Weightage with ID {}: {}", new Date(), id, skillWeightageModel);

				return skillWeightageModel;
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving skillWeightage by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve skillWeightage by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public SkillWeightageModel updateSkillWeightage(Long id, SkillWeightageModel skillWeightageModel) {
		try {
		
				SkillWeightageEntity existingSkillWeightage = skillWeightageRepository.findById(id).orElse(null);
				if (existingSkillWeightage == null) {
					log.warn("[{}] Skill weightage with ID {} not found for update", new Date(), id);
					throw new CustomException("Skill weightage not found with ID: " + id,HttpStatus.NOT_FOUND);
				} else if (skillWeightageRepository.existsBySkillWeightageNameAndClientId(
						skillWeightageModel.getSkillWeightageName(), skillWeightageModel.getClientId())) {
					log.warn("[{}] Skill weightage already exists with the same name", new Date());
					throw new CustomException("Skill weightage already exists with the same name",HttpStatus.CONFLICT);
				} else {
					BeanUtils.copyProperties(skillWeightageModel, existingSkillWeightage, "id");
					existingSkillWeightage.setUpdatedBy("exato");
					existingSkillWeightage.setUpdatedDate(new Date());
					skillWeightageRepository.save(existingSkillWeightage);

					SkillWeightageModel updatedSkillWeightageModel = new SkillWeightageModel();
					BeanUtils.copyProperties(existingSkillWeightage, updatedSkillWeightageModel);

					log.info("[{}] Skill Weightage updated successfully: {}", new Date(), id);

					return updatedSkillWeightageModel;
				}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating Skill Weightage: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update Skill Weightage : "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteSkillWeightage(Long id) {
		try {
				Optional<SkillWeightageEntity> optionalSkillWeightage = skillWeightageRepository.findById(id);
				if (optionalSkillWeightage.isPresent()) {
					skillWeightageRepository.deleteById(id);
					log.info("[{}] Skill Weightage with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("Skill Weightage with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Skill Weightage not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting Skill Weightage with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete Skill Weightage "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
