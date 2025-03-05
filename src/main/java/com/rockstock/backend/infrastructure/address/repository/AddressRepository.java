package com.rockstock.backend.infrastructure.address.repository;

import com.rockstock.backend.entity.geolocation.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.deletedAt IS NULL")
    List<Address> findByUserId(Long userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.subDistrict.district.city.province.id = :provinceId AND a.deletedAt IS NULL")
    List<Address> findByUserIdAndProvinceId(Long userId, Long provinceId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.subDistrict.district.city.province.name = :name AND a.deletedAt IS NULL")
    List<Address> findByUserIdAndProvinceName(Long userId, String name);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.subDistrict.district.city.id = :cityId AND a.deletedAt IS NULL")
    List<Address> findByUserIdAndCityId(Long userId, Long cityId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.subDistrict.district.city.name = :name AND a.deletedAt IS NULL")
    List<Address> findByUserIdAndCityName(Long userId, String name);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.subDistrict.district.id = :districtId AND a.deletedAt IS NULL")
    List<Address> findByUserIdAndDistrictId(Long userId, Long districtId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.subDistrict.district.name = :name AND a.deletedAt IS NULL")
    List<Address> findByUserIdAndDistrictName(Long userId, String name);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.subDistrict.id = :subDistrictId AND a.deletedAt IS NULL")
    List<Address> findByUserIdAndSubDistrictId(Long userId, Long subDistrictId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.subDistrict.name = :name AND a.deletedAt IS NULL")
    List<Address> findByUserIdAndSubDistrictName(Long userId, String name);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.id = :addressId AND a.deletedAt IS NULL")
    Optional<Address> findByUserIdAndAddressId(Long userId, Long addressId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isMain = true AND a.deletedAt IS NULL")
    Optional<Address> findByUserIdAndIsMainTrue(Long userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND LOWER(TRIM(a.label)) = LOWER(TRIM(:label)) AND a.deletedAt IS NULL")
    Optional<Address> findByUserIdAndLabel(Long userId, String label);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.deletedAt IS NOT NULL")
    List<Address> findAllDeletedAddressesByUserId(Long userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.id = :addressId AND a.deletedAt IS NOT NULL")
    Optional<Address> findDeletedAddressByUserIdAndAddressId(Long userId, Long addressId);

    @Modifying
    @Query("UPDATE Address a SET a.isMain = false WHERE a.user.id = :userId AND a.isMain = true")
    void resetMainAddress(Long userId);

}
