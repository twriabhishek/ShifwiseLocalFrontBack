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
import org.springframework.transaction.annotation.Transactional;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.SystemConfig;
import com.exato.usermodule.entity.TenantNamespace;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.SystemConfigModel;
import com.exato.usermodule.repository.SystemConfigRepository;
import com.exato.usermodule.repository.TenantNamespaceRepository;
import com.exato.usermodule.service.SystemConfigService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

	private final SystemConfigRepository systemConfigRepository;
	private final TenantNamespaceRepository tenantNamespaceRepository;
	private final JwtUtils jwtUtils;
	
	@Autowired
	public SystemConfigServiceImpl(SystemConfigRepository systemConfigRepository,
			TenantNamespaceRepository tenantNamespaceRepository,JwtUtils jwtUtils) {
		this.systemConfigRepository = systemConfigRepository;
		this.tenantNamespaceRepository = tenantNamespaceRepository;
		this.jwtUtils=jwtUtils;
	}

	@Override
	public SystemConfigModel createSystemConfig(SystemConfigModel systemConfigModel,HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId;
				Long userId = jwtUtils.extractUserId(jwtToken);
				String roles = jwtUtils.getRolesFromToken(jwtToken);
				if (roles.toLowerCase().contains("superadmin")) {
					clientId = systemConfigModel.getClientId();
				} else {
					clientId = jwtUtils.extractClientId(jwtToken);
				}
				Long namespaceId = systemConfigModel.getNamespaceId();
				TenantNamespace tenantNamespace = tenantNamespaceRepository.findById(namespaceId).orElseThrow(
						() -> new CustomException("Namespace with ID " + namespaceId + " not found",HttpStatus.NOT_FOUND));

				String systemConfigName = systemConfigModel.getServiceName();
				if (systemConfigRepository.existsByTenantNamespaceNamespaceIdOrServiceNameAndClientId(namespaceId,
						systemConfigName, clientId)) {
					log.warn("[{}] System Config already exists with the same name", new Date());
					throw new CustomException("System Config Or Namespace already exists with the same name",HttpStatus.CONFLICT);
				}

				// Create a new systemConfig.
				SystemConfig systemConfig = new SystemConfig();
				systemConfig.setTenantNamespace(tenantNamespace);
				systemConfigModel.setClientId(clientId);
				systemConfigModel.setUserId(userId);
				BeanUtils.copyProperties(systemConfigModel, systemConfig);
				systemConfig.setCreatedBy("exato");
				systemConfig.setCreatedDate(new Date());
				systemConfig = systemConfigRepository.save(systemConfig);

				BeanUtils.copyProperties(systemConfig, systemConfigModel);
				systemConfigModel.setNamespaceId(systemConfig.getTenantNamespace().getNamespaceId());
				log.info("[{}] System Config unit with name '{}' created successfully", new Date(),
						systemConfigModel.getServiceName());

				// Return the systemConfig model.
				return systemConfigModel;
		}catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating System Config : {}", e.getMessage());

	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("System Config name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating System Config: " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating System Config : {}", e.getMessage());
	        throw new CustomException("Error occurred while creating System Config: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<SystemConfigModel> getAllSystemConfigs(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			String clientName = jwtUtils.extractClientName(jwtToken);
			List<SystemConfigModel> listOfSystemConfig = new ArrayList<>();
			List<SystemConfig> systemConfigs = null;

				log.info("[{}] Retrieving all systemConfigs.", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					systemConfigs = systemConfigRepository.findAll();
				} else if (clientId != null) {
					systemConfigs = systemConfigRepository.findByClientId(clientId);
				}
			
			if (systemConfigs.isEmpty()) {
				log.warn("[{}] No systemConfigs found.", new Date());
				throw new CustomException("No systemConfigs found.",HttpStatus.NOT_FOUND);
			} else {
				for (SystemConfig systemConfig : systemConfigs) {
					// Make a reactive RESTful call to the Client microservice to fetch client
					// details
					//ClientInfoModel client = cmsClient.getClient(systemConfig.getClientId(), token);

					SystemConfigModel systemConfigModel = new SystemConfigModel();
					BeanUtils.copyProperties(systemConfig, systemConfigModel);

					if (clientId != null) {
						systemConfigModel.setClientName(clientName);
					}
					TenantNamespace tenantNamespace = systemConfig.getTenantNamespace();
					if (tenantNamespace != null) {
						systemConfigModel.setNamespaceId(tenantNamespace.getNamespaceId());
						systemConfigModel.setNamespaceName(tenantNamespace.getNamespaceName());
					}

					listOfSystemConfig.add(systemConfigModel);
				}
			}
			return listOfSystemConfig;
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all SystemConfig: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve SystemConfig "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public SystemConfigModel getSystemConfigById(Long id) {
		try {
			
				SystemConfig systemConfig = systemConfigRepository.findById(id)
						.orElseThrow(() -> new CustomException("SystemConfig not found with ID: " + id,HttpStatus.NOT_FOUND));

				SystemConfigModel systemConfigModel = new SystemConfigModel();
				BeanUtils.copyProperties(systemConfig, systemConfigModel);

				TenantNamespace tenantNamespace = systemConfig.getTenantNamespace();
				if (tenantNamespace != null) {
					systemConfigModel.setNamespaceId(tenantNamespace.getNamespaceId());
				}
				log.info("Retrieved SystemConfig with ID {}: {}", id, systemConfigModel);
				return systemConfigModel;
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving SystemConfig by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve SystemConfig by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public SystemConfigModel updateSystemConfig(Long id, SystemConfigModel systemConfigModel,HttpServletRequest request) {
		try {
			
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				String systemConfigName = systemConfigModel.getServiceName();
				SystemConfig existingSystemConfig = systemConfigRepository.findById(id)
						.orElseThrow(() -> new CustomException("SystemConfig not found with ID: " + id,HttpStatus.NOT_FOUND));
				if (systemConfigRepository
						.existsByTenantNamespaceNamespaceIdOrServiceNameAndClientIdAndSystemConfigIdNot(
								systemConfigModel.getNamespaceId(), systemConfigName, clientId, id)) {
					log.warn("[{}] System Config already exists with the same name", new Date());
					throw new CustomException("System Config Or Namespace already exists with the same name",HttpStatus.CONFLICT);
				}
				BeanUtils.copyProperties(systemConfigModel, existingSystemConfig);
				existingSystemConfig.setUpdatedBy("exato");
				existingSystemConfig.setUpdatedDate(new Date());
				existingSystemConfig = systemConfigRepository.save(existingSystemConfig);

				BeanUtils.copyProperties(existingSystemConfig, systemConfigModel);
				log.info("SystemConfig updated successfully: {}", systemConfigModel);
				return systemConfigModel;
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
	public void deleteSystemConfig(Long id) {
		try {
				Optional<SystemConfig> optionalSystemConfig = systemConfigRepository.findById(id);
				if (optionalSystemConfig.isPresent()) {
					systemConfigRepository.deleteById(id);
					log.info("[{}] System Configuration with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] System Configuration with ID {} not found for deletion", new Date(), id);
					throw new CustomException("System Configuration not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting System Configuration with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete System Configuration "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
