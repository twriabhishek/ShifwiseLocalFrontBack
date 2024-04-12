package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exato.usermodule.entity.Queue;


public interface QueueRepository extends JpaRepository<Queue, Long> {

	boolean existsByQueueNameAndClientId(String queueName, Long clientId);

	List<Queue> findByClientId(Long clientId);
}
