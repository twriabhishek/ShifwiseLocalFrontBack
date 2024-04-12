package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.SkillWeightageModel;

import jakarta.servlet.http.HttpServletRequest;

public interface SkillWeightageService {

	SkillWeightageModel createSkillWeightage(SkillWeightageModel skillWeightageModel, HttpServletRequest request);

	List<SkillWeightageModel> getAllSkillWeightages(HttpServletRequest request);

	SkillWeightageModel getSkillWeightageById(Long id);

	SkillWeightageModel updateSkillWeightage(Long id, SkillWeightageModel skillWeightageModel);

	void deleteSkillWeightage(Long id);
}
