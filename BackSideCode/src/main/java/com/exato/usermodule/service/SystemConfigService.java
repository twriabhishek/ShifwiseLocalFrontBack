package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.SystemConfigModel;

import jakarta.servlet.http.HttpServletRequest;

public interface SystemConfigService {

	SystemConfigModel createSystemConfig(SystemConfigModel systemConfigModel, HttpServletRequest request);

	List<SystemConfigModel> getAllSystemConfigs(HttpServletRequest request);

	SystemConfigModel getSystemConfigById(Long id);

	SystemConfigModel updateSystemConfig(Long id, SystemConfigModel systemConfigModel, HttpServletRequest request);

	void deleteSystemConfig(Long id);
}
