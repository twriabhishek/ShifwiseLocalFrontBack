package com.exato.usermodule.jwt.utils;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.repository.RevokedTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CheckTokenValidOrNot {
	
	private final JwtUtils jwtUtils;
	private final RevokedTokenRepository revokedTokenRepository;
	
	public CheckTokenValidOrNot(JwtUtils jwtUtils,RevokedTokenRepository revokedTokenRepository) {
		this.jwtUtils = jwtUtils;
		this.revokedTokenRepository = revokedTokenRepository;
	}

	public boolean checkTokenValidOrNot(HttpServletRequest request) {
		
		String authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			

			// Extract the JWT token
			String jwtToken = authorizationHeader.substring(7); // Remove "Bearer " prefix

			// Log the JWT token
			boolean validToken = jwtUtils.isValidToken(jwtToken);
            String username = jwtUtils.getUsernameFromToken(jwtToken);
			if (!validToken) {
				return false;
			} else 
			{
				  if (revokedTokenRepository.existsByToken(jwtToken)) {
					  log.error("Token Expired !! Username id : "+ username);
			            throw new CustomException("Token Expired !!",HttpStatus.FORBIDDEN);
			        }
				return true;
			}
		
			
		}
		else
		{
			log.error("There is no token in header !!");
			return false;
		}

	}
	

}
