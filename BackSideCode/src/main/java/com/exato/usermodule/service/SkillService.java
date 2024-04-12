package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.SkillModel;

import jakarta.servlet.http.HttpServletRequest;


public interface SkillService {

	SkillModel createSkill(SkillModel skillModel, HttpServletRequest request);

	List<SkillModel> getAllSkills(HttpServletRequest request);

	SkillModel getSkillById(Long id);

	SkillModel updateSkill(Long id, SkillModel skillModel);

	void deleteSkill(Long id);
}
