package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemConfigModel {

	private Long clientId;
	private Long userId;
	private Long systemConfigId;
	private String remarks;
	private boolean isActive;
	private Long namespaceId;
	private String clientName;
	private String namespaceName;

	private Long fromSystemId;
	private Long fromVendorId;
	@NotBlank(message = "fromVendorTemplate should not be empty")
	private String fromVendorTemplate;
	private Long toSystemId;
	private Long toVendorId;
	@NotBlank(message = "toVendorTemplate should not be empty")
	private String toVendorTemplate;
	@NotBlank(message = "Service name should not be empty")
	private String serviceName;

}
