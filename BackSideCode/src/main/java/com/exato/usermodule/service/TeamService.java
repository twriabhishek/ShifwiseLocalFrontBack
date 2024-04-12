package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.TeamModel;

import jakarta.servlet.http.HttpServletRequest;

public interface TeamService {

	TeamModel createTeam(TeamModel teamModel, HttpServletRequest request);

	List<TeamModel> getAllTeams(HttpServletRequest request);

	TeamModel getTeamById(Long id, HttpServletRequest request);

	TeamModel updateTeam(Long id, TeamModel teamModel, HttpServletRequest request);

	void deleteTeam(Long id, HttpServletRequest request);
}
