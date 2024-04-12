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
import com.exato.usermodule.model.GroupModel;
import com.exato.usermodule.service.GroupService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/groups")
@Slf4j
public class GroupController {

	private final GroupService groupService;

	@Autowired
	public GroupController(GroupService groupService) {
		this.groupService = groupService;
	}
	
	@Autowired 
	private CheckTokenValidOrNot checkTokenValidOrNot; 
	
	private static final String INVALID_TOKEN_MSG = "Token is invalid or not present in header"; 

	@PostMapping
	public ResponseEntity<GroupModel> createGroup(@Valid @RequestBody GroupModel groupModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			GroupModel createdGroup = groupService.createGroup(groupModel, request);
			log.info("[{}] Created business unit with name '{}'", new Date(), createdGroup.getGroupName());
			return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
		} catch (CustomException e) {
			log.error("[{}] Error creating a group: {}", new Date(), e.getMessage());
			throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("[{}] Error creating a group: {}", new Date(), e.getMessage());
			throw new CustomException("Error creating a group: {}"+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<List<GroupModel>> getAllGroups(HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			List<GroupModel> groupList = groupService.getAllGroups(request);
			log.info("Retrieved {} groups", groupList.size());
			return ResponseEntity.ok(groupList);
		} catch (CustomException e) {
			log.error("[{}] Error occurred while retrieving groups: {}", new Date(), e.getMessage());
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all groups: {}", new Date(), e.getMessage());
			throw new CustomException("Error occurred while retrieving all groups:"+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<GroupModel> getGroupById(@PathVariable Long id,HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			GroupModel group = groupService.getGroupById(id);
			log.info("Retrieved group with ID {}: {}", id, group);
			return ResponseEntity.ok(group);
		} catch (CustomException e) {
			log.error("[{}] Error occurred while retrieving group: {}", new Date(), e.getMessage());
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("[{}] Error retrieving a group by ID {}: {}", new Date(), id, e.getMessage());
			throw new CustomException(" Error occurred while retrieving group:"+e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<GroupModel> updateGroup(@PathVariable Long id, @Valid @RequestBody GroupModel groupModel,
			HttpServletRequest request) {
		
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			GroupModel updatedGroup = groupService.updateGroup(id, groupModel, request);
			log.info("Updated group with ID {}: {}", id, updatedGroup);
			return ResponseEntity.ok(updatedGroup);
		} catch (CustomException e) {
			log.error("[{}] Error occurred while updating Group with name '{}': {}", new Date(),
					groupModel.getGroupName(), e.getMessage());
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("[{}] Error updating a Group with ID {}: {}", new Date(), id, e.getMessage());
			throw new CustomException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<GroupModel> deleteGroup(@PathVariable Long id, HttpServletRequest request) {
		 
		if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
		{
			throw new CustomException(INVALID_TOKEN_MSG,HttpStatus.BAD_REQUEST);
		}
		
		try {
			groupService.deleteGroup(id, request);
			log.info("Deleted group with ID {}", id);
			String successMessage = "Group with ID " + id + " has been deleted successfully.";
			throw new SuccessException(successMessage);
		}catch (SuccessException e) {
			log.info("Group with ID " + id + " has been deleted successfully.", new Date(), id);
			throw e ;
		}catch (CustomException e) {
			log.error("[{}] Group with ID {} not found for deletion", new Date(), id);
			throw new CustomException(e.getMessage(),HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("[{}] Error deleting a Group with ID {}: {}", new Date(), id, e.getMessage());
			throw new CustomException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
