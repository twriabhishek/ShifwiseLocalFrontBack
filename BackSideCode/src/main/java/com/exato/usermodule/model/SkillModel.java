package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillModel {

	private Long clientId;
	private Long skillId;

	@NotBlank(message = "skill name should not be empty")
	private String skillName;

}
