//package com.rockstock.backend.infrastructure.admin.repository;
//
//import com.rockstock.backend.entity.user.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface AdminRepository extends JpaRepository<User, Long> {
//
//    boolean existsByEmail(String email);
//
//
//    Optional<User> findByEmailAndRoleIn(String email, List<String> roles);
//
//
//    List<User> findByRoleIn(List<String> roles);
//}
