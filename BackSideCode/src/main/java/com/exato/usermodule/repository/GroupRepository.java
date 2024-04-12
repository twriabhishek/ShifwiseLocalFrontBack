package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exato.usermodule.entity.GroupEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

	boolean existsByGroupNameAndClientId(String groupName, Long clientId);

	List<GroupEntity> findByClientId(Long clientId);
}
