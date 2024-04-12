package com.exato.usermodule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.jwt.utils.CheckTokenValidOrNot;
import com.exato.usermodule.model.RoleModel;
import com.exato.usermodule.repository.RoleRepository;
import com.exato.usermodule.service.RoleService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

	 @InjectMocks
	    private RoleController roleController;

	    @Mock
	    private CheckTokenValidOrNot checkTokenValidOrNot;

	    @Mock
	    private RoleService roleService;

	    @Mock
	    private RoleRepository roleRepository;

	    @Test
	    void testCreateRole() {
	    	 // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        RoleModel roleModel = mock(RoleModel.class);
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
	        when(roleService.createRole(roleModel)).thenReturn(roleModel);

	        // Act
	        ResponseEntity<RoleModel> responseEntity = roleController.createRole(roleModel, request);

	        // Assert
	        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	        assertEquals(roleModel, responseEntity.getBody());
	    }
	    
	    @Test
	    void testCreateRolesWithInvalidToken() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        RoleModel roleModel = mock(RoleModel.class);
	    
	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);
	        
	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.createRole(roleModel,request),
	                "Invalid token");

	        	    }
	    
	    @Test
	    void testCreateRoleWithBadRequest() {
	        // Arrange
	        RoleModel roleModel = new RoleModel();
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act & Assert
	     assertThrows(CustomException.class,
	                () -> roleController.createRole(roleModel, request),
	                "Invalid token should result in Bad Request");

	       	    }
	    
	    @Test
	    void testCreateRoleWithCustomException() {
	        // Arrange
	        RoleModel roleModel = new RoleModel();
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a CustomException
	        when(roleService.createRole(roleModel)).thenThrow(new CustomException("Role creation failed.", HttpStatus.BAD_REQUEST));

	        // Act & Assert
	      assertThrows(CustomException.class,
	                () -> roleController.createRole(roleModel, request),
	                "CustomException from service should be propagated");

	       	    }
	    
	    @Test
	    void testCreateRoleWithInternalServerError() {
	        // Arrange
	        RoleModel roleModel = new RoleModel();
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a general Exception
	        when(roleService.createRole(roleModel)).thenThrow(new RuntimeException("Internal Server Error"));

	        // Act & Assert
	       assertThrows(CustomException.class,
	                () -> roleController.createRole(roleModel, request),
	                "Internal Server Error should be propagated");
	    }
	    
	    
	    @Test
	    void testGetAllRoles() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        List<RoleModel> roles = mock(List.class);
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
	        when(roleService.getAllRole()).thenReturn(roles);

	        // Act
	        ResponseEntity<List<RoleModel>> responseEntity = roleController.getAllRoles(request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals(roles, responseEntity.getBody());
	    }
	    
	    @Test
	    void testGetAllRolesWithInternalServerError() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a general Exception
	        when(roleService.getAllRole()).thenThrow(new RuntimeException("Internal Server Error"));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.getAllRoles(request),
	                "An error occurred while retrieving all roles.");

	       	    }
	    
	    @Test
	    void testGetAllRolesWithCustomException() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a CustomException
	        when(roleService.getAllRole()).thenThrow(new CustomException("Error occurred while retrieving all roles.", HttpStatus.INTERNAL_SERVER_ERROR));

	        // Act & Assert
	      assertThrows(CustomException.class,
	                () -> roleController.getAllRoles(request),
	                "Error occurred while retrieving all roles.");

	       	    }
	    
	    @Test
	    void testGetAllRolesWithInvalidToken() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.getAllRoles(request),
	                "Invalid token");

	        	    }
	    
	    @Test
	    void testGetAllRolesWithServerError() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a general Exception (error scenario)
	        when(roleService.getAllRole()).thenThrow(new RuntimeException("Internal Server Error"));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.getAllRoles(request),
	                "An error occurred while retrieving all roles.");

	       
	    }
	    
	    @Test
	    void testGetRoleById() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        Long roleId = 1L;
	        RoleModel roleModel = mock(RoleModel.class);
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
	        when(roleService.getRoleById(roleId)).thenReturn(roleModel);

	        // Act
	        ResponseEntity<RoleModel> responseEntity = roleController.getRoleById(roleId, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals(roleModel, responseEntity.getBody());
	    }
	    
	    @Test
	    void testGetRoleByIdWithInternalServerError() {
	        // Arrange
	        Long roleId = 1L;
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a general Exception
	        when(roleService.getRoleById(roleId)).thenThrow(new RuntimeException("Internal Server Error"));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.getRoleById(roleId, request),
	                "An error occurred while retrieving role by ID: " + roleId);
	    }
	    
	    @Test
	    void testGetRoleByIdWithCustomException() {
	        // Arrange
	        Long roleId = 123L;
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a CustomException
	        when(roleService.getRoleById(roleId)).thenThrow(new CustomException("Role not found with ID: " + roleId, HttpStatus.OK));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.getRoleById(roleId, request),
	                "Role not found with ID: " + roleId);

	       	    }
	    
	    @Test
	    void testGetRoleByIdWithInvalidToken() {
	        // Arrange
	    	 Long roleId = 123L;
		        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.getRoleById(roleId, request),
	                "Invalid token");

	        	    }
	    
	    @Test
	    void testGetRoleByIdNotFound() {
	        // Arrange
	        Long roleId = 1L;
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a null role (not found scenario)
	        when(roleService.getRoleById(roleId)).thenReturn(null);

	        // Act & Assert
	       assertThrows(CustomException.class,
	                () -> roleController.getRoleById(roleId, request),
	                "Role not found with ID: " + roleId);

	       	    }


	    @Test
	    void testUpdateRole() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        Long roleId = 1L;
	        RoleModel roleModel = mock(RoleModel.class);
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
	        when(roleService.updateRole(roleId, roleModel)).thenReturn(roleModel);

	        // Act
	        ResponseEntity<RoleModel> responseEntity = roleController.updateRole(roleId, roleModel, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals(roleModel, responseEntity.getBody());
	    }
	    
	    @Test
	    void testUpdateRoleWithInternalServerError() {
	        // Arrange
	        Long roleId = 1L;
	        RoleModel roleModel = new RoleModel();
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a general Exception
	        when(roleService.updateRole(roleId, roleModel)).thenThrow(new RuntimeException("Internal Server Error"));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.updateRole(roleId, roleModel, request),
	                "An error occurred while updating role with ID: " + roleId);

	    }
	    
	    @Test
	    void testUpdateRoleWithCustomException() {
	        // Arrange
	        Long roleId = 123L;
	        RoleModel roleModel = new RoleModel();
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a CustomException
	        when(roleService.updateRole(roleId, roleModel)).thenThrow(new CustomException("Role not found with ID: " + roleId, HttpStatus.OK));

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.updateRole(roleId, roleModel, request),
	                "Role not found with ID: " + roleId);

	     	    }
	    
	    @Test
	    void testUpdateUserWithInvalidToken() {
	        // Arrange
	    	 Long roleId = 123L;
	    	 RoleModel roleModel = new RoleModel();
		        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.updateRole(roleId, roleModel, request),
	                "Invalid token");

	        	    }
	    
	    @Test
	    void testUpdateRoleIdNotFound() {
	        // Arrange
	        Long roleId = 1L;
	   	 RoleModel roleModel = new RoleModel();
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

		        // Act & Assert
	       assertThrows(CustomException.class,
	                () -> roleController.updateRole(roleId,roleModel, request),
	                "Role not found with ID: " + roleId);

	       	    }

	    @Test
	    void testDeleteRole() {
	        // Arrange
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        Long roleId = 1L;
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);
	        when(roleRepository.findById(roleId)).thenReturn(Optional.of(mock(Role.class)));

	        // Act
	        ResponseEntity<String> responseEntity = roleController.deleteRole(roleId, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertTrue(responseEntity.getBody().contains("Role deleted successfylly !!1"));
	    }
	    
	    @Test
	    void testDeleteRoleWithInternalServerError() {
	        // Arrange
	        Long roleId = 1L;
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the service response with a general Exception
	        when(roleRepository.findById(roleId)).thenReturn(Optional.of(new Role())); // Assuming role exists
	        doThrow(new RuntimeException("Internal Server Error")).when(roleService).deleteRole(roleId);

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.deleteRole(roleId, request),
	                "An error occurred while deleting Role with ID: " + roleId);

	     
	    }
	    
	    @Test
	    void testDeleteRoleWithCustomException() {
	        // Arrange
	        Long roleId = 123L;
	        HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the valid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(true);

	        // Mocking the repository response with a null Role
	        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.deleteRole(roleId, request),
	                "Role does not exist with ID: " + roleId);

	    }
	    
	    @Test
	    void testDeleteUserWithInvalidToken() {
	        // Arrange
	    	 Long roleId = 123L;
	    	 HttpServletRequest request = mock(HttpServletRequest.class);

	        // Mocking the invalid token scenario
	        when(checkTokenValidOrNot.checkTokenValidOrNot(request)).thenReturn(false);

	        // Act & Assert
	        assertThrows(CustomException.class,
	                () -> roleController.deleteRole(roleId, request),
	                "Invalid token");

	        	    }

}
