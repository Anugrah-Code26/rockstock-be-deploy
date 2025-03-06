package com.rockstock.backend.service.user.auth.impl;

import com.rockstock.backend.entity.user.Role;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.user.auth.CreateUserService;
import com.rockstock.backend.infrastructure.user.auth.dto.CreateUserRequestDTO;
import com.rockstock.backend.infrastructure.user.repository.RoleRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.service.user.auth.EmailVerificationService;
import com.rockstock.backend.common.exceptions.DuplicateDataException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CreateUserServiceImpl implements CreateUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public CreateUserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationService emailVerificationService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
    }

    @Override
    @Transactional
    public User createUser(CreateUserRequestDTO req) {
        Optional<User> checkUser = userRepository.findByEmailContainsIgnoreCase(req.getEmail());
        if (checkUser.isPresent()) {
            throw new DuplicateDataException("Email already exists");
        }

        // Create user entity without password
        User newUser = req.toEntity();

        // Assign "Customer" role
        Optional<Role> customerRole = roleRepository.findByName("Customer");
        if (customerRole.isPresent()) {
            newUser.getRoles().add(customerRole.get());
        } else {
            throw new RuntimeException("Role 'Customer' not found");
        }

        // Set verification status to false during registration
        newUser.setIsVerified(false);

        // Save user entity first to persist the user
        newUser = userRepository.save(newUser);

        // Send verification email after registration
        emailVerificationService.sendVerificationEmail(newUser.getEmail());

        return newUser;
    }
}
