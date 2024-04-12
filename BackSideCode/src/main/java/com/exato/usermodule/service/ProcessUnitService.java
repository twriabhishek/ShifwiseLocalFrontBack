package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.ProcessUnitModel;

import jakarta.servlet.http.HttpServletRequest;

public interface ProcessUnitService {

	ProcessUnitModel createProcessUnit(ProcessUnitModel processUnitModel, HttpServletRequest request);

	List<ProcessUnitModel> getAllProcessUnits(HttpServletRequest request);

	ProcessUnitModel getProcessUnitById(Long id);

	ProcessUnitModel updateProcessUnit(Long id, ProcessUnitModel processUnitModel, HttpServletRequest request);

	void deleteProcessUnit(Long id, String token);
}
