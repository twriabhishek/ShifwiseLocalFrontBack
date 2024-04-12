package com.exato.usermodule.config;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.exato.usermodule.jwt.utils.JwtUtils;
import com.exato.usermodule.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class AuthTokenFilterTest {


	 private AuthTokenFilter authTokenFilter;
	    private JwtUtils jwtUtilsMock;
	    private UserService userDetailsServiceMock;

	    @BeforeEach
	    public void setUp() {
	        jwtUtilsMock = mock(JwtUtils.class);
	        userDetailsServiceMock = mock(UserService.class);

	        authTokenFilter = new AuthTokenFilter();
	        authTokenFilter.setJwtUtils(jwtUtilsMock);
	        authTokenFilter.setUserDetailsService(userDetailsServiceMock);
	    }

	    @Test
	   void testDoFilterInternal() throws Exception {
	        // Mock HttpServletRequest, HttpServletResponse, and FilterChain
	        HttpServletRequest requestMock = mock(HttpServletRequest.class);
	        HttpServletResponse responseMock = mock(HttpServletResponse.class);
	        FilterChain filterChainMock = mock(FilterChain.class);

	        // Mock token and username
	        String token = "mockToken";
	        String username = "mockUser";

	        // Mock behavior of jwtUtils and userService
	        when(requestMock.getHeader("Authorization")).thenReturn("Bearer " + token);
	        when(jwtUtilsMock.validateToken(token)).thenReturn(true);
	        when(jwtUtilsMock.getUsernameFromToken(token)).thenReturn(username);

	        // Mock UserDetails
	        UserDetails userDetails = new User(username, "password", Collections.emptyList());
	        when(userDetailsServiceMock.loadUserByUsername(username)).thenReturn(userDetails);

	        // Mock behavior of SecurityContextHolder
	        SecurityContextHolder.clearContext();

	        // Call the method to be tested
	        authTokenFilter.doFilterInternal(requestMock, responseMock, filterChainMock);
	        

	     // After the filter is executed, print details about the SecurityContextHolder
	     System.out.println("Authentication after filter: " +
	             SecurityContextHolder.getContext().getAuthentication());


	        // Verify that the authentication was set in the SecurityContextHolder
	        verify(userDetailsServiceMock, times(1)).loadUserByUsername(username);
	        verify(filterChainMock, times(1)).doFilter(requestMock, responseMock);
	
	        // After the filter is executed, assert that authentication is in the SecurityContextHolder
	        assert SecurityContextHolder.getContext().getAuthentication() != null;

	        // Optionally, you can check if the authentication object is of the expected type
	        assertTrue("Authentication should be of type UsernamePasswordAuthenticationToken",
	                SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);

	        // Clean up the SecurityContextHolder to avoid interference with other tests
	        SecurityContextHolder.clearContext();
	    }
}
