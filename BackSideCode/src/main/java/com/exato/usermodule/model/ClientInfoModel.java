package com.exato.usermodule.model;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientInfoModel {
	
    private Long clientId;
    
    @NotBlank(message = "Name is required")
	private String spocName;
    
    @NotBlank(message = "Client name is required")
	private String clientName;
    
    @NotBlank(message = "Email is required")
    @Email(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")
	private String email;
    
    @NotBlank(message = "Password is required")
	private String password;
    
    @NotBlank(message = "Address is required")
	private String address;
    
    @NotBlank(message = "phonenumber is required")
	private String phonenumber;
    
    @NotBlank(message = "bussinessnumber is required")
	private String bussinessnumber;
	
	private boolean isActive;
	
	private String businessUnit;
    private String processUnit;
    private String team;
    private String group;
	
	 @NotNull(message = "At least one role must be assigned")
	private Set<Long> assignedRoles;
	 
	 private Set<String> assignedRoleName;
	
}
