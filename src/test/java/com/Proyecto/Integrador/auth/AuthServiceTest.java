package com.Proyecto.Integrador.auth;

import com.Proyecto.Integrador.config.JwtService;
import com.Proyecto.Integrador.dto.AuthDto;
import com.Proyecto.Integrador.dto.LoginDto;
import com.Proyecto.Integrador.dto.RegisterDto;
import com.Proyecto.Integrador.entity.UserMongoEntity;
import com.Proyecto.Integrador.repository.UserMongoRepository;
import com.Proyecto.Integrador.service.AuthService;
import com.Proyecto.Integrador.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserMongoRepository userMongoRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin() {
        LoginDto loginDto = new LoginDto("testuser@example.com", "testpass");
        UserMongoEntity user = new UserMongoEntity();
        user.setEmail("testuser@example.com");

        when(userMongoRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(jwtService.getToken(user)).thenReturn("testtoken");

        AuthDto result = authService.login(loginDto);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("testuser@example.com", "testpass"));
        assertEquals("testtoken", result.getToken());
    }

    @Test
    public void testRegister() {
        RegisterDto registerDto = new RegisterDto("newuser", "newuser@example.com", "newpass");
        UserMongoEntity user = new UserMongoEntity();
        user.setName("newuser");
        user.setEmail("newuser@example.com");
        user.setPassword("encodedpass");
        user.setRole(Role.USER);

        when(passwordEncoder.encode("newpass")).thenReturn("encodedpass");
        when(jwtService.getToken(user)).thenReturn("newtoken");

        AuthDto result = authService.register(registerDto);

        verify(userMongoRepository).save(any(UserMongoEntity.class));
        assertEquals(null, result.getToken());
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        LoginDto loginDto = new LoginDto("invaliduser@example.com", "invalidpass");

        when(userMongoRepository.findByEmail("invaliduser@example.com")).thenReturn(Optional.empty());

        try {
            authService.login(loginDto);
        } catch (Exception e) {
            assertEquals("No value present", e.getMessage());
        }
    }

    @Test
    public void testRegisterWithExistingUser() {
        RegisterDto registerDto = new RegisterDto("existinguser", "existinguser@example.com", "existingpass");
        UserMongoEntity existingUser = new UserMongoEntity();
        existingUser.setEmail("existinguser@example.com");

        when(userMongoRepository.findByEmail("existinguser@example.com")).thenReturn(Optional.of(existingUser));

        try {
            authService.register(registerDto);
        } catch (Exception e) {
            assertEquals("User already exists", e.getMessage());
        }
    }

    @Test
    public void testRegisterWithoutEmail() {
        RegisterDto registerDto = new RegisterDto("newuser", null, "newpass");

        try {
            authService.register(registerDto);
        } catch (Exception e) {
            assertEquals("Email is required", e.getMessage());
        }
    }
}
