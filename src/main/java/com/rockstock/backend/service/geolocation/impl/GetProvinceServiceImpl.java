package com.rockstock.backend.service.geolocation.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.entity.geolocation.Province;
import com.rockstock.backend.infrastructure.geolocation.dto.GetProvinceResponseDTO;
import com.rockstock.backend.infrastructure.geolocation.repository.ProvinceRepository;
import com.rockstock.backend.service.geolocation.GetProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GetProvinceServiceImpl implements GetProvinceService {

    private final ProvinceRepository provinceRepository;

    @Override
    @Transactional
    public List<Province> getAllProvinces() {return provinceRepository.findAll(); }

    @Override
    @Transactional
    public Optional<Province> getByProvinceName(String name) {
        Optional<Province> provinceName = provinceRepository.findByNameContainingIgnoreCase(name);
        if (provinceName.isEmpty()){
            throw new DataNotFoundException("Province not found !");
        }
        return provinceName;
    }

    @Override
    @Transactional
    public Optional<Province> getByProvinceId(Long provinceId) {
        Optional<Province> province = provinceRepository.findById(provinceId);
        if (province.isEmpty()){
            throw new DataNotFoundException("Province not found !");
        }
        return province;
    }




}
