package com.rockstock.backend.infrastructure.user.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.extern.java.Log;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log
public class Claims {

    public static Map<String, Object> getClaimsFromJwt() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("JWT not found in SecurityContext");
        }

        return jwt.getClaims();
    }

    public static String getEmailFromJwt() {
        return (String) getClaimsFromJwt().get("sub");
    }

    public static String getJwtExpirationDate() {
        return getClaimsFromJwt().get("exp").toString();
    }

    public static String getJwtTokenString() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("JWT not found in SecurityContext");
        }

        return jwt.getTokenValue();
    }

    public static String getRoleFromJwt() {
        Object roles = getClaimsFromJwt().get("scope");
        if (roles instanceof String) {
            return (String) roles;
        } else if (roles instanceof Collection) {
            return ((Collection<?>) roles).stream()
                    .map(Object::toString)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public static List<Long> getWarehouseIdsFromJwt() {
        Object warehouseIds = getClaimsFromJwt().get("warehouseIds");

        if (warehouseIds instanceof Collection<?>) {
            return ((Collection<?>) warehouseIds).stream()
                    .filter(id -> id instanceof Number) // Ensure it's a number
                    .map(id -> ((Number) id).longValue()) // Convert to Long
                    .toList();
        }

        return List.of(); // Return an empty list if no warehouse IDs are found
    }

    public static String getTokenTypeFromJwt() {
        return (String) getClaimsFromJwt().get("type");
    }

    public static Long getUserIdFromJwt() {
        Object userId = getClaimsFromJwt().get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        } else if (userId instanceof Long) {
            return (Long) userId;
        } else if (userId instanceof String) {
            return Long.parseLong((String) userId);
        }
        throw new IllegalStateException("User ID not found in JWT");
    }

    public static String getRefreshTokenFromJwt() {
        return (String) getClaimsFromJwt().get("refresh_token");
    }

    public static String getRefreshTokenExpirationDate() {
        return getClaimsFromJwt().get("refresh_exp").toString();
    }
}