package com.rockstock.backend.infrastructure.user.repository;

import com.rockstock.backend.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    void deleteByUserId(Long userId);
}