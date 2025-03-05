package com.rockstock.backend.infrastructure.geolocation.repository;

import com.rockstock.backend.entity.geolocation.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query("SELECT c FROM City c WHERE c.province.id = :provinceId")
    List<City> findByProvinceId(Long provinceId);

    @Query("SELECT c FROM City c WHERE c.province.name = :name")
    List<City> findByProvinceName(String name);

    @Query("SELECT c FROM City c WHERE c.name = :name")
    List<City> findByNameContainingIgnoreCase(String name);
}
