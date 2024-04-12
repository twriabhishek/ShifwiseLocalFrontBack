package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exato.usermodule.entity.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

	@Query("SELECT v FROM Vendor v WHERE v.systems.systemId = :systemId")
	List<Vendor> findBySystemId(@Param("systemId") Long systemId);

	boolean existsByVendorNameAndClientId(String vendorName, Long clientId);

	boolean existsByVendorNameAndClientIdAndVendorIdNot(String vendorName, Long clientId, Long id);

	List<Vendor> findByClientId(Long clientId);

}