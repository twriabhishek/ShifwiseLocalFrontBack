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

import com.exato.usermodule.entity.Vendor;
import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.System;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.VendorModel;
import com.exato.usermodule.repository.SystemRepository;
import com.exato.usermodule.repository.VendorRepository;
import com.exato.usermodule.service.VendorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class VendorServiceImpl implements VendorService {

	private final VendorRepository vendorRepository;
	private final SystemRepository systemRepository;
	private final ObjectMapper objectMapper;
	private final JwtUtils jwtUtils;

	@Autowired
	public VendorServiceImpl(VendorRepository vendorRepository, SystemRepository systemRepository,
			ObjectMapper objectMapper,JwtUtils jwtUtils) {
		this.vendorRepository = vendorRepository;
		this.systemRepository = systemRepository;
		this.objectMapper = objectMapper;
		this.jwtUtils=jwtUtils;
	}

	@Override
	public VendorModel createVendor(VendorModel vendorModel, HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			Long systemId = vendorModel.getSystemId();
				  System system = systemRepository.findById(systemId).orElse(null);

				if (system != null) {
					Vendor vendor = new Vendor();
					vendor.setSystems(system);

					// Check if a vendor with the specified name already exists in the database.
					String vendorName = vendorModel.getVendorName();
					if (vendorRepository.existsByVendorNameAndClientId(vendorName, clientId)) {
						log.warn("[{}] Vendor already exists with the same name", new Date());
						throw new CustomException("Vendor already exists with the same name",HttpStatus.CONFLICT);
					} else {
						vendor.setClientId(clientId);
						vendor.setVendorName(vendorName);
						vendor.setCreatedBy("exato");
						vendor.setCreatedDate(new Date());
						// Convert the JSON string to a JSON object and store it in the entity
						String templateJson = vendorModel.getTemplate();

						// Use ObjectMapper to parse the JSON string
						JsonNode templateNode = objectMapper.readTree(templateJson);

						// Store the JSON object in the entity
						vendor.setTemplate(templateNode.toString());

						vendor = vendorRepository.save(vendor);

						VendorModel createdVendorModel = new VendorModel();
						BeanUtils.copyProperties(vendor, createdVendorModel);
						createdVendorModel.setSystemId(vendor.getSystems().getSystemId());
						log.info("Vendor created successfully: {}", createdVendorModel);
						return createdVendorModel;
					}
				} else {
					throw new CustomException("Vendor not found with ID: " + systemId,HttpStatus.NOT_FOUND);
				}
		}catch (IllegalStateException e) {
	        log.error(e.getMessage());
	        throw new CustomException(e.getMessage(), HttpStatus.CONFLICT);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error occurred while creating Vendor : {}", e.getMessage());

	        if (e.getMessage() != null && e.getMessage().contains("idx_clientName")) {
	            // Handle the case of duplicate clientName
	            throw new CustomException("Vendor  name is already taken"+ e.getMessage(),HttpStatus.CONFLICT);
	        } else {
	            // Handle other types of DataIntegrityViolationException
	            throw new CustomException("Error occurred while creating Vendor : " + e.getMessage(), HttpStatus.CONFLICT);
	        }
	    }catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
	        log.error("Error occurred while creating Vendor : {}", e.getMessage());
	        throw new CustomException("Error occurred while creating Vendor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    } 
	}

	@Override
	public List<VendorModel> getAllVendors(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
			String roles = jwtUtils.getRolesFromToken(jwtToken);
			List<VendorModel> listOfVendor = new ArrayList<>();
			List<Vendor> vendors = null;
				log.info("[{}] Retrieving all vendors", new Date());
				if (roles.toLowerCase().contains("superadmin")) {
					vendors = vendorRepository.findAll();
				} else if (clientId != null) {
					vendors = vendorRepository.findByClientId(clientId);
				}
			if (vendors.isEmpty()) {
				log.warn("[{}] No vendors found.", new Date());
				throw new CustomException("No vendors found.",HttpStatus.NOT_FOUND);
			} else {
				for (Vendor vendor : vendors) {
					VendorModel vendorModel = new VendorModel();
					BeanUtils.copyProperties(vendor, vendorModel);
					vendorModel.setSystemId(vendor.getSystems().getSystemId());
					listOfVendor.add(vendorModel);
				}
				log.info("[{}] Retrieved {} vendors", new Date(), listOfVendor.size());
				return listOfVendor;
			}
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while retrieving all vendors: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to retrieve vendors :  "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public VendorModel getVendorById(Long id) {
		try {
			
				Vendor vendor = vendorRepository.findById(id)
						.orElseThrow(() -> new CustomException("Vendor not found with ID: " + id,HttpStatus.NOT_FOUND));
				VendorModel vendorModel = new VendorModel();
				BeanUtils.copyProperties(vendor, vendorModel);
				vendorModel.setSystemId(vendor.getSystems().getSystemId());
				log.info("Retrieved Vendor with ID {}: {}", id, vendorModel);
				return vendorModel;
		}  catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}] Error occurred while retrieving Vendor by ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to retrieve Vendor by ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<VendorModel> getVendorsBySystemId(Long systemId) {
		try {
			
			List<VendorModel> listOfVendor = new ArrayList<>();
			List<Vendor> vendors;

				log.info("[{}] Retrieving all vendors by System ID {}", new Date(), systemId);
				vendors = vendorRepository.findBySystemId(systemId);
				if (vendors == null || vendors.isEmpty()) {
					log.warn("[{}] No vendors found for System ID {}", new Date(), systemId);
					throw new CustomException("No vendors found for System ID: " + systemId,HttpStatus.NOT_FOUND);
				}
				for (Vendor vendor : vendors) {
					VendorModel vendorModel = new VendorModel();
					BeanUtils.copyProperties(vendor, vendorModel);
					vendorModel.setSystemId(vendor.getSystems().getSystemId());
					listOfVendor.add(vendorModel);
				}
				log.info("[{}] Retrieved {} vendors for System ID {}", new Date(), listOfVendor.size(), systemId);
				return listOfVendor;
		} catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		}catch (Exception e) {
			log.error("[{}]Error occurred while retrieving all vendors by System ID{}: {}", new Date(), systemId,
					e.getMessage());
			throw new CustomException("Failed to retrieve vendors by System ID "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public VendorModel updateVendor(Long id, VendorModel vendorModel,HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			String jwtToken = token.substring(7); 
			Long clientId = jwtUtils.extractClientId(jwtToken);
				Vendor existingVendor = vendorRepository.findById(id).orElse(null);
				if (existingVendor == null) {
					log.warn("[{}] Vendor with ID {} not found for update", new Date(), id);
					throw new CustomException("Vendor not found with ID: " + id,HttpStatus.NOT_FOUND);
				} else if (vendorRepository.existsByVendorNameAndClientIdAndVendorIdNot(vendorModel.getVendorName(),
						clientId, id)) {
					log.warn("[{}] Vendor already exists with the same name", new Date());
					throw new CustomException("Vendor already exists with the same name",HttpStatus.CONFLICT);
				} else {
					if (existingVendor.getSystems().getSystemId() != null) {
						Long systemId = existingVendor.getSystems().getSystemId();
						System system = systemRepository.findById(systemId).orElse(null);
						if (system != null) {
							existingVendor.setSystems(system);
						} else {
							log.info("System with ID {} not found", systemId);
							throw new CustomException("System not found with ID: " + systemId,HttpStatus.NOT_FOUND);
						}
					}
					existingVendor.setUpdatedBy("exato");
					existingVendor.setUpdatedDate(new Date());
					BeanUtils.copyProperties(vendorModel, existingVendor, "vendorId", "clientId", "createdBy",
							"createdDate");
					vendorRepository.save(existingVendor);
					VendorModel updatedVendorModel = new VendorModel();
					BeanUtils.copyProperties(existingVendor, updatedVendorModel);
					updatedVendorModel.setSystemId(existingVendor.getSystems().getSystemId());
					log.info("[{}] Vendor with ID {} updated successfully", new Date(), id);
					return updatedVendorModel;
				}

		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while updating Vendor: {}", new Date(), e.getMessage());
			throw new CustomException("Failed to update Vendor : "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void deleteVendor(Long id) {
		try {
			
				Optional<Vendor> vendor = vendorRepository.findById(id);
				if (vendor.isPresent()) {
					vendorRepository.deleteById(id);
					log.info("[{}] Vendor with ID {} deleted successfully", new Date(), id);
				} else {
					log.warn("[{}] Vendor unit with ID {} not found for deletion", new Date(), id);
					throw new CustomException("Vendor not found with ID: " + id,HttpStatus.NOT_FOUND);
				}
		
		}catch (CustomException e) {
			String errorMessage = "CustomException: " + e.getMessage();
			log.error(errorMessage, e);
			throw e;
		} catch (Exception e) {
			log.error("[{}] Error occurred while deleting Vendor with ID {}: {}", new Date(), id,
					e.getMessage());
			throw new CustomException("Failed to delete Vendor: "+e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
}
