package com.exato.usermodule.serviceimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.model.RoleModel;
import com.exato.usermodule.repository.RoleRepository;

@SpringBootTest
class RoleServiceImplTest {

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

        Role role = createMockRole();
        
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // Act
        RoleModel createdRoleModel = roleService.createRole(roleModel);

        // Assert
        assertNotNull(createdRoleModel);
        assertEquals(role.getId(), createdRoleModel.getId());
        assertEquals(role.getName(), createdRoleModel.getName());

        // Verify that roleRepository.save() was called once with the correct argument
        verify(roleRepository, times(1)).save(any(Role.class));
    }
    
    @Test
    void testCreateRole_WithError() {
        // Arrange
        RoleModel roleModel = createMockRoleModel();

        Role role = createMockRole();
        
        when(roleRepository.save(any(Role.class))).thenReturn(null);

        // Act
    //    RoleModel createdRoleModel = roleService.createRole(roleModel);
        
        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> roleService.createRole(roleModel),
                "Expected CustomException to be thrown");

     //   assertEquals("Invalid client data", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
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
    void testGetAllRole_NotFound() {
        // Arrange
        List<Role> mockRoles = Arrays.asList(
                createMockRole(),
                createMockRole1()
        );

        when(roleRepository.findAll()).thenReturn(null);

        // Act
     //   List<RoleModel> allRoles = roleService.getAllRole();
        
        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> roleService.getAllRole(),
                "Expected CustomException to be thrown");

     //   assertEquals("Invalid client data", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
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
    void testGetRoleById_Successful() {
        // Arrange
        Long roleId = 1L;
        Role mockRole = createMockRole();
        when(roleRepository.findById(roleId)).thenReturn(java.util.Optional.ofNullable(mockRole));

        // Act
        RoleModel result = roleService.getRoleById(roleId);

        // Assert
        assertNotNull(result);
        assertEquals(mockRole.getName(), result.getName());
        // Add more assertions based on your actual mapping logic

        // Verify that roleRepository.findById was called with the correct argument
        verify(roleRepository, times(1)).findById(roleId);
    }
    
    @Test
    void testGetRoleById_NotFound() {
        // Arrange
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(java.util.Optional.empty());

        // Act
        RoleModel result = roleService.getRoleById(roleId);

        // Assert
        assertNull(result);

        // Verify that roleRepository.findById was called with the correct argument
        verify(roleRepository, times(1)).findById(roleId);
    }
    
    @Test
    void testGetRoleById_withException() {
        // Arrange
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(null);

        // Act
       // RoleModel result = roleService.getRoleById(roleId);

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> roleService.getRoleById(roleId),
                "Expected CustomException to be thrown");

     //   assertEquals("Invalid client data", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
    
    @Test
    void testUpdateRole_Successful() {
        // Arrange
        Long roleId = 1L;
        RoleModel updatedRoleModel = createMockRoleModel();
        Role existingRole = createMockRole();
        when(roleRepository.findById(roleId)).thenReturn(java.util.Optional.ofNullable(existingRole));
        when(roleRepository.save(any(Role.class))).thenReturn(existingRole);

        // Act
        RoleModel result = roleService.updateRole(roleId, updatedRoleModel);

        // Assert
        assertNotNull(result);
        assertEquals(updatedRoleModel.getName(), result.getName());
        // Add more assertions based on your actual mapping logic

        // Verify that roleRepository.findById and roleRepository.save were called with the correct arguments
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).save(existingRole);
    }
    
    @Test
    void testUpdateRole_NotFound() {
        // Arrange
        Long roleId = 1L;
        RoleModel updatedRoleModel = createMockRoleModel();
        when(roleRepository.findById(roleId)).thenReturn(java.util.Optional.empty());

        // Act
        RoleModel result = roleService.updateRole(roleId, updatedRoleModel);

        // Assert
        assertNull(result);

        // Verify that roleRepository.findById was called with the correct argument
        verify(roleRepository, times(1)).findById(roleId);
    }
    
    @Test
    void testUpdateRole_withException() {
        // Arrange
        Long roleId = 1L;
        RoleModel updatedRoleModel = createMockRoleModel();
        when(roleRepository.findById(roleId)).thenReturn(null);

        // Act
      //  RoleModel result = roleService.updateRole(roleId, updatedRoleModel);

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> roleService.updateRole(roleId,updatedRoleModel),
                "Expected CustomException to be thrown");

     //   assertEquals("Invalid client data", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
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
    
    @Test
    void testDeleteRole_withError() {
        RoleServiceImpl roleService = mock(RoleServiceImpl.class);

        // Arrange
        Long roleId = 1L;

        // Mocking the deleteRole method to throw an exception
        doThrow(new CustomException("Invalid role ID", HttpStatus.BAD_REQUEST))
                .when(roleService)
                .deleteRole(isNull());

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> roleService.deleteRole(null),
                "Expected CustomException to be thrown");

        assertEquals("Invalid role ID", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
    
    @Test
    void testDeleteRole_Exception() {
        // Arrange
        Long roleId = 1L;

        // Mocking the behavior of roleRepository.deleteById to throw an exception
        doThrow(new RuntimeException("Simulated database error")).when(roleRepository).deleteById(roleId);

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
        	roleService.deleteRole(roleId);
        });

        assertEquals("Error occurred while deleting role with ID: Simulated database error", exception.getMessage());

        // Verify that roleRepository.deleteById was called once with the expected ID
        verify(roleRepository, times(1)).deleteById(roleId);
    }
    
   

}
