package com.exato.usermodule.service;

import java.util.List;

import com.exato.usermodule.model.ClientInfoModel;

import jakarta.servlet.http.HttpServletRequest;

public interface ClientInfoService {
	
	ClientInfoModel createClient(ClientInfoModel clientInfoModel,HttpServletRequest request);

	List<ClientInfoModel> getAllClient();

	ClientInfoModel getClientById(Long id);

	ClientInfoModel updateClient(Long id, ClientInfoModel clientInfoModel,HttpServletRequest request);

	void deleteClient(Long id);

}
