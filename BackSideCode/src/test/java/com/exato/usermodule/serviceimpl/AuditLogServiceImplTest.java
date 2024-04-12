package com.exato.usermodule.serviceimpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.exato.usermodule.entity.AuditLog;
import com.exato.usermodule.repository.AuditLogRepository;

class AuditLogServiceImplTest {

	 @Mock
	    private AuditLogRepository auditLogRepository;

	    @InjectMocks
	    private AuditLogServiceImpl auditLogService;

	    @Test
	    void testCreateAuditLog() {
	        // Arrange
	        MockitoAnnotations.openMocks(this); // Initialize mocks

	        // Mock data
	        String username = "testUser";
	        String action = "testAction";
	        Long clientId = 1L;
	        
	        // Act
	        auditLogService.createAuditLog(username, action, clientId);

	     // Capture the AuditLog argument
	        ArgumentCaptor<AuditLog> auditLogCaptor = ArgumentCaptor.forClass(AuditLog.class);
	        verify(auditLogRepository, times(1)).save(auditLogCaptor.capture());

	        // Retrieve the captured AuditLog instance
	        AuditLog capturedAuditLog = auditLogCaptor.getValue();

	        // Perform assertions on the captured AuditLog instance
	        // For example:
	        assertEquals(username, capturedAuditLog.getUsername());
	        assertEquals(action, capturedAuditLog.getAction());
	        assertEquals(clientId, capturedAuditLog.getClientId());
	    }

}
