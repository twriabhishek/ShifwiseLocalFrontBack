package com.exato.usermodule.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class RoleTest {


    @Test
    void testRoleEntity() {
        // Arrange
        Long roleId = 1L;
        String roleName = "ROLE_USER";

        // Act
        Role role = new Role(roleId, roleName);

        // Assert
        assertNotNull(role);
        assertEquals(roleId, role.getId());
        assertEquals(roleName, role.getName());
    }
    
    @Test
    void testGetAuthority() {
        // Arrange
        String roleName = "ROLE_ADMIN";
        Role role = new Role(null, roleName);

        // Act
        String authority = role.getAuthority();

        // Assert
        assertEquals(roleName, authority);
    }
    
    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long roleId = 1L;
        String name = "roleTest";
     
        // Act
        Role role = new Role(1L, "roleTest");
      
        // Assert
        assertNotNull(role);
        assertEquals(roleId, role.getId());
        assertEquals(name, role.getName());
       // assertEquals(email, user.getEmail());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Role role = new Role();

        // Assert
        assertNotNull(role);
        // Assuming default values for fields (e.g., null for objects, 0 for primitives)
        assertEquals(null, role.getId());
        assertEquals(null, role.getName());
           }


}
