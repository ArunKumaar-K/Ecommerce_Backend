
package com.arun.ecommerce.config;

import com.arun.ecommerce.security.CustomUserDetailsService;
import com.arun.ecommerce.security.JwtAuthenticationFilter;
import com.arun.ecommerce.security.JwtService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        return new JwtAuthenticationFilter(
                jwtService, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {

    	http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

		.exceptionHandling(exceptions -> exceptions
		    .authenticationEntryPoint((request, response, exception) -> {
		        System.out.println("AUTHENTICATION FAILURE: "
		                + exception.getClass().getSimpleName()
		                + " - " + exception.getMessage());
		
		        response.sendError(
		                HttpServletResponse.SC_UNAUTHORIZED,
		                "Unauthorized"
		        );
		    })
		    .accessDeniedHandler((request, response, exception) -> {
		        System.out.println("ACCESS DENIED: "
		                + exception.getClass().getSimpleName()
		                + " - " + exception.getMessage());

		        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		        response.setContentType("application/json");
		        response.getWriter().write(
		                "{\"status\":403,\"error\":\"Forbidden\"}"
		        );
		    })
		)


        .authorizeHttpRequests(auth -> auth
        	.requestMatchers(
        			HttpMethod.POST,
        			"/api/auth/register",
        			"/api/auth/login"
        	).permitAll()

        	.requestMatchers(HttpMethod.GET, "/api/products/**")
        		.hasAnyRole("CUSTOMER", "ADMIN")

        	.requestMatchers(
        			HttpMethod.POST,
        			"/api/products/**"
        	).hasRole("ADMIN")

        	.requestMatchers(
        			HttpMethod.PUT,
        			"/api/products/**"
        	).hasRole("ADMIN")

        	.requestMatchers(
        			HttpMethod.PATCH,
        			"/api/products/**"
        	).hasRole("ADMIN")

        	.requestMatchers(
        			HttpMethod.DELETE,
        			"/api/products/**"
        			).hasRole("ADMIN")
        	
        	.requestMatchers("/error").permitAll()

        	.anyRequest().authenticated()
        )

        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}
