package com.roottherapy.backend.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//        return http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/register/client", "/api/auth/login").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Public authentication endpoints
                        .requestMatchers(
                                "/api/auth/register/client",
                                "/api/auth/login"
                        ).permitAll()

                        // Public website content
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/services",
                                "/api/services/**",
                                "/api/therapists/public",
                                "/api/therapists/public/**",
                                "/api/blog",
                                "/api/blog/**",
                                "/api/gallery",
                                "/api/gallery/**",
                                "/uploads/**"
                        ).permitAll()

                        // Therapist self-management
                        .requestMatchers(
                                "/api/therapist-profile",
                                "/api/therapist-profile/**",
                                "/api/therapist/blog",
                                "/api/therapist/blog/**",
                                "/api/therapist/uploads",
                                "/api/therapist/uploads/**"
                        ).hasRole("THERAPIST")

                        // Admin content management
                        .requestMatchers(
                                "/api/admin/services",
                                "/api/admin/services/**",
                                "/api/admin/uploads",
                                "/api/admin/uploads/**",
                                "/api/admin/blog",
                                "/api/admin/blog/**",
                                "/api/admin/therapists",
                                "/api/admin/therapists/**",
                                "/api/admin/gallery",
                                "/api/admin/gallery/**"
                        ).hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                ).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    // allows spring security context to persist into the jdbc http session
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}
