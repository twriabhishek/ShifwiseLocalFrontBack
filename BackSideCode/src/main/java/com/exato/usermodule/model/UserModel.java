package com.exato.usermodule.model;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
	
	private Long id;
	
	private Long clientId;
	private String clientName;
	
	 @NotBlank(message = "First name is required")
	    private String firstName;

	    @NotBlank(message = "Last name is required")
	    private String lastName;

	    @NotBlank(message = "Email is required")
	    @Email(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")
	    private String email;

	    @NotBlank(message = "Password is required")
	    private String password;

	    private boolean isActive;
	    
	    @NotBlank(message = "Address is required")
		private String address;
	    
	    @NotBlank(message = "State is required")
		private String state;
	    
	    @NotBlank(message = "Country is required")
		private String country;
	    
	    @NotBlank(message = "phonenumber is required")
		private String phonenumber;
	    
	    @NotBlank(message = "bussinessnumber is required")
		private String bussinessnumber;

	    @NotNull(message = "At least one role must be assigned")
	   	private Set<Long> assignedRoles;
	    
	    private Set<String> assignedRoleName;
	    
	    private String businessUnit;
	    private String processUnit;
	    private String team;
	    private String group;
	

}
