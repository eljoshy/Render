package com.Proyecto.Integrador.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private String token;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetails = new User("testuser", "password", Collections.emptyList());
        token = jwtService.getToken(userDetails);
    }

    @Test
    public void testGetToken() {
        String generatedToken = jwtService.getToken(userDetails);
        assertNotNull(generatedToken);
    }

    @Test
    public void testGetUsernameFromToken() {
        String username = jwtService.getUsernameFromToken(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    public void testIsTokenValid() {
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void testIsTokenExpired() {
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
    public void testGetClaim() {
        String username = jwtService.getClaim(token, Claims::getSubject);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    public void testGetAllClaims() {
        Claims claims = jwtService.getAllClaims(token);
        assertEquals(userDetails.getUsername(), claims.getSubject());
    }

    @Test
    public void testGetExpiration() {
        Date expiration = jwtService.getExpiration(token);
        assertNotNull(expiration);
    }
}
