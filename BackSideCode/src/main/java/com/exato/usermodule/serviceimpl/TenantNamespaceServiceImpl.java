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
import org.springframework.web.bind.annotation.PutMapping;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.TenantNamespace;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.TenantNamespaceModel;
import com.exato.usermodule.repository.BusinessUnitRepository;
import com.exato.usermodule.repository.GroupRepository;
import com.exato.usermodule.repository.ProcessUnitRepository;
import com.exato.usermodule.repository.QueueRepository;
import com.exato.usermodule.repository.SkillRepository;
import com.exato.usermodule.repository.SkillWeightageRepository;
import com.exato.usermodule.repository.SubProcessRepository;
import com.exato.usermodule.repository.TeamRepository;
import com.exato.usermodule.repository.TenantNamespaceRepository;
import com.exato.usermodule.service.TenantNamespaceService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TenantNamespaceServiceImpl implements TenantNamespaceService {

	private final TenantNamespaceRepository tenantNamespaceRepository;
	private final BusinessUnitRepository businessUnitRepository;
	private final ProcessUnitRepository processUnitRepository;
	private final SubProcessRepository subProcessRepository;
	private final TeamRepository teamRepository;
	private final GroupRepository groupRepository;
	private final QueueRepository queueRepository;
	private final SkillRepository skillRepository;
	private final SkillWeightageRepository skillWeightageRepository;
	private final JwtUtils jwtUtils;

	@Autowired
	public TenantNamespaceServiceImpl(TenantNamespaceRepository tenantNamespaceRepository,
			BusinessUnitRepository businessUnitRepository, ProcessUnitRepository processUnitRepository,
			SubProcessRepository subProcessRepository, TeamRepository teamRepository, GroupRepository groupRepository,
			QueueRepository queueRepository, SkillRepository skillRepository,
			SkillWeightageRepository skillWeightageRepository,JwtUtils jwtUtils) {
		this.tenantNamespaceRepository = tenantNamespaceRepository;
		this.businessUnitRepository = businessUnitRepository;
		this.processUnitRepository = processUnitRepository;
		this.subProcessRepository = subProcessRepository;
		this.teamRepository = teamRepository;
		this.groupRepository = groupRepository;
		this.queueRepository = queueRepository;
		this.skillRepository = skillRepository;
		this.skillWeightageRepository = skillWeightageRepository;
		this.jwtUtils=jwtUtils;
	}

	@Override
	public TenantNamespaceModel createTenantNamespaceModel(TenantNamespaceModel tenantNamespaceModel, HttpServletRequest request) {
		try {
	
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				// Create a new Tenant Namespace.
				TenantNamespace tenantNamespace = new TenantNamespace();
				tenantNamespaceModel.setClientId(clientId);
				BeanUtils.copyProperties(tenantNamespaceModel, tenantNamespace);

				// Fetch and set related entities if they are not null.
				tenantNamespace.setBusinessUnit(tenantNamespaceModel.getBusinessUnitId() != null
						? businessUnitRepository.findById(tenantNamespaceModel.getBusinessUnitId()).orElse(null)
						: null);
				tenantNamespace.setProcessUnit(tenantNamespaceModel.getProcessUnitId() != null
						? processUnitRepository.findById(tenantNamespaceModel.getProcessUnitId()).orElse(null)
						: null);
				tenantNamespace.setSubProcess(tenantNamespaceModel.getSubProcessId() != null
						? subProcessRepository.findById(tenantNamespaceModel.getSubProcessId()).orElse(null)
						: null);
				tenantNamespace.setTeam(tenantNamespaceModel.getTeamId() != null
						? teamRepository.findById(tenantNamespaceModel.getTeamId()).orElse(null)
						: null);
				tenantNamespace.setGroup(tenantNamespaceModel.getGroupId() != null
						? groupRepository.findById(tenantNamespaceModel.getGroupId()).orElse(null)
						: null);
				tenantNamespace.setQueue(tenantNamespaceModel.getQueueId() != null
						? queueRepository.findById(tenantNamespaceModel.getQueueId()).orElse(null)
						: null);
				tenantNamespace.setSkill(tenantNamespaceModel.getSkillId() != null
						? skillRepository.findById(tenantNamespaceModel.getSkillId()).orElse(null)
						: null);
				tenantNamespace.setSkillWeightage(tenantNamespaceModel.getSkillWeightageId() != null
						? skillWeightageRepository.findById(tenantNamespaceModel.getSkillWeightageId()).orElse(null)
						: null);
				// Check if a tenant namespace with the specified name already exists in the
				// database.
				String namespaceName = tenantNamespaceModel.getNamespaceName();
				if (tenantNamespaceRepository.existsByNamespaceNameAndClientId(namespaceName, clientId)) {
					log.warn("[{}] Tenant Namespace already exists with the same name", new Date());
					throw new CustomException("Tenant Namespace already exists with the same name",HttpStatus.CONFLICT);
				}
				tenantNamespace.setCreatedBy("exato");
				tenantNamespace.setCreatedDate(new Date());
				tenantNamespace = tenantNamespaceRepository.save(tenantNamespace);

				BeanUtils.copyProperties(tenantNamespace, tenantNamespaceModel);
				log.info("[{}] Tenant Namespace with name '{}' created successfully", new Date(),
						tenantNamespaceModel.getNamespaceName());

				// Return the created Tenant Namespace Model.
				return tenantNamespaceModel;
		} catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating Tenant Namespace : {}", e.getMessage());

	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("Tenant Namespace  name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating Tenant Namespace : " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating Tenant Namespace : {}", e.getMessage());
	        throw new CustomException("Error occurred while creating Tenant Namespace: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<TenantNamespaceModel> getAllTenantNamespaceModels(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<TenantNamespaceModel> listOfTenantNamespace = new ArrayList<>();
			List<TenantNamespace> tenantNamespaces = null;
						log.info("[{}] Retrieving all namespace ", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					tenantNamespaces = tenantNamespaceRepository.findAll();
				} else if (clientId != null) {
					tenantNamespaces = tenantNamespaceRepository.findByClientId(clientId);
				}
				if (tenantNamespaces.isEmpty()) {
				log.warn("[{}] No tenant namespace found.", new Date());
				throw new CustomException("No tenant namespace found.",HttpStatus.NOT_FOUND);
			} else {
				for (TenantNamespace tenantNamespace : tenantNamespaces) {
					TenantNamespaceModel tenantNamespaceModel = new TenantNamespaceModel();

					// Set properties only if they are not null
					if (tenantNamespace.getSubProcess() != null) {
						tenantNamespaceModel.setSubProcessId(tenantNamespace.getSubProcess().getSubProcessId());
					}
					if (tenantNamespace.getBusinessUnit() != null) {
						tenantNamespaceModel.setBusinessUnitId(tenantNamespace.getBusinessUnit().getBusinessUnitId());
					}
					if (tenantNamespace.getProcessUnit() != null) {
						tenantNamespaceModel.setProcessUnitId(tenantNamespace.getProcessUnit().getProcessUnitId());
					}
					if (tenantNamespace.getGroup() != null) {
						tenantNamespaceModel.setGroupId(tenantNamespace.getGroup().getGroupId());
					}
					if (tenantNamespace.getTeam() != null) {
						tenantNamespaceModel.setTeamId(tenantNamespace.getTeam().getTeamId());
					}
					if (tenantNamespace.getQueue() != null) {
						tenantNamespaceModel.setQueueId(tenantNamespace.getQueue().getQueueId());
					}
					if (tenantNamespace.getSkill() != null) {
						tenantNamespaceModel.setSkillId(tenantNamespace.getSkill().getSkillId());
					}
					if (tenantNamespace.getSkillWeightage() != null) {
						tenantNamespaceModel
								.setSkillWeightageId(tenantNamespace.getSkillWeightage().getSkillWeightageId());
					}

					BeanUtils.copyProperties(tenantNamespace, tenantNamespaceModel);
					listOfTenantNamespace.add(tenantNamespaceModel);
				}
				log.info("[{}] Retrieved {} tenant namespace", new Date(), listOfTenantNamespace.size());
				return listOfTenantNamespace;
			}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all tenant namespace: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve tenant namespace "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public TenantNamespaceModel getTenantNamespaceModelById(Long id) {
		try {
			
				TenantNamespace tenantNamespace = tenantNamespaceRepository.findById(id).orElseThrow(
						() -> new CustomException("TenantNamespaceModel not found with ID: " + id,HttpStatus.NOT_FOUND));

				TenantNamespaceModel tenantNamespaceModel = new TenantNamespaceModel();

				// Set properties only if they are not null
				if (tenantNamespace.getBusinessUnit() != null) {
					tenantNamespaceModel.setBusinessUnitId(tenantNamespace.getBusinessUnit().getBusinessUnitId());
				}
				if (tenantNamespace.getProcessUnit() != null) {
					tenantNamespaceModel.setProcessUnitId(tenantNamespace.getProcessUnit().getProcessUnitId());
				}
				if (tenantNamespace.getSubProcess() != null) {
					tenantNamespaceModel.setSubProcessId(tenantNamespace.getSubProcess().getSubProcessId());
				}
				if (tenantNamespace.getGroup() != null) {
					tenantNamespaceModel.setGroupId(tenantNamespace.getGroup().getGroupId());
				}
				if (tenantNamespace.getTeam() != null) {
					tenantNamespaceModel.setTeamId(tenantNamespace.getTeam().getTeamId());
				}
				if (tenantNamespace.getQueue() != null) {
					tenantNamespaceModel.setQueueId(tenantNamespace.getQueue().getQueueId());
				}
				if (tenantNamespace.getSkill() != null) {
					tenantNamespaceModel.setSkillId(tenantNamespace.getSkill().getSkillId());
				}
				if (tenantNamespace.getSkillWeightage() != null) {
					tenantNamespaceModel.setSkillWeightageId(tenantNamespace.getSkillWeightage().getSkillWeightageId());
				}

				BeanUtils.copyProperties(tenantNamespace, tenantNamespaceModel);
				log.info("[{}] Retrieved tenant namespace with ID {}: {}", new Date(), id, tenantNamespaceModel);
				return tenantNamespaceModel;
		
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving tenant namespace by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve tenant namespace by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public TenantNamespaceModel updateTenantNamespaceModel(Long id, TenantNamespaceModel tenantNamespaceModel,
			HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);

				// Fetch the existing TenantNamespace to update
				TenantNamespace existingTenantNamespace = tenantNamespaceRepository.findById(id)
						.orElseThrow(() -> new CustomException("TenantNamespace not found with ID: " + id,HttpStatus.NOT_FOUND));

				if (!clientId.equals(existingTenantNamespace.getClientId())) {
					throw new CustomException("Unauthorized to update this TenantNamespace",HttpStatus.UNAUTHORIZED);
				}
				// Check if the updated namespace name conflicts with another existing namespace
				String updatedNamespaceName = tenantNamespaceModel.getNamespaceName();
				if (tenantNamespaceRepository.existsByNamespaceNameAndClientIdAndNamespaceIdNot(updatedNamespaceName,
						clientId, id)) {
					log.warn("[{}] Tenant Namespace already exists with the same name", new Date());
					throw new CustomException("Tenant Namespace already exists with the same name",HttpStatus.CONFLICT);
				}
				// Fetch related entities only if their IDs are not null in the input model
				if (tenantNamespaceModel.getBusinessUnitId() != null) {
					existingTenantNamespace.setBusinessUnit(
							businessUnitRepository.findById(tenantNamespaceModel.getBusinessUnitId()).orElse(null));
				}

				if (tenantNamespaceModel.getProcessUnitId() != null) {
					existingTenantNamespace.setProcessUnit(
							processUnitRepository.findById(tenantNamespaceModel.getProcessUnitId()).orElse(null));
				}

				if (tenantNamespaceModel.getSubProcessId() != null) {
					existingTenantNamespace.setSubProcess(
							subProcessRepository.findById(tenantNamespaceModel.getSubProcessId()).orElse(null));
				}

				if (tenantNamespaceModel.getTeamId() != null) {
					existingTenantNamespace
							.setTeam(teamRepository.findById(tenantNamespaceModel.getTeamId()).orElse(null));
				}

				if (tenantNamespaceModel.getGroupId() != null) {
					existingTenantNamespace
							.setGroup(groupRepository.findById(tenantNamespaceModel.getGroupId()).orElse(null));
				}

				if (tenantNamespaceModel.getQueueId() != null) {
					existingTenantNamespace
							.setQueue(queueRepository.findById(tenantNamespaceModel.getQueueId()).orElse(null));
				}

				if (tenantNamespaceModel.getSkillId() != null) {
					existingTenantNamespace
							.setSkill(skillRepository.findById(tenantNamespaceModel.getSkillId()).orElse(null));
				}

				if (tenantNamespaceModel.getSkillWeightageId() != null) {
					existingTenantNamespace.setSkillWeightage(
							skillWeightageRepository.findById(tenantNamespaceModel.getSkillWeightageId()).orElse(null));
				}

				// Update non-null properties from the input model
				BeanUtils.copyProperties(tenantNamespaceModel, existingTenantNamespace, "namespaceId", "clientId",
						"createdBy", "createdDate");
				existingTenantNamespace.setUpdatedBy("exato");
				existingTenantNamespace.setUpdatedDate(new Date());
				existingTenantNamespace = tenantNamespaceRepository.save(existingTenantNamespace);

				TenantNamespaceModel updatedTenantNamespaceModel = new TenantNamespaceModel();
				BeanUtils.copyProperties(existingTenantNamespace, updatedTenantNamespaceModel);

				log.info("Updated Tenant Namespace with ID: {}", id);
				return updatedTenantNamespaceModel;
			
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating Tenant Namespace: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update Tenant Namespace "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteTenantNamespaceModel(Long id) {
		try {
		
				Optional<TenantNamespace> tenantNamespaceOptional = tenantNamespaceRepository.findById(id);
				if (tenantNamespaceOptional.isPresent()) {
					tenantNamespaceRepository.deleteById(id);
					log.info("[{}] Tenant Namespace with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] Tenant Namespace with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Tenant Namespace not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting Tenant Namespace with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete Tenant Namespace "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}