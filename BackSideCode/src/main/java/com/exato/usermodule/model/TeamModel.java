package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamModel {
	
	private Long clientId;
	private Long teamId;
 
	@NotBlank(message = "team name should not be empty")
	private String teamName;

}
