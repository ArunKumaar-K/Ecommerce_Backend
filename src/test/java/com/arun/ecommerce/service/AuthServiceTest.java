
package com.arun.ecommerce.service;

import com.arun.ecommerce.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Test
    void authenticateShouldNormalizeEmailAndReturnAuthentication() {
        // Arrange
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);

        AuthService authService =
                new AuthService(authenticationManager);

        LoginRequest request =
                new LoginRequest("  ARUN@EXAMPLE.COM  ", "Password123!");

        Authentication expectedAuthentication =
                mock(Authentication.class);

        when(authenticationManager.authenticate(
                org.mockito.ArgumentMatchers.any(
                        UsernamePasswordAuthenticationToken.class)))
                .thenReturn(expectedAuthentication);

        // Act
        Authentication actualAuthentication =
                authService.authenticate(request);

        // Assert
        assertSame(expectedAuthentication, actualAuthentication);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        "arun@example.com",
                        "Password123!"
                )
        );
    }
}
