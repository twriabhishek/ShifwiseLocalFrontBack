package com.exato.usermodule.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.model.RoleModel;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.service.RoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
	
	private final RoleRepository roleRepository;
	
	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
		}

    @Override
    public RoleModel createRole(RoleModel roleModel) {
        try {
            Role role = new Role();
            role.setName(roleModel.getName());
            role = roleRepository.save(role);
            BeanUtils.copyProperties(role, roleModel);
            log.info("Role created successfully: {}", roleModel);
            return roleModel;
        } catch (Exception e) {
            log.error("Error occurred while creating role: {}", e.getMessage());
            throw new CustomException("Error occurred while creating role: " + e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<RoleModel> getAllRole() {
        try {
            List<RoleModel> roleModelList = new ArrayList<>();
            List<Role> roleList = roleRepository.findAll();

            for (Role role : roleList) {
                RoleModel roleModel = new RoleModel();
                BeanUtils.copyProperties(role, roleModel);
                roleModelList.add(roleModel);
            }
            log.info("Retrieved all roles successfully.");
            return roleModelList;
        } catch (Exception e) {
            log.error("Error occurred while retrieving all roles: {}", e.getMessage());
            throw new CustomException("Error occurred while retrieving all roles: " + e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public RoleModel getRoleById(Long id) {
        try {
            Role role = roleRepository.findById(id).orElse(null);
            if (role != null) {
                RoleModel roleModel = new RoleModel();
                BeanUtils.copyProperties(role, roleModel);
                log.info("Retrieved role by ID: {} successfully.", id);
                return roleModel;
            } else {
                log.warn("Role with ID {} not found.", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error occurred while retrieving role by ID: {}", e.getMessage());
            throw new CustomException("Error occurred while retrieving role by ID: " + e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public RoleModel updateRole(Long id, RoleModel roleModel) {
        try {
            Role role = roleRepository.findById(id).orElse(null);
            if (role != null) {
                role.setName(roleModel.getName());
                Role updatedRole = roleRepository.save(role);
                BeanUtils.copyProperties(updatedRole, roleModel);
                log.info("Updated role successfully: {}", roleModel);
                return roleModel;
            } else {
                log.warn("Role with ID {} not found.", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error occurred while updating role: {}", e.getMessage());
            throw new CustomException("Error occurred while updating role: " + e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void deleteRole(Long id) {
        try {
            roleRepository.deleteById(id);
            log.info("Deleted role with ID: {} successfully.", id);
        } catch (Exception e) {
            log.error("Error occurred while deleting role with ID {}: {}", id, e.getMessage());
            throw new CustomException("Error occurred while deleting role with ID: " + e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
}
