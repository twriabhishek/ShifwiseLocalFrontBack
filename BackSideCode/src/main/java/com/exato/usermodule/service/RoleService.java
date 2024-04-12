package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.RoleModel;

public interface RoleService {
	
	RoleModel createRole(RoleModel roleModel);

	List<RoleModel> getAllRole();

	RoleModel getRoleById(Long id);

	RoleModel updateRole(Long id, RoleModel roleModel);

	void deleteRole(Long id);


}
