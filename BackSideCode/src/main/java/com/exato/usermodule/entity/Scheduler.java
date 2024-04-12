// Scheduler.java

package com.exato.usermodule.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "scheduler")
public class Scheduler {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long schedulerId;

	private Long clientId;

	private Long namespaceId;

	@Temporal(TemporalType.TIME)
	@Column(name = "schedule_time")
	private Date scheduleTime;

	private String scheduleType; // Daily/weekly/monthly

	@Column(name = "weekly_schedule_day")
	private String weeklyScheduleDay; // Store weekdays like "Monday", "Tuesday", etc.

	@Column(name = "monthly_schedule_date")
	private Integer monthlyScheduleDate; // Store the day of the month
}
