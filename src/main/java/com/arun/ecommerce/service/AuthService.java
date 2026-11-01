
package com.arun.ecommerce.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.arun.ecommerce.dto.LoginRequest;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthService(
            AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Authentication authenticate(LoginRequest request) {

        String email = request.email().trim().toLowerCase();

        var authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.password()
                );

        return authenticationManager.authenticate(authenticationToken);
    }
}
