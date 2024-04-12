package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TenantNamespaceModel {

	private Long clientId;
	private Long businessUnitId;
	private Long processUnitId;
	private Long subProcessId;
	private Long teamId;
	private Long groupId;
	private Long queueId;
	private Long skillId;
	private Long skillWeightageId;

	private boolean isActive;
	private Long namespaceId;
	@NotBlank(message = "namespace name should not be empty")
	private String namespaceName;

}
