package com.exato.usermodule.model;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;


class ProcessUnitModelTest {
	
	private static Validator validator;
	
	 @Test
	    void testProcessUnitModelConstructorAndGetters() {
	        // Arrange
	        Long clientId = 1L;
	        Long processUnitId = 2L;
	        String processUnitName = "Test Process Unit";

	        // Act
	        ProcessUnitModel processUnitModel = new ProcessUnitModel();
	        processUnitModel.setClientId(1L);
	        processUnitModel.setProcessUnitId(2L);
	        processUnitModel.setProcessUnitName("Test Process Unit");

	        // Assert
	        assertEquals(clientId, processUnitModel.getClientId());
	        assertEquals(processUnitId, processUnitModel.getProcessUnitId());
	        assertEquals(processUnitName, processUnitModel.getProcessUnitName());
	    }
	 	
}
