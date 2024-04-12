package com.exato.usermodule.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	  @Value("${allowed.origins}")
	    private String allowedOrigins;
	
	@Bean
	public AuthenticationEntryPoint invalidUserAuthEntryPoint() {
		return new InvalidUserAuthEntryPoint();
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new AccessDeniedHandlerJwt();
	}

	@Bean
	public OncePerRequestFilter authTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
	public LogoutHandler logoutHandlerService() {
		return new LogoutHandlerService();
	}

	@Bean
	public PasswordEncoder passwordEncode() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authManager(UserDetailsService userDetailsService) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncode());

		return new ProviderManager(authProvider);
	}

	

	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf(csrf -> csrf.disable())
	    .cors().and()
	        .authorizeHttpRequests(auth -> auth
	        		.requestMatchers(new AntPathRequestMatcher("http://localhost:9090/shiftwise/login","POST")).permitAll()
	        		.requestMatchers(new AntPathRequestMatcher("/**","GET")).permitAll()
	        		.requestMatchers(new AntPathRequestMatcher("/**","PUT")).permitAll()
	        		.requestMatchers(new AntPathRequestMatcher("/**","DELETE")).permitAll()
	        		.requestMatchers(new AntPathRequestMatcher("/**","POST")).permitAll()
	        		.requestMatchers(new AntPathRequestMatcher("/logout","POST")).permitAll()
							        		
    	        	 .anyRequest().authenticated())
	        .exceptionHandling(exception -> {
	            exception.authenticationEntryPoint(invalidUserAuthEntryPoint());
	            exception.accessDeniedHandler(accessDeniedHandler());
	        })
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	    
	    // Configure CORS to allow requests from localhost:3000
	    http.cors().configurationSource(request -> {
	        CorsConfiguration corsConfig = new CorsConfiguration();
	        corsConfig.setAllowedOrigins(Arrays.asList(allowedOrigins));
	        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	        corsConfig.setAllowedHeaders(Arrays.asList("*"));
	        corsConfig.setAllowCredentials(true);
	        corsConfig.setMaxAge(3600L);
	        return corsConfig;
	    });
	    
	       	    return http.build();
	}
 
/**	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf().disable()
	        .cors().and() // Add .and() to separate CORS configuration
	        .authorizeRequests(auth -> auth
	            .requestMatchers(HttpMethod.POST, "/login").permitAll()
	            .requestMatchers(HttpMethod.GET, "/**").permitAll()
	            .requestMatchers(HttpMethod.PUT, "/**").permitAll()
	            .requestMatchers(HttpMethod.DELETE, "/**").permitAll()
	            .requestMatchers(HttpMethod.POST, "/**").permitAll()
	            .requestMatchers(HttpMethod.POST, "/logout").permitAll()
	            .anyRequest().authenticated())
	        .exceptionHandling(exception -> {
	            exception.authenticationEntryPoint(invalidUserAuthEntryPoint());
	            exception.accessDeniedHandler(accessDeniedHandler());
	        })
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);

	    // Configure CORS to allow requests from localhost:3000
	    http.cors().configurationSource(request -> {
	        CorsConfiguration corsConfig = new CorsConfiguration();
	        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
	        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	        corsConfig.setAllowedHeaders(Arrays.asList("*"));
	        corsConfig.setAllowCredentials(true);
	        corsConfig.setMaxAge(3600L);
	        return corsConfig;
	    });

	    return http.build();
	}

**/	
	
}
