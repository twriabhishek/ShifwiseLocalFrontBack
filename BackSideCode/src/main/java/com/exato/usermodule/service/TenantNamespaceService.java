package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.TenantNamespaceModel;

import jakarta.servlet.http.HttpServletRequest;

public interface TenantNamespaceService {

	TenantNamespaceModel createTenantNamespaceModel(TenantNamespaceModel tenantNamespaceModel, HttpServletRequest request);

	List<TenantNamespaceModel> getAllTenantNamespaceModels(HttpServletRequest request);

	TenantNamespaceModel getTenantNamespaceModelById(Long id);

	TenantNamespaceModel updateTenantNamespaceModel(Long id, TenantNamespaceModel tenantNamespaceModel,HttpServletRequest request);

	void deleteTenantNamespaceModel(Long id);
}
