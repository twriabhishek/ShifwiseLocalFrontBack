package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubProcessModel {

	private Long clientId;
	private Long subProcessId;

	@NotBlank(message = "subProcess name should not be empty")
	private String subProcessName;

}
