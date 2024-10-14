package com.Proyecto.Integrador.controller.auth;

import com.Proyecto.Integrador.dto.AuthDto;
import com.Proyecto.Integrador.dto.LoginDto;
import com.Proyecto.Integrador.dto.RegisterDto;
import com.Proyecto.Integrador.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<AuthDto> Login(@RequestBody LoginDto login){
        AuthDto authDto = this.authService.login(login);
        if (authDto == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        return ResponseEntity.ok(authDto);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto> register(@RequestBody RegisterDto dto){
        try {
            AuthDto authDto = this.authService.register(dto);
            return ResponseEntity.ok(authDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(new AuthDto(e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Service is up and running!");
    }

    @GetMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@RequestParam String token) {
        try {
            boolean isValid = authService.verifyToken(token);
            return ResponseEntity.ok(isValid ? "Token is valid" : "Token is invalid");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid token");
        }
    }
}
