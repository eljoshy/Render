package com.Proyecto.Integrador.service;

import com.Proyecto.Integrador.config.JwtService;
import com.Proyecto.Integrador.dto.AuthDto;
import com.Proyecto.Integrador.dto.LoginDto;
import com.Proyecto.Integrador.dto.RegisterDto;
import com.Proyecto.Integrador.entity.UserMongoEntity;
import com.Proyecto.Integrador.repository.UserMongoRepository;
import com.Proyecto.Integrador.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserMongoRepository userMongoRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthDto login(final LoginDto request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user = userMongoRepository.findByEmail(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user);
        return new AuthDto(token);
    }

    public AuthDto register(final RegisterDto request){
        UserMongoEntity user = new UserMongoEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userMongoRepository.save(user);
        return new AuthDto(this.jwtService.getToken(user));
    }

    public boolean verifyToken(String token) {
        try {
            String username = jwtService.getUsernameFromToken(token);
            UserDetails userDetails = userMongoRepository.findByEmail(username).orElse(null);
            return jwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
}
