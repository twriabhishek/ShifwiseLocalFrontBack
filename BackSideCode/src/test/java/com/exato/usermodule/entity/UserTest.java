package com.exato.usermodule.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class UserTest {
	
	 @Test
	    void testUserConstructor() {
	        // Arrange
	        Long id = 1L;
	        Long clientId = 2L;
	        String clientName = "ClientABC";
	        String firstName = "John";
	        String lastName = "Doe";
	        String email = "john.doe@example.com";
	        String password = "password123";
	        boolean isActive = true;
	        String otpNumber = "123456";
	        String businessUnit = "BusinessUnitABC";
	        String processUnit = "ProcessUnitABC";
	        String team = "TeamABC";
	        String groupId = "Group123";
	        String phoneNumber = "1234567890";
	        String businessNumber = "BN123";
	        String address = "123 Main St";
	        String state = "CA";
	        String country = "USA";
	        Set<Role> assignedRoles = new HashSet<>();
	        String createdBy = "admin";
	        String updatedBy = "admin";
	        Date createdDate = new Date();
	        Date updatedDate = new Date();

	        // Act
	        User user = new User(id, clientId, clientName, firstName, lastName, email, password, isActive, otpNumber,
	                businessUnit, processUnit, team, groupId, phoneNumber, businessNumber, address, state, country,
	                assignedRoles, createdBy, updatedBy, createdDate, updatedDate);

	        // Assert
	        assertNotNull(user);
	        assertEquals(id, user.getId());
	        assertEquals(clientId, user.getClientId());
	        assertEquals(clientName, user.getClientName());
	        assertEquals(firstName, user.getFirstName());
	        assertEquals(lastName, user.getLastName());
	        assertEquals(email, user.getEmail());
	        assertEquals(password, user.getPassword());
	        assertEquals(isActive, user.isActive());
	        assertEquals(otpNumber, user.getOtpNumber());
	        assertEquals(businessUnit, user.getBusinessUnit());
	        assertEquals(processUnit, user.getProcessUnit());
	        assertEquals(team, user.getTeam());
	        assertEquals(groupId, user.getGroupid());
	        assertEquals(phoneNumber, user.getPhonenumber());
	        assertEquals(businessNumber, user.getBussinessnumber());
	        assertEquals(address, user.getAddress());
	        assertEquals(state, user.getState());
	        assertEquals(country, user.getCountry());
	        assertEquals(assignedRoles, user.getAssignedRoles());
	        assertEquals(createdBy, user.getCreatedBy());
	        assertEquals(updatedBy, user.getUpdatedBy());
	        assertEquals(createdDate, user.getCreatedDate());
	        assertEquals(updatedDate, user.getUpdatedDate());
	    }

	    @Test
	    void testUserNoArgsConstructor() {
	        // Act
	        User user = new User();

	        // Assert
	        assertNotNull(user);
	        // Assuming default values for fields (e.g., null for objects, 0 for primitives)
	        assertEquals(null, user.getId());
	        // Add assertions for other fields as needed
	    }

	    @Test
	    void testUserGettersAndSetters() {
	        // Arrange
	        User user = new User();
	        String updatedEmail = "updated.email@example.com";
	        user.setEmail(updatedEmail);

	        // Act
	        String retrievedEmail = user.getEmail();

	        // Assert
	        assertEquals(updatedEmail, retrievedEmail);
	    }

	 @Test
	    void testGetUsername() {
	        // Arrange
	        String email = "john.doe@example.com";
	        User user = new User();
	        user.setEmail(email);

	        // Act
	        String username = user.getUsername();

	        // Assert
	        assertEquals(email, username);
	    }

	    @Test
	    void testIsAccountNonExpired() {
	        // Arrange
	        User user = new User();

	        // Act
	        boolean isAccountNonExpired = user.isAccountNonExpired();

	        // Assert
	        assertTrue(isAccountNonExpired);
	    }

	    @Test
	    void testIsAccountNonLocked() {
	        // Arrange
	        User user = new User();

	        // Act
	        boolean isAccountNonLocked = user.isAccountNonLocked();

	        // Assert
	        assertTrue(isAccountNonLocked);
	    }

	    @Test
	    void testIsCredentialsNonExpired() {
	        // Arrange
	        User user = new User();

	        // Act
	        boolean isCredentialsNonExpired = user.isCredentialsNonExpired();

	        // Assert
	        assertTrue(isCredentialsNonExpired);
	    }

	    @Test
	    void testIsEnabled() {
	        // Arrange
	        User user = new User();
	        user.setActive(true);

	        // Act
	        boolean isEnabled = user.isEnabled();

	        // Assert
	        assertTrue(isEnabled);
	    }

	    @Test
	    void testGetAuthorities() {
	        // Arrange
	        User user = new User();
	        Set<Role> assignedRoles = new HashSet<>();
	        Role role = new Role();
	        role.setName("ROLE_USER");
	        assignedRoles.add(role);
	        user.setAssignedRoles(assignedRoles);

	        // Act
	        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

	        // Assert
	        assertNotNull(authorities);
	        assertFalse(authorities.isEmpty());
	        assertTrue(authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
	    }

}
