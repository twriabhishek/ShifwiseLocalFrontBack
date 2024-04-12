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
import com.exato.usermodule.entity.BusinessUnit;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.BusinessUnitModel;
import com.exato.usermodule.repository.BusinessUnitRepository;
import com.exato.usermodule.service.BusinessUnitService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BusinessUnitServiceImpl implements BusinessUnitService {

	private final BusinessUnitRepository businessUnitRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public BusinessUnitServiceImpl(BusinessUnitRepository businessUnitRepository,JwtUtils jwtUtils) {
		this.businessUnitRepository = businessUnitRepository;
		this.jwtUtils = jwtUtils;
	}

	@Override
	public BusinessUnitModel createBusinessUnit(BusinessUnitModel businessUnitModel, HttpServletRequest request) {
		try {
	
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				String businessUnitName = businessUnitModel.getBusinessUnitName();
				if (businessUnitRepository.existsByBusinessUnitNameAndClientId(businessUnitName, clientId)) {
					log.warn("[{}] Business unit already exists with the same name", new Date());
					throw new CustomException("Business unit already exists with the same name", HttpStatus.CONFLICT);
				}

				// Create a new business unit entity.
				BusinessUnit businessUnit = new BusinessUnit();
				businessUnitModel.setClientId(clientId);
				BeanUtils.copyProperties(businessUnitModel, businessUnit);
				businessUnit.setCreatedBy("exato");
				businessUnit.setCreatedDate(new Date());

				// Save the business unit entity to the database.
				businessUnit = businessUnitRepository.save(businessUnit);

				// Copy the properties of the business unit entity to the business unit model.
				BeanUtils.copyProperties(businessUnit, businessUnitModel);

				// Log a message indicating that the business unit was created successfully.
				log.info("[{}] Business unit with name '{}' created successfully", new Date(),
						businessUnitModel.getBusinessUnitName());
		//	}
			return businessUnitModel;
		}catch (IllegalStateException e) {
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
	        throw new CustomException("Error occurred while creating business unit: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<BusinessUnitModel> getAllBusinessUnits(HttpServletRequest request) {
		try {

			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<BusinessUnitModel> listOfBusinessUnit = new ArrayList<>();
			List<BusinessUnit> businessUnits = null;
				log.info("[{}] Retrieving all business units", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					businessUnits = businessUnitRepository.findAll();
				} else if (clientId != null) {
					businessUnits = businessUnitRepository.findByClientId(clientId);
				}

			if (businessUnits.isEmpty()) {
				log.warn("[{}] No business units found.", new Date());
				throw new CustomException("No business units found.",HttpStatus.NO_CONTENT);
			} else {
				for (BusinessUnit businessunit : businessUnits) {
					BusinessUnitModel businessUnitModel = new BusinessUnitModel();
					BeanUtils.copyProperties(businessunit, businessUnitModel);
					listOfBusinessUnit.add(businessUnitModel);
				}
				log.info("[{}] Retrieved {} business units", new Date(), listOfBusinessUnit.size());
				return listOfBusinessUnit;
			}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all business units: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve business units "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public BusinessUnitModel getBusinessUnitById(Long id) {
		try {
	
				BusinessUnit businessUnit = businessUnitRepository.findById(id)
						.orElseThrow(() -> new CustomException("Business unit with ID " + id + " not found",HttpStatus.NO_CONTENT));
				BusinessUnitModel businessUnitModel = new BusinessUnitModel();
				BeanUtils.copyProperties(businessUnit, businessUnitModel);
				log.info("[{}] Retrieved business unit with ID {}: {}", new Date(), id, businessUnitModel);
				return businessUnitModel;
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving business unit by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve business unit by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public BusinessUnitModel updateBusinessUnit(Long id, BusinessUnitModel businessUnitModel) {
		try {
			
				BusinessUnit existingBusinessUnit = businessUnitRepository.findById(id).orElse(null);
				if (existingBusinessUnit == null) {
					log.warn("[{}] Business unit with ID {} not found for update", new Date(), id);
					throw new CustomException("Business unit not found with ID: " + id,HttpStatus.NO_CONTENT);
				} else if (businessUnitRepository.existsByBusinessUnitNameAndClientId(
						businessUnitModel.getBusinessUnitName(), businessUnitModel.getClientId())) {
					log.warn("[{}] Business unit already exists with the same name", new Date());
					throw new CustomException("Business unit already exists with the same name",HttpStatus.CONFLICT);
				} else {
					BeanUtils.copyProperties(businessUnitModel, existingBusinessUnit);
					existingBusinessUnit.setUpdatedBy("exato");
					existingBusinessUnit.setUpdatedDate(new Date());
					businessUnitRepository.save(existingBusinessUnit);
					BeanUtils.copyProperties(existingBusinessUnit, businessUnitModel);
					log.info("[{}] Business unit with ID {} updated successfully", new Date(), id);
					return businessUnitModel;
				}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating business unit: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update business unit "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteBusinessUnit(Long id) {
		try {
				Optional<BusinessUnit> optionalBusinessUnit = businessUnitRepository.findById(id);
				if (optionalBusinessUnit.isPresent()) {
					businessUnitRepository.deleteById(id);
					log.info("[{}] Business unit with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] Business unit with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Business unit not found with ID: " + id,HttpStatus.NO_CONTENT);
				}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting business unit with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete business unit "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
