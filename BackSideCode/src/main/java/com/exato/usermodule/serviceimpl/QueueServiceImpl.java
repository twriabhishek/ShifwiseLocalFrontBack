package com.exato.usermodule.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.Queue;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.QueueModel;
import com.exato.usermodule.repository.QueueRepository;
import com.exato.usermodule.service.QueueService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QueueServiceImpl implements QueueService {

	private final QueueRepository queueRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public QueueServiceImpl(QueueRepository queueRepository,JwtUtils jwtUtils) {
		this.queueRepository = queueRepository;
		this.jwtUtils = jwtUtils;
	}

	@Override
	public QueueModel createQueue(QueueModel queueModel,HttpServletRequest request) {
		try {
	
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				String queueName = queueModel.getQueueName();
				if (queueRepository.existsByQueueNameAndClientId(queueName, clientId)) {
					log.warn("[{}] Queue already exists with the same name", new Date());
					throw new CustomException("Queue already exists with the same name",HttpStatus.CONFLICT);
				}

				// Create a new queue entity.
				Queue queueEntity = new Queue();
				queueModel.setClientId(clientId);
				BeanUtils.copyProperties(queueModel, queueEntity);
				queueEntity.setCreatedBy("exato");
				queueEntity.setCreatedDate(new Date());

				// Save the Queue entity to the database.
				queueEntity = queueRepository.save(queueEntity);

				// Copy the properties of the Queue entity to the Queue model.
				BeanUtils.copyProperties(queueEntity, queueModel);

				// Log a message indicating that the Queue was created successfully.
				log.info("[{}] Queue with name '{}' created successfully", new Date(), queueModel.getQueueName());

				// Return the Queue model.
				return queueModel;

		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating queue : {}", e.getMessage());
	        throw new CustomException("Error occurred while creating queue: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }  
	}

	@Override
	public List<QueueModel> getAllQueues(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<QueueModel> listOfQueues = new ArrayList<>();
			List<Queue> queues = null;
					log.info("[{}] Retrieving all queues", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					queues = queueRepository.findAll();
				} else if (clientId != null) {
					queues = queueRepository.findByClientId(clientId);
				}
			if (queues.isEmpty()) {
				log.warn("[{}] No queues found.", new Date());
				throw new CustomException("No queues found.",HttpStatus.NOT_FOUND);
			} else {
				for (Queue queue : queues) {
					QueueModel queueModel = new QueueModel();
					BeanUtils.copyProperties(queue, queueModel);
					listOfQueues.add(queueModel);
				}
				log.info("[{}] Retrieved {} queues", new Date(), listOfQueues.size());
				return listOfQueues;
			}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all queues: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve queues "+e.getMessage(),HttpStatus.BAD_REQUEST);
		} 
	}

	@Override
	public QueueModel getQueueById(Long id, HttpServletRequest request) {
		try {
				Queue queue = queueRepository.findById(id)
						.orElseThrow(() -> new CustomException("Queue not found with ID: " + id,HttpStatus.NOT_FOUND));
				QueueModel queueModel = new QueueModel();
				BeanUtils.copyProperties(queue, queueModel);
				log.info("[{}] Retrieved queue with ID {}: {}", new Date(), id, queueModel);
				return queueModel;
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving queue by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve queue by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@Override
	public QueueModel updateQueue(Long id, QueueModel queueModel, HttpServletRequest request) {
		try {
	
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				Queue existingQueue = queueRepository.findById(id).orElse(null);
				if (existingQueue == null) {
					throw new CustomException("Queue not found with ID: " + id,HttpStatus.NOT_FOUND);
				} else if (queueRepository.existsByQueueNameAndClientId(queueModel.getQueueName(),
						clientId)) {
					throw new CustomException("Queue already exists with the same name ",HttpStatus.CONFLICT);
				} else {
					BeanUtils.copyProperties(queueModel, existingQueue, "id");
					existingQueue.setUpdatedBy("exato");
					existingQueue.setUpdatedDate(new Date());
					queueRepository.save(existingQueue);
					BeanUtils.copyProperties(existingQueue, queueModel);
					log.info("[{}] Queue updated successfully: {}", new Date(), queueModel);
					return queueModel;
				}

		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating Queue: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to updateQueue "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteQueue(Long id) {
		try {
	
				Optional<Queue> optionalQueue = queueRepository.findById(id);
				if (optionalQueue.isPresent()) {
					queueRepository.deleteById(id);
					log.info("[{}] Queue with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] Queue with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Queue not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting queue with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete queue "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
