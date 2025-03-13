package com.rockstock.backend.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

public class CorsConfigurationSourceImpl implements CorsConfigurationSource {

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOriginPatterns(List.of(
                "http://localhost:3001", "http://localhost:3000",
                "http://0.0.0.0:3000", "http://host.docker.internal:3000",
                "https://rockstock.vercel.app", "https://rockstock-finpro.vercel.app", "http://rockstock-fe-deploy-test1-idimis-projects.vercel.app"
        ));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(
                List.of("Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        return corsConfiguration;
    }
}