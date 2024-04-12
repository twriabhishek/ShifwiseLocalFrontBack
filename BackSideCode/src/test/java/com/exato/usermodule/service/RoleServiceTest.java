package com.exato.usermodule.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.exato.usermodule.entity.Role;
import com.exato.usermodule.model.RoleModel;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.serviceimpl.RoleServiceImpl;

@SpringBootTest
class RoleServiceTest {

	 @Mock
	    private RoleRepository roleRepository;

	    @InjectMocks
	    private RoleServiceImpl roleService;
	    
	    // Utility method for creating mock objects
	    private RoleModel createMockRoleModel() {
	        RoleModel roleModel = new RoleModel();
	        roleModel.setId(1L);
	        roleModel.setName("ROLE_USER");
	        // Set other properties as needed
	        return roleModel;
	    }
	    
	    private RoleModel createMockRoleModel1() {
	        RoleModel roleModel = new RoleModel();
	        roleModel.setId(2L);
	        roleModel.setName("ROLE_USER1");
	        // Set other properties as needed
	        return roleModel;
	    }

	    private Role createMockRole() {
	        Role role = new Role();
	        role.setId(1L);
	        role.setName("ROLE_USER");
	        // Set other properties as needed
	        return role;
	    }
	    
	    private Role createMockRole1() {
	        Role role = new Role();
	        role.setId(2L);
	        role.setName("ROLE_USER1");
	        // Set other properties as needed
	        return role;
	    }

	    @Test
	    void testCreateRole() {
	        // Arrange
	        RoleModel roleModel = createMockRoleModel();
	        when(roleRepository.save(any())).thenReturn(createMockRole());

	        // Act
	        RoleModel createdRole = roleService.createRole(roleModel);

	        // Assert
	        assertNotNull(createdRole);
	        assertEquals("ROLE_USER", createdRole.getName());
	        // Add more assertions based on your requirements
	    }
	    
	    @Test
	    void testGetAllRole_notFound() {
	        // Arrange
	        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

	        // Act
	        List<RoleModel> allRoles = roleService.getAllRole();

	        // Assert
	        assertNotNull(allRoles);
	        assertEquals(0, allRoles.size());
	    }
	    
	    @Test
	    void testGetAllRole_found() {
	        // Arrange
	        List<Role> mockRoles = Arrays.asList(
	                createMockRole(),
	                createMockRole1()
	        );

	        when(roleRepository.findAll()).thenReturn(mockRoles);

	        // Act
	        List<RoleModel> allRoles = roleService.getAllRole();

	        // Assert
	        assertNotNull(allRoles);
	        assertEquals(2, allRoles.size());

	        // Add more assertions based on your actual mapping logic
	        RoleModel expectedRoleModel1 = createMockRoleModel();
	        RoleModel expectedRoleModel2 = createMockRoleModel1();

	        assertEquals(expectedRoleModel1.getId(), allRoles.get(0).getId());
	        assertEquals(expectedRoleModel1.getName(), allRoles.get(0).getName());

	        assertEquals(expectedRoleModel2.getId(), allRoles.get(1).getId());
	        assertEquals(expectedRoleModel2.getName(), allRoles.get(1).getName());

	        // Reset mock interactions for a clean state in case you have more tests
	        reset(roleRepository);
	    }
	    
	    @Test
	    void testGetRoleById_Found() {
	        // Arrange
	        Long roleId = 1L;
	        Role mockRole = createMockRole();

	        when(roleRepository.findById(roleId)).thenReturn(Optional.of(mockRole));

	        // Act
	        RoleModel roleById = roleService.getRoleById(roleId);

	        // Assert
	        assertNotNull(roleById);

	        // Add more assertions based on your actual mapping logic
	        RoleModel expectedRoleModel = createMockRoleModel();

	        assertEquals(expectedRoleModel.getId(), roleById.getId());
	        assertEquals(expectedRoleModel.getName(), roleById.getName());

	        // Reset mock interactions for a clean state in case you have more tests
	        reset(roleRepository);
	    }
	    
	    @Test
	    void testGetRoleById_notFound() {
	        // Arrange
	        Long roleId = 1L;
	        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

	        // Act
	        RoleModel roleById = roleService.getRoleById(roleId);

	        // Assert
	        assertNull(roleById);
	        
	     	    }
	    
	    @Test
	    void testUpdateRole() {
	        // Arrange
	        Long roleId = 1L;
	        RoleModel roleModel = createMockRoleModel();
	        when(roleRepository.findById(roleId)).thenReturn(Optional.of(createMockRole()));
	        when(roleRepository.save(any())).thenReturn(createMockRole());

	        // Act
	        RoleModel updatedRole = roleService.updateRole(roleId, roleModel);

	        // Assert
	        assertNotNull(updatedRole);
	        assertEquals("ROLE_USER", updatedRole.getName());
	        // Add more assertions based on your requirements
	    }
	    
	    @Test
	    void testDeleteRole() {
	        // Arrange
	        Long roleId = 1L;

	        // Act
	        roleService.deleteRole(roleId);

	        // Verify that roleRepository.deleteById() was called once with the correct argument
	        verify(roleRepository, times(1)).deleteById(roleId);
	    }
	    
	  
}
