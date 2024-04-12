package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.BusinessUnitModel;

import jakarta.servlet.http.HttpServletRequest;

public interface BusinessUnitService {

	BusinessUnitModel createBusinessUnit(BusinessUnitModel businessUnitModel, HttpServletRequest token);

	List<BusinessUnitModel> getAllBusinessUnits(HttpServletRequest token);

	BusinessUnitModel getBusinessUnitById(Long id);

	BusinessUnitModel updateBusinessUnit(Long id, BusinessUnitModel businessUnitModel);

	void deleteBusinessUnit(Long id);

}
