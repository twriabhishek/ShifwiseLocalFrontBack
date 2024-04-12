package com.exato.usermodule.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueueModel {

	private Long clientId;
	private Long queueId;

	@NotBlank(message = "queue name should not be empty")
	private String queueName;

}
