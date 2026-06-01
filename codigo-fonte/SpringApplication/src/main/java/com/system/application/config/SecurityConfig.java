package com.system.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    @Profile("test")
    public SecurityFilterChain testPublicChain(HttpSecurity http) {
        http
                .securityMatcher(
                        "/console-h2/**",
                        "/school-admins",
                        "/actuator",
                        "/auth/login",
                        "/auth/refresh",
                        "/auth/logout",
                        "/auth/login/admin",
                        "/auth/verify"
                )
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2ResourceServer(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    @Profile("test")
    public SecurityFilterChain testProtectedChain(HttpSecurity http) {
        http
                .cors(withDefaults())
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    @Order(1)
    @Profile({"prod", "dev"})
    public SecurityFilterChain publicRegisterChain(HttpSecurity http) {
        http
                .securityMatcher(
                        "/auth/**",
                        "/school-admins",
                        "/webhooks/mercado-pago"
                )
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/school-admins",
                                "/auth/login",
                                "/auth/refresh",
                                "/auth/logout",
                                "/auth/login/admin",
                                "/webhooks/mercado-pago"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/auth/verify"
                        ).permitAll()
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    @Profile({"prod", "dev"})
    public SecurityFilterChain securedChain(HttpSecurity http) {
        http
                .cors(withDefaults())
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
