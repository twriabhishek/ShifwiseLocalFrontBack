package com.exato.usermodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exato.usermodule.entity.Shift;


@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

	boolean existsByShiftNameAndClientId(String shiftName, Long clientId);

	List<Shift> findByClientId(Long clientId);

	boolean existsByShiftNameAndClientIdAndShiftIdNot(String shiftName, Long clientId, Long id);

}
