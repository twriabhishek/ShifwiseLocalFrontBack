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
import com.exato.usermodule.entity.ProcessUnit;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.ProcessUnitModel;
import com.exato.usermodule.repository.ProcessUnitRepository;
import com.exato.usermodule.service.ProcessUnitService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessUnitServiceImpl implements ProcessUnitService {

	private final ProcessUnitRepository processUnitRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public ProcessUnitServiceImpl(ProcessUnitRepository processUnitRepository,
			JwtUtils jwtUtils) {
		this.processUnitRepository = processUnitRepository;
		this.jwtUtils = jwtUtils;
	}

	@Override
	public ProcessUnitModel createProcessUnit(ProcessUnitModel processUnitModel, HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String processUnitName = processUnitModel.getProcessUnitName();

				if (processUnitRepository.existsByProcessUnitNameAndClientId(processUnitName, clientId)) {
					log.warn("[{}] Process unit already exists with the same name", new Date());
					throw new CustomException("Process unit already exists with the same name",HttpStatus.CONFLICT);
				}
				ProcessUnit processUnitEntity = new ProcessUnit();
				processUnitModel.setClientId(clientId);
				BeanUtils.copyProperties(processUnitModel, processUnitEntity);
				processUnitEntity.setCreatedBy("exato");
				processUnitEntity.setCreatedDate(new Date());

				processUnitEntity = processUnitRepository.save(processUnitEntity);

				BeanUtils.copyProperties(processUnitEntity, processUnitModel);

				log.info("[{}] Process unit created successfully: {}", new Date(), processUnitModel);
				return processUnitModel;
		} catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating process unit : {}", e.getMessage());

	        // Check if the exception is related to a duplicate business unit 
	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("process unit  name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating process unit : " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    } catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
	        log.error("Error occurred while creating process unit : {}", e.getMessage());
	        throw new CustomException("Error occurred while process user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<ProcessUnitModel> getAllProcessUnits(HttpServletRequest request) {
		try {

			List<ProcessUnitModel> listOfProcessUnit = new ArrayList<>();
			List<ProcessUnit> processUnits = null;
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);

				log.info("[{}] Retrieving all process units", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					processUnits = processUnitRepository.findAll();
				} else if (clientId != null) {
					processUnits = processUnitRepository.findByClientId(clientId);
				}

			if (processUnits.isEmpty()) {
				log.warn("[{}] No process units found.", new Date());
				throw new CustomException("No process units found.",HttpStatus.NO_CONTENT);
			} else {
				for (ProcessUnit processUnit : processUnits) {
					ProcessUnitModel processUnitModel = new ProcessUnitModel();
					BeanUtils.copyProperties(processUnit, processUnitModel);
					listOfProcessUnit.add(processUnitModel);
				}
				log.info("[{}] Retrieved {} process units", new Date(), listOfProcessUnit.size());
				return listOfProcessUnit;

			}
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all process units: {}", new Date(), e.getMessage(), e);
			throw new CustomException("Failed to retrieve process units "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ProcessUnitModel getProcessUnitById(Long id) {
		try {

				ProcessUnit processUnit = processUnitRepository.findById(id)
						.orElseThrow(() -> new CustomException("Process unit not found with ID: " + id,HttpStatus.NO_CONTENT));
				ProcessUnitModel processUnitModel = new ProcessUnitModel();
				BeanUtils.copyProperties(processUnit, processUnitModel);
				log.info("[{}] Retrieved process unit with ID {}: {}", new Date(), id, processUnitModel);
				return processUnitModel;

		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving a process unit by ID {}: {}", new Date(), id,
					e.getMessage(), e);
			throw new CustomException("Failed to retrieve process unit by ID "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ProcessUnitModel updateProcessUnit(Long id, ProcessUnitModel processUnitModel, HttpServletRequest request) {
		try {
	
				ProcessUnit existingProcessUnit = processUnitRepository.findById(id).orElse(null);

				if (existingProcessUnit == null) {
					log.warn("[{}] Process unit with ID {} not found for update", new Date(), id);
					throw new CustomException("Process unit not found with ID: " + id,HttpStatus.NO_CONTENT);
				} else if (processUnitRepository.existsByProcessUnitNameAndClientId(
						processUnitModel.getProcessUnitName(), processUnitModel.getClientId())) {
					log.warn("[{}] Process unit already exists with the same name", new Date());
					throw new CustomException("Process unit already exists with the same name",HttpStatus.CONFLICT);
				} else {
					BeanUtils.copyProperties(processUnitModel, existingProcessUnit, "id");
					existingProcessUnit.setUpdatedBy("exato");
					existingProcessUnit.setUpdatedDate(new Date());
					processUnitRepository.save(existingProcessUnit);
					ProcessUnitModel updatedProcessUnitModel = new ProcessUnitModel();
					BeanUtils.copyProperties(existingProcessUnit, updatedProcessUnitModel);

					log.info("[{}] Process unit updated successfully: {}", new Date(), updatedProcessUnitModel);
					return updatedProcessUnitModel;
				}
		
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while updating a process unit: {}", new Date(), e.getMessage(), e);
			throw new CustomException("Failed to update process unit "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteProcessUnit(Long id, String token) {
		try {

				Optional<ProcessUnit> optionalProcessUnit = processUnitRepository.findById(id);
				if (optionalProcessUnit.isPresent()) {
					processUnitRepository.deleteById(id);
					log.info("[{}] Process unit with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] Process unit with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Process unit not found with ID: " + id,HttpStatus.NO_CONTENT);
				}

		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting a process unit with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete process unit "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
