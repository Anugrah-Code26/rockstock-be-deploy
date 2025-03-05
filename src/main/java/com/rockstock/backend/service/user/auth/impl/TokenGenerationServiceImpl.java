package com.rockstock.backend.service.user.auth.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.entity.user.Role;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.entity.user.UserRole;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.service.user.auth.TokenGenerationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TokenGenerationServiceImpl implements TokenGenerationService {
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;

    private final long ACCESS_TOKEN_EXPIRY = 3600L; // 1 hours
    private final long REFRESH_TOKEN_EXPIRY = 86400L; // 24 hours

    public TokenGenerationServiceImpl(JwtEncoder jwtEncoder, UserRepository userRepository, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public String generateToken(Authentication authentication, TokenType tokenType) {
        Instant now = Instant.now();
        long expiry = (tokenType == TokenType.ACCESS) ? ACCESS_TOKEN_EXPIRY : REFRESH_TOKEN_EXPIRY;

        String email = authentication.getName();

        User user = userRepository.findByEmailContainsIgnoreCase(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

//        String scope = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .reduce((a, b) -> a + " " + b)
//                .orElse("");

        List<Long> warehouseIds = user.getWarehouseAdmins().stream()
                .map(warehouseAdmin -> warehouseAdmin.getWarehouse().getId())
                .toList();

        String roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.joining(" "));

        System.out.println("check " + roles + " " + user.getId());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(email)
                .claim("roles", roles)
                .claim("warehouseIds", warehouseIds)
                .claim("userId", user.getId())
                .claim("type", tokenType.name())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        Jwt jwt = this.jwtDecoder.decode(refreshToken);
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ACCESS_TOKEN_EXPIRY))
                .subject(jwt.getSubject())
                .claim("scope", jwt.getClaimAsString("scope"))
                .claim("userId", jwt.getClaimAsString("userId"))
                .claim("type", TokenType.ACCESS.name())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String generateEmailVerificationToken(String email, Long userId) {
        Instant now = Instant.now();
        long expiry = 3600L; // Token berlaku 1 jam

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(email)
                .claim("userId", userId)
                .claim("type", "EMAIL_VERIFICATION") // Tambahkan tipe khusus
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

}