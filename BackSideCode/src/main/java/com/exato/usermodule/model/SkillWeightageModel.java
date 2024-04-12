package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillWeightageModel {

	private Long clientId;
	private Long skillWeightageId;

	@NotBlank(message = "skillWeightage name should not be empty")
	private String skillWeightageName;

}
