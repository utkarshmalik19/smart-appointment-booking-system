package com.nagarro.api_gateway.config;

import com.nagarro.api_gateway.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthWebFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**").permitAll()// public endpoints
                        .pathMatchers("/api/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/doctors/**").hasAnyRole("DOCTOR","ADMIN")
                        .pathMatchers("/api/patients/**").hasAnyRole("PATIENT","ADMIN")
                        .pathMatchers("/api/appointments/**").hasAnyRole("PATIENT","DOCTOR","ADMIN")
                        .anyExchange().authenticated()
                )
                // add your JWT WebFilter
                .addFilterAt(jwtAuthWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}