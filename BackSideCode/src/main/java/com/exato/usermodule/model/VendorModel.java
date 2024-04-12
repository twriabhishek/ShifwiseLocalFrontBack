package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VendorModel {

	private Long clientId;
	private Long vendorId;
	private Long systemId;

	@NotBlank(message = "vendor name should not be empty")
	private String vendorName;

	@NotBlank(message = "template should not be empty")
	private String template;
}
