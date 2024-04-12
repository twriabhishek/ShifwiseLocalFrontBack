package com.exato.usermodule.service;

public interface AuditLogService {

	public void createAuditLog(String username, String action , Long clientId);

}
