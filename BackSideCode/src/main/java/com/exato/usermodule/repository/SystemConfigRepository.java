package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exato.usermodule.entity.SystemConfig;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

	List<SystemConfig> findByClientId(Long clientId);

	boolean existsByTenantNamespaceNamespaceIdOrServiceNameAndClientId(Long namespaceId, String systemConfigName,
			Long clientId);

	boolean existsByTenantNamespaceNamespaceIdOrServiceNameAndClientIdAndSystemConfigIdNot(Long namespaceId,
			String systemConfigName, Long clientId, Long id);

	List<SystemConfig> findByTenantNamespace_NamespaceId(Long namespaceId);

}
