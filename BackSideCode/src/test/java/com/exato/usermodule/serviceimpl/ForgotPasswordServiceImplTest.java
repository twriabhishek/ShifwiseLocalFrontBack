/**package com.exato.usermodule.serviceimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.exato.usermodule.config.CallNotification;
import com.exato.usermodule.config.CustomException;
import com.exato.usermodule.entity.ClientInfo;
import com.exato.usermodule.entity.Role;
import com.exato.usermodule.entity.User;
import com.exato.usermodule.model.ForgotModel;
import com.exato.usermodule.model.NewPasswordModel;
import com.exato.usermodule.model.VerifyOTPModel;
import com.exato.usermodule.repository.ClientInfoRepository;
import com.exato.usermodule.repository.UserRepository;
import com.exato.usermodule.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceImplTest {
	
	    @InjectMocks
	    private ForgotPasswordServiceImpl forgotPasswordService;

	    @Mock
	    private UserRepository userRepository;

	    @Mock
	    private ClientInfoRepository clientRepository;

	    @Mock
	    private PasswordEncoder passwordEncoder;

	    @Mock
	    private CallNotification callNotification;

	    @Mock
	    private AuditLogService auditLogService;

	    @Mock
	    private HttpServletRequest request;
	    
	    private User createMockUser() {
	        User mockUser = new User();
	        mockUser.setId(1L);
	        mockUser.setClientId(1L);
	        mockUser.setFirstName("John");
	        mockUser.setLastName("Doe");
	        mockUser.setEmail("john.doe@example.com");
	        mockUser.setAssignedRoles(Collections.singleton(createMockRole()));
	        // Set other properties as needed
	        return mockUser;
	    }
	    
	    private Role createMockRole() {
	        Role mockRole = new Role();
	        mockRole.setId(1L);
	        mockRole.setName("ROLE_USER");
	        // Set other properties as needed
	        return mockRole;
	    }
	    
	    private ClientInfo createMockClient() {
	        ClientInfo client = new ClientInfo();
	        client.setClientId(1L);
	        // Set other properties as needed
	        return client;
	    }
	    
	    private HttpServletRequest mockHttpServletRequest() {
	        HttpServletRequest request = mock(HttpServletRequest.class);
	        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
	        return request;
	    }

	    @Test
	    void testSendResetPasswordEmail_Success() {
	        // Arrange
	        ForgotModel forgotModel = mock(ForgotModel.class);
	        when(forgotModel.getEmail()).thenReturn("john.doe@example.com");

	        User user = createMockUser();
	     
	        // Mock the OTP
	        user.setOtpNumber("1234");
	       
	     // Test class
	        Mockito.when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(createMockUser()));
	        Mockito.when(callNotification.sendOTPEmail(eq("john.doe@example.com"), any(), eq("http://example.com"))).thenReturn(true);

	        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
	        when(request.getServletPath()).thenReturn("/servletPath");
	        // Act
	        ResponseEntity<String> responseEntity = forgotPasswordService.sendResetPasswordEmail(forgotModel, request);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("OTP sent successfully", responseEntity.getBody());

	    }
	    
	    @Test
	    void testSendResetPasswordEmail_EmailNotFound() {
	        // Arrange
	        ForgotModel forgotModel = mock(ForgotModel.class);
	        when(forgotModel.getEmail()).thenReturn("john.doe@example.com");

	     // Test class
	        Mockito.lenient().when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
	        Mockito.lenient().when(callNotification.sendOTPEmail(eq("john.doe@example.com"), any(), eq("http://example.com"))).thenReturn(true);

	        Mockito.lenient().when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
	        Mockito.lenient().when(request.getServletPath()).thenReturn("/servletPath");
	
	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> forgotPasswordService.sendResetPasswordEmail(forgotModel, request));
	        
	        // Assert
	        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	         }
	    
	    
	    @Test
	    void testSendResetPasswordEmail_InternalServerError() {
	        // Arrange
	        ForgotModel forgotModel = mock(ForgotModel.class);
	        when(forgotModel.getEmail()).thenReturn("john.doe@example.com");

	     // Test class
	        Mockito.lenient().when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(createMockUser()));
	        Mockito.lenient().when(callNotification.sendOTPEmail(eq("john.doe@example.com"), any(), eq("http://example.com"))).thenReturn(false);

	        Mockito.lenient().when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
	        Mockito.lenient().when(request.getServletPath()).thenReturn("/servletPath");
	
	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> forgotPasswordService.sendResetPasswordEmail(forgotModel, request));
	        
	        // Assert
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
	         }
	   	  
	    @Test
	    void testSendResetPasswordEmail_Exception() {
	        // Arrange
	        ForgotModel forgotModel = mock(ForgotModel.class);
	        Mockito.lenient().when(forgotModel.getEmail()).thenReturn("john.doe@example.com");

	        // Mock the UserRepository to return a user (email found)
	        User user = createMockUser();
	        Mockito.lenient().when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

	        // Mock the CallNotification behavior to simulate an exception
	        Mockito.lenient().when(callNotification.sendOTPEmail(anyString(), anyString(), anyString()))
	                .thenThrow(new RuntimeException("Simulated exception"));

	        Mockito.lenient().when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
	        Mockito.lenient().when(request.getServletPath()).thenReturn("/servletPath");

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> forgotPasswordService.sendResetPasswordEmail(forgotModel, request));

	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	    }

	   	    
	    @Test
	    void testVerifyOtp_Success() {
	        // Arrange
	        VerifyOTPModel verifyOTPModel = mock(VerifyOTPModel.class);
	       when(verifyOTPModel.getUserEmail()).thenReturn("john.doe@example.com");
	       when(verifyOTPModel.getOtpNumber()).thenReturn("123456");

	        User user = createMockUser();
	        user.setOtpNumber("123456");
	        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

	        // Act
	        ResponseEntity<VerifyOTPModel> responseEntity = forgotPasswordService.verifyOtp(verifyOTPModel);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

	        // Verify UserRepository interactions
	        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
	        verify(userRepository, times(1)).save(user);

	        // Ensure that the user's OTP is set to null after verification
	        assertNull(user.getOtpNumber());

	        // Verify HttpHeaders
	        HttpHeaders headers = responseEntity.getHeaders();
	        assertTrue(headers.containsKey(HttpHeaders.LOCATION));
	        assertEquals("/forgot/changePassword?email=john.doe@example.com", headers.getFirst(HttpHeaders.LOCATION));

	    	    }
	    
	    @Test
	    void testVerifyOtp_NoUser() {
	        // Arrange
	        VerifyOTPModel verifyOTPModel = mock(VerifyOTPModel.class);
	       when(verifyOTPModel.getUserEmail()).thenReturn("john.doe@example.com");
	       when(verifyOTPModel.getOtpNumber()).thenReturn("123456");

	        User user = createMockUser();
	        user.setOtpNumber("123456");
	        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

	      	     // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> forgotPasswordService.verifyOtp(verifyOTPModel));
	        
	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	    	    }
	    
	    @Test
	    void testVerifyOtp_WrongOtp() {
	        // Arrange
	        VerifyOTPModel verifyOTPModel = new VerifyOTPModel();
	        verifyOTPModel.setUserEmail("john.doe@example.com");
	        verifyOTPModel.setOtpNumber("123456"); // Assuming this is the wrong OTP

	        // Mock the UserRepository to return a user (email found)
	        User user = createMockUser();
	        user.setOtpNumber("654321"); // Setting a different OTP to simulate a wrong OTP
	        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> forgotPasswordService.verifyOtp(verifyOTPModel));

	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()); // Adjust based on your actual implementation
	    }
	    
	    @Test
	    void testChangePassword_Success() {
	        // Arrange
	        NewPasswordModel newPasswordModel = mock(NewPasswordModel.class);
	        when(newPasswordModel.getNewPassword()).thenReturn("newPassword123");

	        String email = "john.doe@example.com";

	        User user = createMockUser();
	        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

	        // Act
	        ResponseEntity<String> responseEntity = forgotPasswordService.changePassword(newPasswordModel, email);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("Password changed successfully", responseEntity.getBody());

	        // Verify UserRepository interactions
	        verify(userRepository, times(1)).findByEmail(email);
	        verify(userRepository, times(1)).save(user);

	        // Verify Password Encoding
	        verify(passwordEncoder, times(1)).encode("newPassword123");

	        // Verify AuditLogService interactions
	        verify(auditLogService, times(1)).createAuditLog(email, "password changed ", user.getClientId());

	    }
	    
	    @Test
	    void testChangePassword_NoUser() {
	        // Arrange
	        NewPasswordModel newPasswordModel = mock(NewPasswordModel.class);
	        Mockito.lenient().when(newPasswordModel.getNewPassword()).thenReturn("newPassword123");

	        String email = "john.doe@example.com";
	    
	        Mockito.lenient().when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> forgotPasswordService.changePassword(newPasswordModel,email));
	        
	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	      
	    }
	    
	    @Test
	    void testResetPassword_Success() {
	        // Arrange
	        NewPasswordModel newPasswordModel = mock(NewPasswordModel.class);
	        when(newPasswordModel.getNewPassword()).thenReturn("newPassword123");

	        String email = "john.doe@example.com";

	        User user = createMockUser();
	        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

	        ClientInfo client = createMockClient();
	        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));

	        // Act
	        ResponseEntity<String> responseEntity = forgotPasswordService.resetPassword(newPasswordModel, email);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertEquals("Password reset successfully", responseEntity.getBody());

	        // Verify UserRepository interactions
	        verify(userRepository, times(1)).findByEmail(email);
	        verify(userRepository, times(1)).save(user);

	        // Verify ClientRepository interactions
	        verify(clientRepository, times(1)).findByEmail(email);
	        verify(clientRepository, times(1)).save(client);

	        // Verify Password Encoding
	        verify(passwordEncoder, times(2)).encode("newPassword123"); // Twice, once for user, once for client

	        // Verify AuditLogService interactions
	        verify(auditLogService, times(1)).createAuditLog(email, "password reset ", user.getClientId());

	    }
 
	    
	    @Test
	    void testResetPassword_EmailNotFound() {
	        // Arrange
	        NewPasswordModel newPasswordModel = mock(NewPasswordModel.class);
	        Mockito.lenient().when(newPasswordModel.getNewPassword()).thenReturn("newPassword123");

	       String email = "john.doe@example.com";
	       Mockito.lenient().when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

	        ClientInfo client = createMockClient();
	        Mockito.lenient().when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
	        
	        // Act & Assert
	        CustomException exception = assertThrows(CustomException.class,
	                () -> forgotPasswordService.changePassword(newPasswordModel,email));
	        

	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	   
	      
	    }
 

}
**/