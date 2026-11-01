
package com.arun.ecommerce.controller;

import com.arun.ecommerce.dto.LoginRequest;
import com.arun.ecommerce.security.JwtService;
import com.arun.ecommerce.service.AuthService;
import com.arun.ecommerce.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.arun.ecommerce.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void loginWithValidCredentialsShouldReturnAccessToken()
            throws Exception {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "arun@example.com",
                        null
                );

        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken(authentication))
                .thenReturn("mock.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "arun@example.com",
                                  "password": "Password123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Login successful"))
                .andExpect(jsonPath("$.tokenType")
                        .value("Bearer"))
                .andExpect(jsonPath("$.accessToken")
                        .value("mock.jwt.token"))
                .andExpect(jsonPath("$.expiresIn")
                        .value(900));
    }

    @Test
    void loginWithInvalidCredentialsShouldNotReturnSuccess()
            throws Exception {

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authService)
                .authenticate(any(LoginRequest.class));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "arun@example.com",
                                  "password": "WrongPassword!"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
