package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exato.usermodule.entity.ProcessUnit;

public interface ProcessUnitRepository extends JpaRepository<ProcessUnit, Long> {

	boolean existsByProcessUnitNameAndClientId(String processUnitName, Long clientId);

	List<ProcessUnit> findByClientId(Long clientId);
}
