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
import com.exato.usermodule.entity.SubProcess;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.SubProcessModel;
import com.exato.usermodule.repository.SubProcessRepository;
import com.exato.usermodule.service.SubProcessService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubProcessServiceImpl implements SubProcessService {

	private final SubProcessRepository subProcessRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public SubProcessServiceImpl(SubProcessRepository subProcessRepository,JwtUtils jwtUtils) {
		this.subProcessRepository = subProcessRepository;
		this.jwtUtils = jwtUtils;

	}

	@Override
	public SubProcessModel createSubProcess(SubProcessModel subProcessModel, HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String subProcessName = subProcessModel.getSubProcessName();
				if (subProcessRepository.existsBySubProcessNameAndClientId(subProcessName, clientId)) {
					log.warn("[{}] Sub Process already exists with the same name", new Date());
					throw new CustomException("Sub Process already exists with the same name",HttpStatus.CONFLICT);
				}

				// Create a new business unit entity.
				SubProcess subProcessEntity = new SubProcess();
				subProcessModel.setClientId(clientId);
				BeanUtils.copyProperties(subProcessModel, subProcessEntity);
				subProcessEntity.setCreatedBy("exato");
				subProcessEntity.setCreatedDate(new Date());

				subProcessEntity = subProcessRepository.save(subProcessEntity);
				BeanUtils.copyProperties(subProcessEntity, subProcessModel);

				log.info("[{}] Business unit with name '{}' created successfully", new Date(),
						subProcessModel.getSubProcessName());
				return subProcessModel;
		} catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating business unit : {}", e.getMessage());

	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("business unit  name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating business unit : " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating business unit : {}", e.getMessage());
	        throw new CustomException("Error occurred while creating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<SubProcessModel> getAllSubProcesses(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<SubProcessModel> listOfSubProcesses = new ArrayList<>();
			List<SubProcess> subProcesses = null;
			log.info("[{}] Retrieving all sub processes", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					subProcesses = subProcessRepository.findAll();
				} else if (clientId != null) {
					subProcesses = subProcessRepository.findByClientId(clientId);
				}
			if (subProcesses.isEmpty()) {
				log.warn("[{}] No sub process found.", new Date());
				throw new CustomException("No sub process found.",HttpStatus.NOT_FOUND);
			} else {
				for (SubProcess subProcess : subProcesses) {
					SubProcessModel subProcessModel = new SubProcessModel();
					BeanUtils.copyProperties(subProcess, subProcessModel);
					listOfSubProcesses.add(subProcessModel);
				}
				log.info("[{}] Retrieved {} Sub Processes", new Date(), listOfSubProcesses.size());
				return listOfSubProcesses;
			}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all Sub Processes: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve Sub Processes "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public SubProcessModel getSubProcessById(Long id) {
		try {
			
				SubProcess subProcessEntity = subProcessRepository.findById(id)
						.orElseThrow(() -> new CustomException("Sub Process not found with ID: " + id,HttpStatus.NOT_FOUND));
				SubProcessModel subProcessModel = new SubProcessModel();
				BeanUtils.copyProperties(subProcessEntity, subProcessModel);
				log.info("[{}] Retrieved sub process with ID {}: {}", new Date(), id, subProcessModel);
				return subProcessModel;
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving sub process by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve sub process by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public SubProcessModel updateSubProcess(Long id, SubProcessModel subProcessModel) {
		try {
				SubProcess existingSubProcess = subProcessRepository.findById(id).orElse(null);
				if (existingSubProcess == null) {
					log.warn("[{}] sub process with ID {} not found for update", new Date(), id);
					throw new CustomException("sub process not found with ID: " + id,HttpStatus.NOT_FOUND);
				} else if (subProcessRepository.existsBySubProcessNameAndClientId(subProcessModel.getSubProcessName(),
						subProcessModel.getClientId())) {
					log.warn("[{}] sub process already exists with the same name", new Date());
					throw new CustomException("sub process already exists with the same name",HttpStatus.CONFLICT);
				} else {
					BeanUtils.copyProperties(subProcessModel, existingSubProcess, "id");
					existingSubProcess.setUpdatedBy("exato");
					existingSubProcess.setUpdatedDate(new Date());
					subProcessRepository.save(existingSubProcess);

					SubProcessModel updatedSubProcessModel = new SubProcessModel();
					BeanUtils.copyProperties(existingSubProcess, updatedSubProcessModel);

					log.info("Sub Process updated successfully: {}", updatedSubProcessModel);

					return updatedSubProcessModel;
				}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating Sub Process: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update Sub Process : "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteSubProcess(Long id) {
		try {
		
				Optional<SubProcess> optionalSubProcess = subProcessRepository.findById(id);
				if (optionalSubProcess.isPresent()) {
					subProcessRepository.deleteById(id);
					log.info("[{}] Sub Process with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] Sub Process with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Sub Process not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting Sub Process with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete Sub Process "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
