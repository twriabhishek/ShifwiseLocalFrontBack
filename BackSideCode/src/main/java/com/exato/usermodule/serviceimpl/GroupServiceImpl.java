package com.exato.usermodule.serviceimpl;

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
import com.exato.usermodule.config.SuccessException;
import com.exato.usermodule.entity.GroupEntity;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.GroupModel;
import com.exato.usermodule.repository.GroupRepository;
import com.exato.usermodule.service.GroupService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

	private final GroupRepository groupRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public GroupServiceImpl(GroupRepository groupRepository, JwtUtils jwtUtils) {
		this.groupRepository = groupRepository;
		this.jwtUtils = jwtUtils;
	}

	@Override
	public GroupModel createGroup(GroupModel groupModel, HttpServletRequest request) {
		try {
	
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);

				String groupName = groupModel.getGroupName();
				if (groupRepository.existsByGroupNameAndClientId(groupName, clientId)) {
					log.warn("[{}] Group already exists with the same name", new Date());
					throw new CustomException("Group already exists with the same name",HttpStatus.CONFLICT);
				}

				// Create a new Group entity.
				GroupEntity group = new GroupEntity();
				groupModel.setClientId(clientId);
				BeanUtils.copyProperties(groupModel, group);
				group.setCreatedBy("exato");
				group.setCreatedDate(new Date());

				// Save the Group entity to the database.
				group = groupRepository.save(group);

				// Copy the properties of the Group entity to the Group model.
				BeanUtils.copyProperties(group, groupModel);

				// Log a message indicating that the Group was created successfully.
				log.info("[{}] Group created successfully: {}", new Date(), groupModel);

				// Return the Group model.
				return groupModel;

		} catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating group : {}", e.getMessage());

	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("group name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating group : " + e.getMessage(), HttpStatus.CONFLICT);
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
	public List<GroupModel> getAllGroups(HttpServletRequest request) {
		try {
	
			List<GroupModel> listOfgroup = new ArrayList<>();
			List<GroupEntity> groups = null;
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);

				log.info("[{}] Retrieving all groups", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					groups = groupRepository.findAll();
				} else if (clientId != null) {
					groups = groupRepository.findByClientId(clientId);
				}

			if (groups.isEmpty()) {
				throw new CustomException("No groups found.",HttpStatus.NOT_FOUND);
			} else {
				for (GroupEntity group : groups) {
					GroupModel groupModel = new GroupModel();
					BeanUtils.copyProperties(group, groupModel);
					listOfgroup.add(groupModel);
				}
				log.info("[{}] Retrieved {} groups", new Date(), listOfgroup.size());
				return listOfgroup;
			}
		} catch (CustomException e) {
			log.error("[{}] No groups found: {}", new Date(), e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all groups: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve groups "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public GroupModel getGroupById(Long id) {
		try {

				GroupEntity group = groupRepository.findById(id)
						.orElseThrow(() -> new CustomException("Group not found with ID: " + id,HttpStatus.NOT_FOUND));

				GroupModel groupModel = new GroupModel();
				BeanUtils.copyProperties(group, groupModel);
				log.info("Retrieved group with ID {}: {}", id, groupModel);
				return groupModel;

		} catch (CustomException e) {
			log.info(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while retrieving a group with ID {}: {}", id, e.getMessage(), e);
			throw new CustomException("Failed to retrieve group "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public GroupModel updateGroup(Long id, GroupModel groupModel,HttpServletRequest request) {
		try {

				GroupEntity existingGroup = groupRepository.findById(id).orElse(null);
				if (existingGroup == null) {
					throw new CustomException("Group not found with ID: " + id,HttpStatus.NOT_FOUND);
				} else if (groupRepository.existsByGroupNameAndClientId(groupModel.getGroupName(),
						groupModel.getClientId())) {
					throw new CustomException("Group already exists with the same name",HttpStatus.CONFLICT);
				} else {
					BeanUtils.copyProperties(groupModel, existingGroup, "id");
					existingGroup.setUpdatedBy("exato");
					existingGroup.setUpdatedDate(new Date());
					groupRepository.save(existingGroup);
					BeanUtils.copyProperties(existingGroup, groupModel);
					log.info("Group updated successfully: {}", groupModel);
					return groupModel;
				}

		}  catch (CustomException e) {
			log.error("[{}] Error occurred while updating Group with name '{}': {}", new Date(),
					groupModel.getGroupName(), e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating Group: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update Group "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteGroup(Long id, HttpServletRequest request) {
		try {
	
				Optional<GroupEntity> optionalGroup = groupRepository.findById(id);
				if (optionalGroup.isPresent()) {
					groupRepository.deleteById(id);
					log.info("[{}] Group with ID {} deleted successfully", new Date(), id);
					 throw new SuccessException("Group with ID " + id + " has been deleted successfully.");
				} else {
					log.warn("[{}] Group with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Group not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
		}catch (SuccessException e) {
	        // Log success information or handle accordingly
	        log.info(e.getMessage());
	        throw e;
	    } catch (CustomException e) {
			log.error("[{}] Group not found with ID {}.", new Date(), id);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting group with ID {}: {}", new Date(), id, e.getMessage());
			throw new CustomException("Failed to delete group "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
