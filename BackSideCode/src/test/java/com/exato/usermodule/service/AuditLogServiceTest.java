package com.exato.usermodule.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuditLogServiceTest {

	@Test
    void testCreateAuditLog() {
        // Arrange
        AuditLogService auditLogServiceMock = mock(AuditLogService.class);

        // Mock data
        String username = "testUser";
        String action = "testAction";
        Long clientId = 1L;

        // Act
        auditLogServiceMock.createAuditLog(username, action, clientId);

        // Assert
        // Verify that the createAuditLog method was called with the expected parameters
        verify(auditLogServiceMock, times(1)).createAuditLog(username, action, clientId);
	}

}
