package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exato.usermodule.entity.SkillWeightageEntity;


public interface SkillWeightageRepository extends JpaRepository<SkillWeightageEntity, Long> {

	boolean existsBySkillWeightageNameAndClientId(String skillWeightageName, Long clientId);

	List<SkillWeightageEntity> findByClientId(Long clientId);
}
