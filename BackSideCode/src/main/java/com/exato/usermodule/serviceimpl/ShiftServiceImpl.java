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
import com.exato.usermodule.entity.Shift;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.ShiftModel;
import com.exato.usermodule.repository.ShiftRepository;
import com.exato.usermodule.service.ShiftService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShiftServiceImpl implements ShiftService {

	@Autowired
	private ShiftRepository shiftRepository;

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	public ShiftModel createShift(ShiftModel shiftModel, HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				// Check if a shift with the same name already exists for the given client
				if (shiftRepository.existsByShiftNameAndClientId(shiftModel.getShiftName(), clientId)) {
					log.warn("[{}] Shift already exists with the same name", new Date(), shiftModel.getShiftName());
					throw new CustomException("Shift with the same name already exists",HttpStatus.CONFLICT);
				}

				Shift shift = new Shift();
				BeanUtils.copyProperties(shiftModel, shift);
				shift.setClientId(clientId);
				shift.setCreatedBy("exato");
				shift.setCreatedDate(new Date());
				shift = shiftRepository.save(shift);
				BeanUtils.copyProperties(shift, shiftModel);
				log.info("Shift created successfully: {}", shiftModel);

				return shiftModel;
		}catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating Shift : {}", e.getMessage());

	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("Shift name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating Shift : " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating shift : {}", e.getMessage());
	        throw new CustomException("Error occurred while creating shift: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<ShiftModel> getAllShifts(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<ShiftModel> listOfShift = new ArrayList<>();
			List<Shift> shifts = null;

				if (roles.toLowerCase().contains("superadmin")) {
					shifts = shiftRepository.findAll();
				} else if (clientId != null) {
					shifts = shiftRepository.findByClientId(clientId);
				}

			if (shifts.isEmpty()) {
				log.warn("[{}] No shifts found.", new Date());
				throw new CustomException("No shifts found.",HttpStatus.NOT_FOUND);
			} else {
				for (Shift shift : shifts) {
					ShiftModel shiftModel = new ShiftModel();
					BeanUtils.copyProperties(shift, shiftModel);
					listOfShift.add(shiftModel);
				}
				log.info("Retrieved {} shifts", listOfShift.size());
				return listOfShift;
			}

		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all shifts: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve shifts "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ShiftModel getShiftById(Long id) {
		try {
			Shift shift = shiftRepository.findById(id)
						.orElseThrow(() -> new CustomException("Shift with ID " + id + " not found",HttpStatus.NOT_FOUND));

				ShiftModel shiftModel = new ShiftModel();
				BeanUtils.copyProperties(shift, shiftModel);
				log.info("[{}] Retrieved shift with ID {}: {}", new Date(), id, shiftModel);
				return shiftModel;

		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving shift by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve shift by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ShiftModel updateShift(Long id, ShiftModel shiftModel,HttpServletRequest request) {
		try {
			
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				Shift existingShift = shiftRepository.findById(id)
						.orElseThrow(() -> new CustomException("Shift with ID " + id + " not found",HttpStatus.NOT_FOUND));

				// Check if a shift with the same name already exists (excluding the current
				// shift)
				if (shiftRepository.existsByShiftNameAndClientIdAndShiftIdNot(shiftModel.getShiftName(), clientId,
						id)) {
					log.warn("[{}] Shift already exists with the same name", new Date(), shiftModel.getShiftName());
					throw new CustomException("Shift with the same name already exists",HttpStatus.CONFLICT);
				}
				BeanUtils.copyProperties(shiftModel, existingShift, "shiftId", "clientId", "createdBy", "createdDate");
				existingShift.setUpdatedBy("exato");
				existingShift.setUpdatedDate(new Date());
				existingShift = shiftRepository.save(existingShift);

				BeanUtils.copyProperties(existingShift, shiftModel);
				log.info("Shift updated successfully: {}", shiftModel);
				return shiftModel;
			
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating shifts: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update shifts "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteShift(Long id) {
		try {
			
				Optional<Shift> optionalShift = shiftRepository.findById(id);
				if (optionalShift.isPresent()) {
					shiftRepository.deleteById(id);
					log.info("[{}] Shift with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] Shift with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Shift not found with ID: " + id,HttpStatus.NOT_FOUND);
				}

		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while shift with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete shift : "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
