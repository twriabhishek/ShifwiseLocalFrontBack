package com.exato.usermodule.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ClientInfoTest {

	 @Test
	    void testClientInfoConstructor() {
	        // Arrange
	        Long clientId = 1L;
	        String spocName = "John Doe";
	        String clientName = "ClientABC";
	        String email = "client@example.com";
	        String password = "password123";
	        String phoneNumber = "1234567890";
	        String businessNumber = "BN123";
	        String address = "123 Main St";
	        boolean isActive = true;
	        String otpNumber = "123456";
	        String businessUnit = "BusinessUnitABC";
	        String processUnit = "ProcessUnitABC";
	        String team = "TeamABC";
	        String groupId = "Group123";
	        Set<Role> assignedRoles = new HashSet<>();
	        String createdBy = "admin";
	        String updatedBy = "admin";
	        Date createdDate = new Date();
	        Date updatedDate = new Date();

	        // Act
	        ClientInfo clientInfo = new ClientInfo();
	        clientInfo.setClientId(clientId);
	        clientInfo.setSpocName(spocName);
	        clientInfo.setClientName(clientName);
	        clientInfo.setEmail(email);
	        clientInfo.setPassword(password);
	        clientInfo.setPhonenumber(phoneNumber);
	        clientInfo.setBussinessnumber(businessNumber);
	        clientInfo.setAddress(address);
	        clientInfo.setActive(isActive);
	        clientInfo.setOtpNumber(otpNumber);
	        clientInfo.setBusinessUnit(businessUnit);
	        clientInfo.setProcessUnit(processUnit);
	        clientInfo.setTeam(team);
	        clientInfo.setGroupid(groupId);
	        clientInfo.setAssignedRoles(assignedRoles);
	        clientInfo.setCreatedBy(createdBy);
	        clientInfo.setUpdatedBy(updatedBy);
	        clientInfo.setCreatedDate(createdDate);
	        clientInfo.setUpdatedDate(updatedDate);
	        

	        // Assert
	        assertNotNull(clientInfo);
	        assertEquals(clientId, clientInfo.getClientId());
	        assertEquals(spocName, clientInfo.getSpocName());
	        assertEquals(clientName, clientInfo.getClientName());
	        assertEquals(email, clientInfo.getEmail());
	        assertEquals(password, clientInfo.getPassword());
	        assertEquals(phoneNumber, clientInfo.getPhonenumber());
	        assertEquals(businessNumber, clientInfo.getBussinessnumber());
	        assertEquals(address, clientInfo.getAddress());
	        assertEquals(isActive, clientInfo.isActive());
	        assertEquals(otpNumber, clientInfo.getOtpNumber());
	        assertEquals(businessUnit, clientInfo.getBusinessUnit());
	        assertEquals(processUnit, clientInfo.getProcessUnit());
	        assertEquals(team, clientInfo.getTeam());
	        assertEquals(groupId, clientInfo.getGroupid());
	        assertEquals(assignedRoles, clientInfo.getAssignedRoles());
	        assertEquals(createdBy, clientInfo.getCreatedBy());
	        assertEquals(updatedBy, clientInfo.getUpdatedBy());
	        assertEquals(createdDate, clientInfo.getCreatedDate());
	        assertEquals(updatedDate, clientInfo.getUpdatedDate());
	        
	    }

	    @Test
	    void testClientInfoNoArgsConstructor() {
	        // Act
	        ClientInfo clientInfo = new ClientInfo();

	        // Assert
	        assertNotNull(clientInfo);
	        // Assuming default values for fields (e.g., null for objects, 0 for primitives)
	        assertEquals(null, clientInfo.getClientId());
	        // Add assertions for other fields as needed
	    }

}
