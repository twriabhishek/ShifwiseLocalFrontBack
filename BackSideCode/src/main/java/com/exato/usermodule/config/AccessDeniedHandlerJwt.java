package com.exato.usermodule.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccessDeniedHandlerJwt implements AccessDeniedHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AccessDeniedHandlerJwt.class);

	@Override
	public void handle(HttpServletRequest req, HttpServletResponse response, AccessDeniedException e)
			throws IOException, ServletException {

		LOG.error("ERROR: AccessDeniedHandlerJwt-Error occured: {}", e.getMessage());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		final Map<String, Object> body = new HashMap<>();
		body.put("code", HttpServletResponse.SC_FORBIDDEN);
		body.put("payload", "You don't have required role to perform this action.");

		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);
	}

}
