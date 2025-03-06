package com.rockstock.backend.infrastructure.geolocation.repository;

import com.rockstock.backend.entity.geolocation.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

    @Query("SELECT d FROM District d WHERE d.city.province.id = :provinceId")
    List<District> findByProvinceId(Long provinceId);

    @Query("SELECT d FROM District d WHERE d.city.province.name = :name")
    List<District> findByProvinceName(String name);

    @Query("SELECT d FROM District d WHERE d.city.id = :cityId")
    List<District> findByCityId(Long cityId);

    @Query("SELECT d FROM District d WHERE d.city.name = :name")
    List<District> findByCityName(String name);

    @Query("SELECT d FROM District d WHERE d.name = :name")
    List<District> findByNameContainingIgnoreCase(String name);
}
