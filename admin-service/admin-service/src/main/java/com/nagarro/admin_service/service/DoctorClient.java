package com.nagarro.admin_service.service;

import com.nagarro.admin_service.config.FeignConfig;
import com.nagarro.admin_service.model.DoctorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "doctor-service",configuration = FeignConfig.class)
public interface DoctorClient {
    @PostMapping("/api/doctors")
    DoctorDto createDoctor(@RequestBody DoctorDto doctor);

    @GetMapping("/api/doctors")
    List<DoctorDto> getAllDoctors();

    @GetMapping("/api/doctors/{id}")
    DoctorDto getDoctorById(@PathVariable Long id);

    @DeleteMapping("/api/doctors/{id}")
    void deleteDoctor(@PathVariable Long id);
}