package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.SystemModel;

import jakarta.servlet.http.HttpServletRequest;

public interface SystemService {

	SystemModel createSystem(SystemModel systemModel,HttpServletRequest request);

	List<SystemModel> getAllSystems(HttpServletRequest request);

	SystemModel getSystemById(Long id);

	SystemModel updateSystem(Long id, SystemModel systemModel,HttpServletRequest request);

	void deleteSystem(Long id);
}