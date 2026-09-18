package com.example.studentplacementmanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()

                        .requestMatchers("/api/v1/students/**")
                        .hasAnyRole(
                                "STUDENT",
                                "PLACEMENT_OFFICER",
                                "ADMIN"
                        )

                        .requestMatchers("/api/v1/companies/**")
                        .hasAnyRole(
                                "COMPANY",
                                "PLACEMENT_OFFICER",
                                "ADMIN"
                        )

                        .requestMatchers("/api/v1/job-drives/**")
                        .hasAnyRole(
                                "STUDENT",
                                "COMPANY",
                                "PLACEMENT_OFFICER",
                                "ADMIN"
                        )

                        .requestMatchers("/api/v1/applications/**")
                        .hasAnyRole(
                                "STUDENT",
                                "COMPANY",
                                "PLACEMENT_OFFICER",
                                "ADMIN"
                        )

                        .requestMatchers("/api/v1/interviews/**")
                        .hasAnyRole(
                                "STUDENT",
                                "COMPANY",
                                "PLACEMENT_OFFICER",
                                "ADMIN"
                        )

                        .requestMatchers("/api/v1/offers/**")
                        .hasAnyRole(
                                "STUDENT",
                                "COMPANY",
                                "PLACEMENT_OFFICER",
                                "ADMIN"
                        )

                        .requestMatchers("/api/v1/resumes/**")
                        .hasAnyRole(
                                "STUDENT",
                                "PLACEMENT_OFFICER",
                                "ADMIN"
                        )

                        .requestMatchers("/api/v1/notifications/**")
                        .authenticated()

                        .requestMatchers("/api/v1/audits/**")
                        .hasAnyRole(
                                "PLACEMENT_OFFICER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                "/actuator/**"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(
                                                HttpStatus.UNAUTHORIZED.value(),
                                                "Unauthorized"
                                        )
                        )
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}