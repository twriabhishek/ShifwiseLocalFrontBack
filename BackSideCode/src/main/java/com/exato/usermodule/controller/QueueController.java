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
import com.exato.usermodule.model.QueueModel;
import com.exato.usermodule.service.QueueService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/queues")
@Slf4j
public class QueueController {

	@Autowired
	private QueueService queueService;
	@Autowired 
	private CheckTokenValidOrNot checkTokenValidOrNot; 
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 

	@PostMapping
	public ResponseEntity<QueueModel> createQueue(@Valid @RequestBody QueueModel queueModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			QueueModel createdQueue = queueService.createQueue(queueModel, request);
			log.info("Created a new queue: {}", new Date(), createdQueue.getQueueName());
			return new ResponseEntity<>(createdQueue, HttpStatus.CREATED);
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			log.error("An error occurred during Queue creation: {}", e.getMessage(), e);
			throw new CustomException("An error occurred during Queue creation: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<QueueModel>> getAllQueues(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			List<QueueModel> queueList = queueService.getAllQueues(request);
			log.info("Retrieved {} queues", queueList.size());
			return new ResponseEntity<>(queueList, HttpStatus.OK);
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			log.error("An error occurred while getting all Queues: {}", e.getMessage(), e);
			throw new CustomException("An error occurred while getting all Queues: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<QueueModel> getQueueById(@PathVariable Long id,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		try {
			QueueModel queue = queueService.getQueueById(id, request);
			log.info("Retrieved queue with ID {}: {}", id, queue);
			return new ResponseEntity<>(queue, HttpStatus.OK);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while reteiving Queues by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while reteiving Queues by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<QueueModel> updateQueue(@PathVariable Long id, @Valid @RequestBody QueueModel queueModel,
			HttpServletRequest request) {
		try {
			QueueModel updatedQueue = queueService.updateQueue(id, queueModel, request);
			log.info("Updated queue with ID {}: {}", id, updatedQueue);
			return new ResponseEntity<>(updatedQueue, HttpStatus.OK);
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while updating Queues by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while updating Queues by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteQueue(@PathVariable Long id, HttpServletRequest request) {
		try {
			queueService.deleteQueue(id);
			log.info("Deleted queue with ID {}", id);
			String successMessage = "Queue with ID " + id + " has been deleted successfully.";
			throw new SuccessException(successMessage);
		}catch (SuccessException e) {
	        // Log success information or handle accordingly
	        log.info(e.getMessage());
	        throw new SuccessException(e.getMessage());
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("An error occurred while deleting queue by id : {}", e.getMessage(), e);
			throw new CustomException("An error occurred while deleting queue by id : " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
