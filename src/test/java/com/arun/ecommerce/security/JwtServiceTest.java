
package com.arun.ecommerce.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import java.util.Base64;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void generateTokenShouldContainCorrectClaims() {
        // Arrange: use a Base64-encoded 256-bit test key
        String secret =
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

        JwtService jwtService = new JwtService(secret, 900000);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "arun@example.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_CUSTOMER")
                        )
                );

        // Act
        String token = jwtService.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());

        Claims claims = jwtService.extractClaims(token);

        assertEquals("arun@example.com", claims.getSubject());
        assertEquals(
                List.of("ROLE_CUSTOMER"),
                claims.get("authorities", List.class)
        );
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));

        assertTrue(jwtService.isTokenValid(token, "arun@example.com"));
        assertFalse(jwtService.isTokenValid(token, "wrong@example.com"));
    }
    

	@Test
	void expiredTokenShouldBeRejected() {
	    String secret =
	            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
	
	    JwtService jwtService = new JwtService(secret, 900000);
	
	    // Generate a correctly signed token that is already expired.
	    byte[] keyBytes = Base64.getDecoder().decode(secret);
	    SecretKey key = Keys.hmacShaKeyFor(keyBytes);
	
	    Date now = new Date();
	    Date expiredTime = new Date(now.getTime() - 60_000);
	
	    String expiredToken = Jwts.builder()
	            .subject("arun@example.com")
	            .issuedAt(new Date(now.getTime() - 120_000))
	            .expiration(expiredTime)
	            .signWith(key)
	            .compact();
	
	    assertFalse(
	            jwtService.isTokenValid(expiredToken, "arun@example.com")
	    );
	}
	
	@Test
	void tokenSignedWithWrongKeyShouldBeRejected() {
	    String validSecret =
	            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
	
	    String wrongSecret =
	            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWYxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=";
	
	    JwtService jwtService = new JwtService(validSecret, 900000);
	
	    SecretKey wrongKey = Keys.hmacShaKeyFor(
	            Base64.getDecoder().decode(wrongSecret)
	    );
	
	    String token = Jwts.builder()
	            .subject("arun@example.com")
	            .issuedAt(new Date())
	            .expiration(new Date(System.currentTimeMillis() + 900000))
	            .signWith(wrongKey)
	            .compact();
	
	    assertFalse(
	            jwtService.isTokenValid(token, "arun@example.com")
	    );
	}

}
