package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.exato.usermodule.entity.BusinessUnit;

@Repository
public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long> {

	boolean existsByBusinessUnitNameAndClientId(String businessUnitName, Long clientId);

	@Query("SELECT bu FROM BusinessUnit bu WHERE bu.clientId = :clientId")
	List<BusinessUnit> findByClientId(Long clientId);

}
