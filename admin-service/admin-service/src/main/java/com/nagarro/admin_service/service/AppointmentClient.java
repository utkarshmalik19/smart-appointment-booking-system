package com.nagarro.admin_service.service;

import com.nagarro.admin_service.config.FeignConfig;
import com.nagarro.admin_service.model.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "appointment-service",configuration = FeignConfig.class)
public interface AppointmentClient {
    @GetMapping("/api/appointments/{id}")
    AppointmentDto getAppointmentById(@PathVariable Long id);

    @GetMapping("/api/appointments/patient/{patientId}")
    Page<AppointmentDto> getAppointmentByPatientId(@PathVariable Long patientId,@RequestParam int page, @RequestParam int size);

    @GetMapping("/api/appointments/doctor/{doctorId}")
    Page<AppointmentDto> getAppointmentByDoctorId(@PathVariable Long doctorId,@RequestParam int page, @RequestParam int size);

    @DeleteMapping("/api/appointments/{id}")
    void deleteAppointment(@PathVariable Long id);
}