package com.exato.usermodule.jwt.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {
	
	@Value("${TOKEN_EXPIRATION_TIME_HOURS}")
    private int tokenExpirationTimeHours;

	private String secret = "secret";
	private String issuer = "issuer";

	private String getToken(UserDetails principal) {

		User user = (User) principal;
		String subject = user.getEmail();
		Long clientId = user.getClientId();
		Long userId = user.getId();
		String clientName = user.getClientName();
		String businessUnit = user.getBusinessUnit();
        boolean active = user.isActive();
        
        if (!active) {
        	log.error("User not active, so cannot generate a token");
			return "User not active, so cannot generate a token";
		}
        
		Set<Role> assignedRoles = (Set<Role>) principal.getAuthorities();

		String allAssignedRoles = commaSeparatedRoles(assignedRoles);

		if (allAssignedRoles.isEmpty() || clientId==null  ) {
			log.error("No roles  assigned or no client is associated , so cannot generate a token");
			throw new CustomException("No roles  assigned or no client is associated , so cannot generate a token", HttpStatus.BAD_REQUEST);
		}

		return (subject == null || subject.isEmpty()) ? null
				: Jwts.builder().setSubject(subject + "##" + allAssignedRoles)
				        .claim("clientId", clientId) // Add clientId as a claim
				        .claim("clientName", clientName) // Add clientName as a claim
				        .claim("userId", userId) //Add user Id as a claim
				        .claim("businessUnit", businessUnit)//Add business unit as a claim
						.setIssuer(issuer).setIssuedAt(new Date(System.currentTimeMillis()))
						.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(tokenExpirationTimeHours)))
						.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	public String generateToken(Authentication authentication) {
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		return getToken(principal);
	}

	public String generateToken(UserDetails principal) {
		try {
			return getToken(principal);
		} catch (Exception e) {
			log.error("Error: couldn't generate token");
			e.printStackTrace();
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	public Claims getClaims(String token) {
		 try { 
  		        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		    } catch (Exception e) {
		        log.error("Error parsing JWT claims", e);
		        throw new RuntimeException("Error parsing JWT claims", e);
		    }
	}

	public String getUsernameFromToken(String token) {
		try {
			if (token != null && !token.isEmpty() && isValidToken(token)) {
				return getClaims(token).getSubject().split("##")[0];
			} else
				return null;
		} catch (ExpiredJwtException e) {
			log.error("ERROR:ExpiredJwtException Getting email from TOKEN, {} ", e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("ERROR:Exception Getting email from TOKEN, {}", e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	public Long extractClientId(String token) {
		try {
			Claims claims = getClaims(token);
			return claims.get("clientId", Long.class);
		} catch (Exception e) {
			log.error("Error extracting clientId from token: " + e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	public String extractBusinessUnit(String token) {
		try {
			Claims claims = getClaims(token);
			return claims.get("businessUnit", String.class);
		} catch (Exception e) {
			log.error("Error extracting businessUnit from token: " + e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	public String extractClientName(String token) {
		try {
			Claims claims = getClaims(token);
			return claims.get("clientName", String.class);
		} catch (Exception e) {
			log.error("Error extracting client Name from token: " + e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	public Long extractUserId(String token) {
		try {
			Claims claims = getClaims(token);
			return claims.get("userId", Long.class);
		} catch (Exception e) {
			log.error(" Error extracting User Id from token: " + e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	public boolean isValidToken(String token) {
		try {
			
			Claims claims = getClaims(token);
				// Check if the token is revoked
	        if (claims != null && claims.getExpiration() != null) {
	            // Check if the expiration date is after the current date
	            return claims.getExpiration().after(new Date(System.currentTimeMillis()));
	        } else {
	            log.error("Error validating token: Claims or expiration date is null");
	            return false;
	        }
		} catch (Exception e) {
			 log.error("Error validating token: {}", e.getMessage());
		        return false;
			// throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	public boolean validateToken(String token) {
		try {
			return isValidToken(token);
		} catch (SignatureException e) {
	        log.error("Invalid signature!");
	        throw new CustomException("Invalid signature",HttpStatus.BAD_REQUEST);
	    } catch (MalformedJwtException e) {
	        log.error("Token malformed!");
	        throw new CustomException("Token malformed",HttpStatus.BAD_REQUEST);
	    } catch (ExpiredJwtException e) {
	        log.error("Token expired!");
	        throw new CustomException("Token expired. Please log in again.",HttpStatus.BAD_REQUEST);
	    } catch (UnsupportedJwtException e) {
	        log.error("Token unsupported!");
	        throw new CustomException("Token unsupported",HttpStatus.BAD_REQUEST);
	    } catch (IllegalArgumentException e) {
	        log.error("Claims string is empty!");
	        throw new CustomException("Claims string is empty",HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        log.error("An unexpected error occurred during token validation!", e);
	        throw new CustomException("An unexpected error occurred during token validation",HttpStatus.BAD_REQUEST);
	    }
	}

	public String getRolesFromToken(String token) {
		try {
			if (token != null && !token.isEmpty() && isValidToken(token)) {
				return getClaims(token).getSubject();
			} else
				return null;
		} catch (ExpiredJwtException e) {
			log.error("ERROR:ExpiredJwtException extracting roles from TOKEN, {} ", e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("ERROR:Exception extracting roles from TOKEN, {}", e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	public String commaSeparatedRoles(Set<Role> roles) {

		// Convert the Set to a List
		List<Role> rolesList = new ArrayList<>(roles);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < roles.size(); i++) {

			sb.append(rolesList.get(i).getName()).append(", ");
		}

		// Remove the trailing comma and space
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 2);
		}
		return sb.toString();
	}

	public Date getExpirationDateFromToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
			return claims.getExpiration();
		} catch (ExpiredJwtException e) {
			log.error("ERROR:ExpiredJwtException Getting expitation time from TOKEN, {} ", e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("ERROR:Exception Getting expiration time from TOKEN, {}", e.getMessage());
			//return null;
			throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	

}
