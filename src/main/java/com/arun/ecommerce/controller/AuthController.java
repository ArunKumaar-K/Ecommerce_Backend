
package com.arun.ecommerce.controller;

import com.arun.ecommerce.dto.LoginRequest;
import com.arun.ecommerce.dto.UserRegisterRequest;
import com.arun.ecommerce.entity.User;
import com.arun.ecommerce.security.JwtService;
import com.arun.ecommerce.service.AuthService;
import com.arun.ecommerce.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            AuthService authService,
            JwtService jwtService) {
        this.userService = userService;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody UserRegisterRequest request) {

        User user = userService.register(request);

        Map<String, Object> response = Map.of(
                "message", "Registration successful",
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication authentication =
                authService.authenticate(request);

        String token = jwtService.generateToken(authentication);

        Map<String, Object> response = Map.of(
                "message", "Login successful",
                "tokenType", "Bearer",
                "accessToken", token,
                "expiresIn", 900
        );

        return ResponseEntity.ok(response);
    }
}
