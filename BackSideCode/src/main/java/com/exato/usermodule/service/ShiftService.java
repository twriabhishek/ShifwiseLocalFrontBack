package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.ShiftModel;

import jakarta.servlet.http.HttpServletRequest;

public interface ShiftService {

	ShiftModel createShift(ShiftModel shiftModel, HttpServletRequest request);

	List<ShiftModel> getAllShifts(HttpServletRequest request);

	ShiftModel getShiftById(Long id);

	ShiftModel updateShift(Long id, ShiftModel shiftModel, HttpServletRequest request);

	void deleteShift(Long id);

}
