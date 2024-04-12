package com.exato.usermodule.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BusinessUnitModelTest {

	 @Test
	    void testBusinessUnitModel() {
	        // Arrange
	        BusinessUnitModel businessUnitModel = new BusinessUnitModel();
	        businessUnitModel.setClientId(1L);
	        businessUnitModel.setBusinessUnitId(1L);
	        businessUnitModel.setBusinessUnitName("Test Business Unit");

	        // Act and Assert
	        assertEquals(1L, businessUnitModel.getClientId());
	        assertEquals(1L, businessUnitModel.getBusinessUnitId());
	        assertEquals("Test Business Unit", businessUnitModel.getBusinessUnitName());
	    }
	 
	 @Test
	    void testNoArgsConstructor() {
	        // Act
	        BusinessUnitModel businessUnitModel = new BusinessUnitModel();

	        // Assert
	        assertNotNull(businessUnitModel);
	        assertNull(businessUnitModel.getClientId());
	        assertNull(businessUnitModel.getBusinessUnitId());
	        assertNull(businessUnitModel.getBusinessUnitName());
	    }
	 
	 @Test
	    void testAllArgsConstructor() {
	        // Arrange
	        Long clientId = 1L;
	        Long businessUnitId = 2L;
	        String businessUnitName = "Test Business Unit";

	        // Act
	        BusinessUnitModel businessUnitModel = new BusinessUnitModel(clientId, businessUnitId, businessUnitName);

	        // Assert
	        assertEquals(clientId, businessUnitModel.getClientId());
	        assertEquals(businessUnitId, businessUnitModel.getBusinessUnitId());
	        assertEquals(businessUnitName, businessUnitModel.getBusinessUnitName());
	    }

}
