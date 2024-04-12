package com.exato.usermodule.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

@Data
public class AuthTokenFilter extends OncePerRequestFilter {
        
	    @Autowired
    	 private JwtUtils jwtUtils;
	    
	    @Autowired
	    private UserService userDetailsService;

		
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		
		String token = parseToken(request);
		if (token != null) {
			try {
				if (jwtUtils.validateToken(token)) {
					String email = jwtUtils.getUsernameFromToken(token);

					if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
						UserDetails userDetails = userDetailsService.loadUserByUsername(email);

						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());

						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						// Final object stored in security context with user details(UN and PWD)
						SecurityContextHolder.getContext().setAuthentication(authentication);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	
		filterChain.doFilter(request, response);
	}

	private String parseToken(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer "))
			return StringUtils.hasText(headerAuth.split(" ")[1]) ? headerAuth.split(" ")[1] : null;
		return null;
	}
}
