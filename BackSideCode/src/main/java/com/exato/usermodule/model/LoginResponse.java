package com.exato.usermodule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

	private String token;
	private String tokenType;
    private String username ;
    private Long clientId;
    private String clientName;
    private Long userId;
    private String[] roles;
    private String businessUnit;
}
