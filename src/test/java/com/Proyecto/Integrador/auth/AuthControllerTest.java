package com.Proyecto.Integrador.auth;

import com.Proyecto.Integrador.controller.auth.AuthController;
import com.Proyecto.Integrador.dto.AuthDto;
import com.Proyecto.Integrador.dto.LoginDto;
import com.Proyecto.Integrador.dto.RegisterDto;
import com.Proyecto.Integrador.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin() {
        LoginDto loginDto = new LoginDto("testuser", "testpass");

        AuthDto authDto = new AuthDto("testtoken");

        when(authService.login(loginDto)).thenReturn(authDto);

        ResponseEntity<AuthDto> response = authController.Login(loginDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testtoken", response.getBody().getToken());
    }

    @Test
    public void testRegister() {
        RegisterDto registerDto = new RegisterDto("newuser", "newuser@example.com", "newpass");

        AuthDto authDto = new AuthDto("newtoken");

        when(authService.register(registerDto)).thenReturn(authDto);

        ResponseEntity<AuthDto> response = authController.register(registerDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newtoken", response.getBody().getToken());
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        LoginDto loginDto = new LoginDto("invaliduser", "invalidpass");

        when(authService.login(loginDto)).thenReturn(null);

        ResponseEntity<AuthDto> response = authController.Login(loginDto);

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testRegisterWithExistingUser() {
        RegisterDto registerDto = new RegisterDto("existinguser", "existinguser@example.com", "existingpass");

        when(authService.register(registerDto)).thenThrow(new RuntimeException("User already exists"));

        ResponseEntity<AuthDto> response = authController.register(registerDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User already exists", response.getBody().getToken());
    }

    @Test
    public void testLoginWithoutUsername() {
        LoginDto loginDto = new LoginDto(null, "testpass");
        loginDto.setPassword("testpass");

        ResponseEntity<AuthDto> response = authController.Login(loginDto);

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testRegisterWithoutEmail() {
        RegisterDto registerDto = new RegisterDto("newuser", null, "newpass");

        when(authService.register(registerDto)).thenThrow(new RuntimeException("Email is required"));

        ResponseEntity<AuthDto> response = authController.register(registerDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email is required", response.getBody().getToken());
    }

    @Test
    public void testRegisterWithoutPassword() {
        RegisterDto registerDto = new RegisterDto("newuser", "newuser@example.com", null);

        when(authService.register(registerDto)).thenThrow(new RuntimeException("Password is required"));

        ResponseEntity<AuthDto> response = authController.register(registerDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Password is required", response.getBody().getToken());
    }
}
