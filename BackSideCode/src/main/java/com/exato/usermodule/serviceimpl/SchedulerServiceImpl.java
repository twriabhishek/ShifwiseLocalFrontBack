package com.exato.usermodule.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.Scheduler;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.SchedulerModel;
import com.exato.usermodule.repository.SchedulerRepository;
import com.exato.usermodule.service.SchedulerService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

	@Autowired
	private SchedulerRepository schedulerRepository;
	
	@Autowired
	private JwtUtils jwtUtils;

	@Override
	public SchedulerModel createScheduler(SchedulerModel schedulerModel, HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
				Scheduler scheduler = new Scheduler();
				BeanUtils.copyProperties(schedulerModel, scheduler);
				if (roles.toLowerCase().contains("superadmin")) {
					scheduler.setClientId(schedulerModel.getClientId());
				} else {
					scheduler.setClientId(clientId);
				}
				scheduler.setScheduleTime(new SimpleDateFormat("HH:mm:ss").parse(schedulerModel.getScheduleTime()));
				Scheduler savedScheduler = schedulerRepository.save(scheduler);
				BeanUtils.copyProperties(savedScheduler, schedulerModel);
				log.info("[{}] Created scheduler with ID {}", new Date(), schedulerModel.getSchedulerId());
				return schedulerModel;
		} catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating scheduler : {}", e.getMessage());

	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("scheduler name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating scheduler : " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating scheduler : {}", e.getMessage());
	        throw new CustomException("Error occurred while creating scheduler: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<SchedulerModel> getAllSchedulers(HttpServletRequest request) {
		try {

			List<SchedulerModel> schedulerModels = new ArrayList<>();
			List<Scheduler> schedulerList = null;
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);

				log.info("[{}] Retrieving all schedulers", new Date());

				if (roles.toLowerCase().contains("superadmin")) {
					schedulerList = schedulerRepository.findAll();
				} else if (clientId != null) {
					schedulerList = schedulerRepository.findByClientId(clientId);
				}

				if (schedulerList.isEmpty()) {
					log.warn("[{}] No schedulers found.", new Date());
					throw new CustomException("No schedulers found.",HttpStatus.NOT_FOUND);
				} else {
					for (Scheduler scheduler : schedulerList) {
						SchedulerModel schedulerModel = new SchedulerModel();
						BeanUtils.copyProperties(scheduler, schedulerModel);
						if (scheduler.getScheduleTime() != null) {
							schedulerModel.setScheduleTime(
									new SimpleDateFormat("HH:mm:ss").format(scheduler.getScheduleTime()));
						}
						schedulerModels.add(schedulerModel);
					}
					log.info("[{}] Retrieved {} schedulers", new Date(), schedulerModels.size());
					return schedulerModels;
				}

		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all schedulers: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve schedulers:  "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public SchedulerModel getSchedulerById(Long id) {
		try {

				Optional<Scheduler> optionalScheduler = schedulerRepository.findById(id);

				if (optionalScheduler.isPresent()) {
					SchedulerModel schedulerModel = new SchedulerModel();
					BeanUtils.copyProperties(optionalScheduler.get(), schedulerModel);
					log.info("[{}] Retrieved scheduler with ID {}", new Date(), id);
					return schedulerModel;
				} else {
					throw new CustomException("Scheduler with ID " + id + " not found",HttpStatus.NOT_FOUND);
				}
			}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving scheduler by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve scheduler by ID :  "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public SchedulerModel updateScheduler(Long id, SchedulerModel schedulerModel) {
		try {
		
				Optional<Scheduler> optionalScheduler = schedulerRepository.findById(id);
				if (optionalScheduler.isPresent()) {
					Scheduler existingScheduler = optionalScheduler.get();
					BeanUtils.copyProperties(schedulerModel, existingScheduler);
//					existingScheduler.setUpdatedBy("exato");
//					existingScheduler.setUpdatedDate(new Date());
					Scheduler savedScheduler = schedulerRepository.save(existingScheduler);
					SchedulerModel savedSchedulerModel = new SchedulerModel();
					BeanUtils.copyProperties(savedScheduler, savedSchedulerModel);
					log.info("[{}] Updated scheduler with ID {}", new Date(), id);
					return savedSchedulerModel;
				} else {
					throw new CustomException("Scheduler not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating scheduler: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update scheduler "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<Void> deleteScheduler(Long id) {
		try {
				Optional<Scheduler> optionalScheduler = schedulerRepository.findById(id);
				if (optionalScheduler.isPresent()) {
					schedulerRepository.deleteById(id);
					log.info("[{}] Deleted scheduler with ID {}", new Date(), id);
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				} else {
					throw new CustomException("Scheduler not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting scheduler with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete scheduler "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
