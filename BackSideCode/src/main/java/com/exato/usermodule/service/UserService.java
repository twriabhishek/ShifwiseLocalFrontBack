package com.exato.usermodule.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.exato.usermodule.model.UserModel;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService{
	
	UserModel createUser(UserModel userModel , HttpServletRequest request);
	
	List<UserModel> getAllUser();

	UserModel getUserById(Long id);

	UserModel updateUser(Long id, UserModel userModel ,HttpServletRequest request);

	void deleteUser(Long id);
	
	List<UserModel> getAllClientIds();
	
	List<UserModel> getAllUserByClientId(Long clientId);
	
	UserDetails loadUserByUsername(String username);

	

	

}
