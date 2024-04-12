package com.exato.usermodule.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.RevokedToken;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.model.LoginModel;
import com.exato.usermodule.model.LoginResponse;
import com.exato.usermodule.model.LogoutModel;
import com.exato.usermodule.repository.RevokedTokenRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping
@Slf4j
public class AuthController {

	private final UserRepository userRepository;
	private final AuditLogService auditLogService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final CheckTokenValidOrNot checkTokenValidOrNot;
	private final RevokedTokenRepository revokedTokenRepository;

	public AuthController(UserRepository userRepository,AuditLogService auditLogService,AuthenticationManager authenticationManager,JwtUtils jwtUtils,CheckTokenValidOrNot checkTokenValidOrNot,RevokedTokenRepository revokedTokenRepository) {
		this.userRepository = userRepository;
		this.auditLogService = auditLogService;
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.checkTokenValidOrNot = checkTokenValidOrNot;
		this.revokedTokenRepository = revokedTokenRepository;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginModel loginModel) {
		Authentication authentication = null;
		try {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(loginModel.getUsername(), loginModel.getPassword()));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw new CustomException(e.getMessage(),HttpStatus.UNAUTHORIZED);
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
      try {
		String token = jwtUtils.generateToken(authentication);
	    String username = jwtUtils.getUsernameFromToken(token);
	    Long clientId = jwtUtils.extractClientId(token);
	    String clientName = jwtUtils.extractClientName(token);
	    Long userId = jwtUtils.extractUserId(token);
	    String businessUnit = jwtUtils.extractBusinessUnit(token);
	    String roles = jwtUtils.getRolesFromToken(token);
	    String[] splitRoles = roles.split("##");

        // Extract the roles from the second part of the split string
        String roleList = splitRoles[1];

        // If there are multiple roles separated by commas, you can further split them
        String[] listOfRoles = roleList.split(",");
	    
		Optional<User> user = userRepository.findByEmail(loginModel.getUsername());
		if (!user.isPresent())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(new LoginResponse("Bearer " + token, "Bearer",username,clientId,clientName,userId,listOfRoles,businessUnit), HttpStatus.OK);
      }catch(Exception e) {
    	  throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
	}

	
	
	 @PostMapping("/logoutURL")
	    public ResponseEntity<String> logout(@RequestBody LogoutModel logoutModel,HttpServletRequest request) {
		 
			if(!checkTokenValidOrNot.checkTokenValidOrNot(request))
			{
				 throw new CustomException("Token is invalid or not present in header",HttpStatus.BAD_REQUEST);
			}
	        try {
	            String response = "Logout successful";
	            log.info("Logout successful for user: {}", logoutModel.getUsername());
	            String header = request.getHeader("Authorization");
	            String token = header.substring(7);
	            if (!revokedTokenRepository.existsByToken(token)) {
	                RevokedToken revokedToken = new RevokedToken();
	                revokedToken.setToken(token);
	                revokedToken.setRevokedAt(LocalDateTime.now());
	                revokedTokenRepository.save(revokedToken);
	            }
	            auditLogService.createAuditLog(logoutModel.getUsername(), "logout", null);
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            log.error("An error occurred during logout: {}", e.getMessage(), e);
	            throw new CustomException("An error occurred during logout",HttpStatus.BAD_REQUEST);
	        }
	 }
	
}





