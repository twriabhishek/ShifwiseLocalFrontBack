package com.exato.usermodule.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RootControllerTest {

	  @InjectMocks
	    private RootController rootController;

	    @Test
	    void testRootData() {
	        // Act
	        String result = rootController.rootData();

	        // Assert
	        assertEquals("Hello World! Testing pipeline with exato - 17", result);
	    }

}
