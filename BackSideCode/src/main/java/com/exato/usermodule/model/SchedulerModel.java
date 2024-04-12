package com.exato.usermodule.model;

import lombok.Data;

@Data
public class SchedulerModel {

	public Long schedulerId;
	public Long clientId;
	public Long namespaceId;
	public String scheduleTime;
	public String scheduleType;
	private String weeklyScheduleDay;
	private Integer monthlyScheduleDate;
}
