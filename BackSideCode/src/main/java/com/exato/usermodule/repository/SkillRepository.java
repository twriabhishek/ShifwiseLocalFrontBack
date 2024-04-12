package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exato.usermodule.entity.SkillEntity;


public interface SkillRepository extends JpaRepository<SkillEntity, Long> {

	boolean existsBySkillNameAndClientId(String skillName, Long clientId);

	List<SkillEntity> findByClientId(Long clientId);
}
