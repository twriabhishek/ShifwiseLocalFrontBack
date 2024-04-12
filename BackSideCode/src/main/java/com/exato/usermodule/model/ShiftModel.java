package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShiftModel {

	private Long clientId;
	private Long shiftId;
	@NotBlank(message = "Shift name should not be empty")
	private String shiftName;
}
