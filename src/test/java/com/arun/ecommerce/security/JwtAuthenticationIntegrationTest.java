package com.arun.ecommerce.security;

import com.arun.ecommerce.config.SecurityConfig;
import com.arun.ecommerce.controller.ProductController;
import com.arun.ecommerce.dto.ProductRequest;
import com.arun.ecommerce.dto.ProductResponse;
import com.arun.ecommerce.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        CustomUserDetailsService.class
})
class JwtAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private UserDetails customer;
    private UserDetails admin;

    @BeforeEach
    void setUp() {
        customer = User.withUsername("customer@example.com")
                .password("unused")
                .roles("CUSTOMER")
                .build();

        admin = User.withUsername("admin@example.com")
                .password("unused")
                .roles("ADMIN")
                .build();

        when(userDetailsService.loadUserByUsername(
                "customer@example.com"))
                .thenReturn(customer);

        when(userDetailsService.loadUserByUsername(
                "admin@example.com"))
                .thenReturn(admin);

        when(productService.getAllProducts())
                .thenReturn(List.of());
    }

    private String generateToken(UserDetails user) {
        return jwtService.generateToken(
                new org.springframework.security.authentication
                        .UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                )
        );
    }

    @Test
    void requestWithoutTokenShouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithInvalidTokenShouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerWithValidTokenShouldAccessProducts()
            throws Exception {

        String token = generateToken(customer);

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void customerWithValidTokenShouldNotCreateProduct()
            throws Exception {

        String token = generateToken(customer);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Test Product",
                                  "description": "Integration test product",
                                  "price": 99.99,
                                  "stockQuantity": 10
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminWithValidTokenShouldCreateProduct()
            throws Exception {

        String token = generateToken(admin);

        ProductResponse response = org.mockito.Mockito.mock(
                ProductResponse.class);

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Test Product",
                                  "description": "Integration test product",
                                  "price": 99.99,
                                  "stockQuantity": 10
                                }
                                """))
                .andExpect(status().isCreated());
    }
}