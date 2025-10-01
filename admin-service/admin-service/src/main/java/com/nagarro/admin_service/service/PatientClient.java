package com.nagarro.admin_service.service;

import com.nagarro.admin_service.config.FeignConfig;
import com.nagarro.admin_service.model.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "patient-service",configuration = FeignConfig.class)
public interface PatientClient {
    @GetMapping("/api/patients")
    List<PatientDto> getAllPatients();

    @GetMapping("/api/patients/{id}")
    PatientDto getPatientById(@PathVariable Long id);
}