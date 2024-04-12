package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.SubProcessModel;

import jakarta.servlet.http.HttpServletRequest;

public interface SubProcessService {

	SubProcessModel createSubProcess(SubProcessModel subProcessModel, HttpServletRequest request);

	List<SubProcessModel> getAllSubProcesses(HttpServletRequest request);

	SubProcessModel getSubProcessById(Long id);

	SubProcessModel updateSubProcess(Long id, SubProcessModel subProcessModel);

	void deleteSubProcess(Long id);
}
