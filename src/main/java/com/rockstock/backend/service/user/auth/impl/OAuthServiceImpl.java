package com.rockstock.backend.service.user.auth.impl;

import com.rockstock.backend.entity.user.Role;
import com.rockstock.backend.entity.user.UserProvider;
import com.rockstock.backend.infrastructure.user.auth.dto.OAuthLoginResponse;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.user.repository.RoleRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.service.user.auth.OAuthService;
import com.rockstock.backend.service.user.auth.TokenGenerationService;
import com.rockstock.backend.service.user.auth.TokenGenerationService.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.Optional;

@Service
public class OAuthServiceImpl implements OAuthService {
    private final UserRepository userRepository;
    private final TokenGenerationService tokenGenerationService;
    private final RestTemplate restTemplate;

@Autowired
private RoleRepository roleRepository;

    public OAuthServiceImpl(UserRepository userRepository, TokenGenerationService tokenGenerationService, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.tokenGenerationService = tokenGenerationService;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public OAuthLoginResponse processGoogleLogin(String idToken, String accessToken) {
        String googleApiUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idToken;
        String response = restTemplate.getForObject(googleApiUrl, String.class);
        JSONObject json = new JSONObject(response);

        String email = json.getString("email");
        String fullName = json.optString("name", "");
        String pictureUrl = json.optString("picture", "");

        Optional<User> existingUser = userRepository.findByEmailContainsIgnoreCase(email);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setFullname(fullName);
            user.setGoogleImageUrl(pictureUrl);
            UserProvider provider = new UserProvider();
            provider.setProvider("google");
            user.setUserProvider(provider);
            Optional<Role> customerRole = roleRepository.findByName("Customer");
            if (customerRole.isPresent()) {
                user.getRoles().add(customerRole.get());
            } else {
                throw new RuntimeException("Role 'Customer' not found");
            }
            userRepository.save(user);

        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null);
        String jwtAccessToken = tokenGenerationService.generateToken(authentication, TokenType.ACCESS);
        String jwtRefreshToken = tokenGenerationService.generateToken(authentication, TokenType.REFRESH);

        return new OAuthLoginResponse(jwtAccessToken, jwtRefreshToken, user.getFullname(), user.getEmail(), pictureUrl);

    }
}
