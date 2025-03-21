package com.rockstock.backend.service.admin;

import com.rockstock.backend.entity.user.Role;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.entity.user.UserRole;
import com.rockstock.backend.infrastructure.admin.dto.AdminCreateRequestDTO;
import com.rockstock.backend.infrastructure.admin.dto.AdminResponseDTO;
import com.rockstock.backend.infrastructure.admin.dto.AdminUpdateRequestDTO;
//import com.rockstock.backend.infrastructure.admin.repository.AdminRepository;
import com.rockstock.backend.infrastructure.user.repository.RoleRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public AdminResponseDTO createAdmin(AdminCreateRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        Optional<Role> role = roleRepository.findByName("Warehouse Admin");


        User admin = new User();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setIsAdmin(true);
        admin.setFullname(request.getFullname());

        User savedUser = userRepository.save(admin);
        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(role.get());
        userRoleRepository.save(userRole);
        return new AdminResponseDTO(admin.getId(), admin.getEmail(), role.get().getName(), admin.getFullname());
    }


    public List<AdminResponseDTO> getAllAdmins() {
        List<User> admins = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("Warehouse Admin")))
                .collect(Collectors.toList());

        return admins.stream()
                .map(user -> new AdminResponseDTO(user.getId(), user.getEmail(),
                        user.getRoles().stream().findFirst().map(Role::getName).orElse("UNKNOWN"), user.getFullname()))
                .collect(Collectors.toList());
    }


    @Transactional
    public AdminResponseDTO updateAdmin(Long adminId, AdminUpdateRequestDTO request) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            admin.setEmail(request.getEmail());
        }
        if (request.getFullname() != null && !request.getFullname().isEmpty()) {
            admin.setFullname(request.getFullname());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            Role newRole = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            admin.getRoles().clear();
            admin.getRoles().add(newRole);
        }

        userRepository.save(admin);

        return new AdminResponseDTO(
                admin.getId(),
                admin.getEmail(),
                admin.getRoles().stream().findFirst().map(Role::getName).orElse("UNKNOWN"),
                admin.getFullname()
        );
    }



    public void deleteAdmin(Long requesterId, Long adminId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));


        boolean isSuperAdmin = requester.getRoles().stream()
                .anyMatch(role -> role.getName().equals("Super Admin"));

        if (!isSuperAdmin) {
            throw new RuntimeException("Unauthorized: Only Super Admin can delete admins");
        }

        if (!userRepository.existsById(adminId)) {
            throw new RuntimeException("Admin not found");
        }

        userRepository.deleteById(adminId);
    }


    public void softDeleteAdmin(Long adminId) {
        // Temukan admin berdasarkan ID
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Tandai admin sebagai deleted (soft delete)
        admin.setIsDeleted(true); // Pastikan ada field isDeleted pada entity Admin
        userRepository.save(admin); // Simpan perubahan
    }
}
