package com.rockstock.backend.infrastructure.geolocation.repository;

import com.rockstock.backend.entity.geolocation.SubDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubDistrictRepository extends JpaRepository<SubDistrict, Long> {

    @Query("SELECT sd FROM SubDistrict sd WHERE sd.district.city.province.id = :provinceId")
    List<SubDistrict> findByProvinceId(Long provinceId);

    @Query("SELECT sd FROM SubDistrict sd WHERE sd.district.city.province.name = :name")
    List<SubDistrict> findByProvinceName(String name);

    @Query("SELECT sd FROM SubDistrict sd WHERE sd.district.city.id = :cityId")
    List<SubDistrict> findByCityId(Long cityId);

    @Query("SELECT sd FROM SubDistrict sd WHERE sd.district.city.name = :name")
    List<SubDistrict> findByCityName(String name);

    @Query("SELECT sd FROM SubDistrict sd WHERE sd.district.id = :districtId")
    List<SubDistrict> findByDistrictId(Long districtId);

    @Query("SELECT sd FROM SubDistrict sd WHERE sd.district.name = :name")
    List<SubDistrict> findByDistrictName(String name);

    @Query("SELECT sd FROM SubDistrict sd WHERE sd.name = :name")
    List<SubDistrict> findByNameContainingIgnoreCase(String name);
}
