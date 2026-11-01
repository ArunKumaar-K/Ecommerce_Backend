
package com.arun.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.HttpMethod;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.arun.ecommerce.controller.ProductController;
import com.arun.ecommerce.security.CustomUserDetailsService;
import com.arun.ecommerce.security.JwtAuthenticationFilter;
import com.arun.ecommerce.security.JwtService;
import com.arun.ecommerce.service.ProductService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;



@WebMvcTest(ProductController.class)
@Import(ProductSecurityTest.TestSecurityConfig.class)
class ProductSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void customerShouldAccessGetProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .with(user("customer").roles("CUSTOMER")))
            .andExpect(status().isOk());
    }
    
    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http)
                throws Exception {

            http
                .csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(auth -> auth
				    .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
				    .requestMatchers(HttpMethod.GET, "/api/products/**")
				        .hasAnyRole("CUSTOMER", "ADMIN")
				    .requestMatchers(HttpMethod.POST, "/api/products/**")
				        .hasRole("ADMIN")
				    .requestMatchers(HttpMethod.PUT, "/api/products/**")
				        .hasRole("ADMIN")
				    .requestMatchers(HttpMethod.PATCH, "/api/products/**")
				        .hasRole("ADMIN")
				    .requestMatchers(HttpMethod.DELETE, "/api/products/**")
				        .hasRole("ADMIN")
				    .anyRequest().authenticated()
				)

                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());

            return http.build();
        }
    }
    

	@Test
	void customerShouldBeForbiddenFromCreatingProduct() throws Exception {
	    mockMvc.perform(post("/api/products")
	            .with(user("customer").roles("CUSTOMER")))
	        .andExpect(status().isForbidden());
	}
	
	@Test
	void adminShouldBeAllowedToCreateProduct() throws Exception {
	    mockMvc.perform(post("/api/products")
	            .with(user("admin").roles("ADMIN")))
	        .andExpect(status().isOk());
	}
	

	@Test
	void customerShouldBeForbiddenFromUpdatingProduct() throws Exception {
	    mockMvc.perform(put("/api/products/1")
	            .with(user("customer").roles("CUSTOMER")))
	        .andExpect(status().isForbidden());
	}
	
	@Test
	void customerShouldBeForbiddenFromPatchingProduct() throws Exception {
	    mockMvc.perform(patch("/api/products/1")
	            .with(user("customer").roles("CUSTOMER")))
	        .andExpect(status().isForbidden());
	}
	
	@Test
	void customerShouldBeForbiddenFromDeletingProduct() throws Exception {
	    mockMvc.perform(delete("/api/products/1")
	            .with(user("customer").roles("CUSTOMER")))
	        .andExpect(status().isForbidden());
	}
	 
	//Now we'll verify that ADMIN users are not blocked by role authorization for these three operations.

	
	@Test
	void adminShouldBeAllowedToUpdateProduct() throws Exception {
	    mockMvc.perform(put("/api/products/1")
	            .with(user("admin").roles("ADMIN")))
	        .andExpect(status().isOk());
	}
	
	@Test
	void adminShouldBeAllowedToPatchProduct() throws Exception {
	    mockMvc.perform(patch("/api/products/1")
	            .with(user("admin").roles("ADMIN")))
	        .andExpect(status().isOk());
	}
	
	@Test
	void adminShouldBeAllowedToDeleteProduct() throws Exception {
	    mockMvc.perform(delete("/api/products/1")
	            .with(user("admin").roles("ADMIN")))
	        .andExpect(status().isOk());
	}




}
