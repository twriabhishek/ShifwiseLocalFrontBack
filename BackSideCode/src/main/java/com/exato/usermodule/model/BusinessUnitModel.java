package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessUnitModel {
	
	private Long clientId;
	private Long businessUnitId;
 
	@NotBlank(message = "business unit name should not be empty")
	private String businessUnitName;

}
