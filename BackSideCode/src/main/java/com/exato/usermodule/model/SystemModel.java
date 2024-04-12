package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemModel {

	private Long clientId;
	private Long systemId;

	@NotBlank(message = "system name should not be empty")
	private String systemName;
}
