package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProcessUnitModel {
	
	private Long clientId;
	private Long processUnitId;
 
	@NotBlank(message = "processUnit name should not be empty")
	private String processUnitName;

}
