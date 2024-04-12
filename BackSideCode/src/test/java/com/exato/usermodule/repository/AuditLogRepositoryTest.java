package com.exato.usermodule.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.exato.usermodule.entity.AuditLog;

class AuditLogRepositoryTest {
	
	@Test
	void testAuditRepoFindAll() {
		// Arrange
		AuditLogRepository auditLogRepositoryMock = mock(AuditLogRepository.class);
		List<AuditLog> expectedLogs = Arrays.asList(new AuditLog(), new AuditLog());
		when(auditLogRepositoryMock.findAll()).thenReturn(expectedLogs);

		// Act
		List<AuditLog> actualLogs = auditLogRepositoryMock.findAll();

		// Assert
		assertEquals(expectedLogs.size(), actualLogs.size());
		assertNotEquals(expectedLogs.size() + 1, actualLogs.size());
	}

}
