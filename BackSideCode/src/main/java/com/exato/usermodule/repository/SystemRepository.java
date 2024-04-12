package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.exato.usermodule.entity.System;


@Repository
public interface SystemRepository extends JpaRepository<System, Long> {

	boolean existsBySystemNameAndClientId(String systemName, Long clientId);

	boolean existsBySystemNameAndClientIdAndSystemIdNot(String systemName, Long clientId, Long id);

	List<System> findByClientId(Long clientId);

}
