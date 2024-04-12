package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exato.usermodule.entity.Teams;

public interface TeamRepository extends JpaRepository<Teams, Long> {

	boolean existsByTeamNameAndClientId(String teamName, Long clientId);

	List<Teams> findByClientId(Long clientId);
}
