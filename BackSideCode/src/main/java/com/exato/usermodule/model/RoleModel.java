package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleModel {
	
	private Long id;
	
	@NotBlank(message = "Name is required")
	private String name;

}
