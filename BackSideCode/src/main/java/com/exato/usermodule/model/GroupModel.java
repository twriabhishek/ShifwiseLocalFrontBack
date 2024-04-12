package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GroupModel {
	
	private Long clientId;
	private Long groupId;
 
	@NotBlank(message = "group name should not be empty")
	private String groupName;

}
