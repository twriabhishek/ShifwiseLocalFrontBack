package com.exato.usermodule.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class AuditLogTest {

	 @Test
	    void testAuditLogConstructor() {
	        // Arrange
	        Long logId = 1L;
	        String username = "john_doe";
	        Long clientId = 123L;
	        String action = "LOGIN";
	        LocalDateTime timestamp = LocalDateTime.now();

	        // Act
	        AuditLog auditLog = new AuditLog(logId, username, clientId, action, timestamp);

	        // Assert
	        assertNotNull(auditLog);
	        assertEquals(logId, auditLog.getId());
	        assertEquals(username, auditLog.getUsername());
	        assertEquals(clientId, auditLog.getClientId());
	        assertEquals(action, auditLog.getAction());
	        assertEquals(timestamp, auditLog.getTimestamp());
	    }

	    @Test
	    void testAuditLogNoArgsConstructor() {
	        // Act
	        AuditLog auditLog = new AuditLog();

	        // Assert
	        assertNotNull(auditLog);
	        // Assuming default values for fields (e.g., null for objects, 0 for primitives)
	        assertEquals(null, auditLog.getId());
	        assertEquals(null, auditLog.getUsername());
	        assertEquals(null, auditLog.getClientId());
	        assertEquals(null, auditLog.getAction());
	        assertEquals(null, auditLog.getTimestamp());
	    }


}
