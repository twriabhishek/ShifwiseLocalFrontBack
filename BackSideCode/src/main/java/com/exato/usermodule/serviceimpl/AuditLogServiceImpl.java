package com.exato.usermodule.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.exato.usermodule.entity.AuditLog;
import com.exato.usermodule.repository.AuditLogRepository;
import com.exato.usermodule.service.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

	private final AuditLogRepository auditLogRepository;

	public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	public void createAuditLog(String username, String action , Long clientId ) {
		AuditLog auditLog = new AuditLog();
		auditLog.setUsername(username);
		auditLog.setClientId(clientId);
		auditLog.setAction(action);
		auditLog.setTimestamp(LocalDateTime.now());
		auditLogRepository.save(auditLog);
	}

	
}
