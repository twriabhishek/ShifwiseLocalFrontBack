package com.exato.usermodule.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TeamModelTest {

	 @Test
	    void testTeamModelConstructorAndGetters() {
	        // Arrange
	        Long clientId = 1L;
	        Long teamId = 2L;
	        String teamName = "Test Team";

	        // Act
	        TeamModel teamModel = new TeamModel();
	        teamModel.setClientId(clientId);
	        teamModel.setTeamId(teamId);
	        teamModel.setTeamName(teamName);

	        // Assert
	        assertEquals(clientId, teamModel.getClientId());
	        assertEquals(teamId, teamModel.getTeamId());
	        assertEquals(teamName, teamModel.getTeamName());
	    }

}
