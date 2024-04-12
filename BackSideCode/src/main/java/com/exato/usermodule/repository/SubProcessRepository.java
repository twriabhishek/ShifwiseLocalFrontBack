package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exato.usermodule.entity.SubProcess;


public interface SubProcessRepository extends JpaRepository<SubProcess, Long> {

	boolean existsBySubProcessNameAndClientId(String subProcessName, Long clientId);

	List<SubProcess> findByClientId(Long clientId);
}
