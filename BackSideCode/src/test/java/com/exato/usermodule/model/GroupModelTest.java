package com.exato.usermodule.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class GroupModelTest {

		    @Test
	    void testGroupModelGettersSetters() {
	        // Arrange
	        Long clientId = 1L;
	        Long groupId = 123L;
	        String groupName = "TestGroupName";

	        // Act
	        GroupModel groupModel = new GroupModel();
	        groupModel.setClientId(clientId);
	        groupModel.setGroupId(groupId);
	        groupModel.setGroupName(groupName);

	        // Assert
	        assertNotNull(groupModel);
	        assertEquals(clientId, groupModel.getClientId());
	        assertEquals(groupId, groupModel.getGroupId());
	        assertEquals(groupName, groupModel.getGroupName());
	    }

}
