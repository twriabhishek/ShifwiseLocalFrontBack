package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.VendorModel;

import jakarta.servlet.http.HttpServletRequest;

public interface VendorService {

	VendorModel createVendor(VendorModel vendorModel,HttpServletRequest request);

	List<VendorModel> getAllVendors(HttpServletRequest request);

	VendorModel getVendorById(Long id);

	VendorModel updateVendor(Long id, VendorModel vendorModel,HttpServletRequest request);

	void deleteVendor(Long id);

	List<VendorModel> getVendorsBySystemId(Long systemId);
}
