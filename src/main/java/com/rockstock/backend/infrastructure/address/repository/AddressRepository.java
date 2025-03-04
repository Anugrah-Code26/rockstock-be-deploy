package com.rockstock.backend.infrastructure.address.repository;

import com.rockstock.backend.entity.geolocation.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.deletedAt IS NULL")
    List<Address> findByUserId(Long userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.id = :addressId AND a.deletedAt IS NULL")
    Optional<Address> findByUserIdAndAddressId(Long userId, Long addressId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isMain = true AND a.deletedAt IS NULL")
    Optional<Address> findByUserIdAndIsMainTrue(Long userId, boolean isMain);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND LOWER(TRIM(a.label)) = LOWER(TRIM(:label)) AND a.deletedAt IS NULL")
    Optional<Address> findByUserIdAndLabel(Long userId, String label);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.deletedAt IS NOT NULL")
    List<Address> findAllDeletedAddressesByUserId(Long userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.deletedAt IS NOT NULL")
    Optional<Address> findDeletedAddressByUserIdAndAddressId(Long userId, Long addressId);

    @Modifying
    @Query("UPDATE Address a SET a.isMain = false WHERE a.user.id = :userId AND a.isMain = true")
    void resetMainAddress(Long userId);

}
