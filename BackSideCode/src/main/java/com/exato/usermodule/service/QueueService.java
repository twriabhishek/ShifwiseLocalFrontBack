package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.QueueModel;

import jakarta.servlet.http.HttpServletRequest;


public interface QueueService {

	QueueModel createQueue(QueueModel queueModel,HttpServletRequest request);

	List<QueueModel> getAllQueues(HttpServletRequest request);

	QueueModel getQueueById(Long id, HttpServletRequest request);

	QueueModel updateQueue(Long id, QueueModel queueModel, HttpServletRequest request);

	void deleteQueue(Long id);
}
