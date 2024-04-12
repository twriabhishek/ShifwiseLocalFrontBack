package com.exato.usermodule.service;

import org.springframework.http.ResponseEntity;

import com.exato.usermodule.model.SchedulerModel;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface SchedulerService {

	SchedulerModel createScheduler(SchedulerModel schedulerModel, HttpServletRequest request);

	SchedulerModel getSchedulerById(Long id);

	SchedulerModel updateScheduler(Long id, SchedulerModel schedulerModel);

	ResponseEntity<Void> deleteScheduler(Long id);

	List<SchedulerModel> getAllSchedulers(HttpServletRequest request);
}
