package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exato.usermodule.entity.TenantNamespace;


@Repository
public interface TenantNamespaceRepository extends JpaRepository<TenantNamespace, Long> {

	boolean existsByNamespaceNameAndClientId(String namespaceName, Long clientId);

	boolean existsByNamespaceNameAndClientIdAndNamespaceIdNot(String namespaceName, Long clientId, Long id);

	List<TenantNamespace> findByClientId(Long clientId);

}
