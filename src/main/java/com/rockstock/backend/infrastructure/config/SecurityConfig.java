package com.rockstock.backend.infrastructure.config;

import com.rockstock.backend.infrastructure.user.auth.security.TokenBlacklist;
import com.rockstock.backend.service.user.auth.GetUserAuthDetailsService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Log
public class SecurityConfig {

    private final GetUserAuthDetailsService getUserAuthDetailsService;
    private final JwtConfigProperties jwtConfigProperties;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklist tokenBlacklistFilter;

    public SecurityConfig(
            GetUserAuthDetailsService getUserAuthDetailsService,
            JwtConfigProperties jwtConfigProperties,
            PasswordEncoder passwordEncoder,
            TokenBlacklist tokenBlacklistFilter) {
        this.getUserAuthDetailsService = getUserAuthDetailsService;
        this.jwtConfigProperties = jwtConfigProperties;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistFilter = tokenBlacklistFilter;
    }

    @Bean
    public AuthenticationManager authManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(getUserAuthDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(new CorsConfigurationSourceImpl()))
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/api/v1/geolocations/**").permitAll()
                        .requestMatchers("/api/v1/user/register").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/google/login").permitAll()
                        .requestMatchers("/api/v1/auth/oauth/google").permitAll()
                        .requestMatchers("/api/v1/admin/**").permitAll()
                        .requestMatchers("/api/v1/user/**").permitAll()
                        .requestMatchers("/api/v1/user/profile").permitAll()
                        .requestMatchers("/api/v1/user/upload-avatar").permitAll()
                        .requestMatchers("/api/v1/user/confirm-reset-password").permitAll()
                        .requestMatchers("/api/v1/warehouse/**").permitAll()
                        .requestMatchers("/api/v1/warehouse-admins/**").permitAll()
                        .requestMatchers("/api/v1/auth/resend-verification").permitAll()
                        .requestMatchers("/api/v1/auth/forgot-password").permitAll()
                        .requestMatchers("/api/v1/auth/setup-password").permitAll()
                        .requestMatchers("/api/v1/auth/reset-password").permitAll()
                        .requestMatchers("/api/v1/auth/test-email").permitAll()
                        .requestMatchers("/api/v1/auth/verify-email").permitAll()
                        .requestMatchers("/api/v1/payments/webhook").permitAll()
                        .requestMatchers("/api/v1/warehouses/**").permitAll()
                        .requestMatchers("/api/v1/warehouse-admins/**").permitAll()

                        // private endpoints
                        .requestMatchers("/test/**").permitAll() // Allow test API access
                        .requestMatchers("/static/**").permitAll() // Allow email template
                        .requestMatchers("/static/**", "/public/**").permitAll() // Allow static resources
                        .requestMatchers("/verification-email.html").permitAll() // Allow email template
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/api/v1/addresses/**").permitAll()
                        .requestMatchers("/api/v1/products/**").permitAll()
                        .requestMatchers("/api/v1/stocks**").permitAll()
                        .requestMatchers("/api/v1/pictures/**").permitAll()
                        .requestMatchers("/api/v1/categories/**").permitAll()
                        .requestMatchers("/api/v1/stocks/**").permitAll()
                        .requestMatchers("/api/v1/mutations/**").permitAll()
                        // Allow static files
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()));
                    oauth2.bearerTokenResolver(request -> {
                        Cookie[] cookies = request.getCookies();
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if (cookie.getName().equals("SID")) {
                                    return cookie.getValue();
                                }
                            }
                        }

                        // Get from headers instead of cookies
                        var header = request.getHeader("Authorization");
                        if (header != null) {
                            return header.replace("Bearer ", "");
                        }

                        return null;
                    });
                })
                .addFilterAfter(tokenBlacklistFilter, BearerTokenAuthenticationFilter.class)
                .userDetailsService(getUserAuthDetailsService)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey originalKey = new SecretKeySpec(jwtConfigProperties.getSecret().getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(originalKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey key = new SecretKeySpec(jwtConfigProperties.getSecret().getBytes(), "HmacSHA256");
        JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<SecurityContext>(key);
        return new NimbusJwtEncoder(immutableSecret);
    }
}
