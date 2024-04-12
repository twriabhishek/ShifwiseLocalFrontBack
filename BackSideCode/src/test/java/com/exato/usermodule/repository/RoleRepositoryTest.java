package com.exato.usermodule.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.exato.usermodule.entity.Role;

@SpringBootTest
class RoleRepositoryTest {

	@Mock
    private RoleRepository roleRepository;

    @Mock
    private Role role; // Mock Role object

    @Test
    void testFindByName() {
        // Arrange
        String roleName = "ROLE_USER";
        when(roleRepository.findByName(roleName)).thenReturn(role);

        // Act
        Role foundRole = roleRepository.findByName(roleName);

        // Assert
        assertEquals(role, foundRole);
    }


}
