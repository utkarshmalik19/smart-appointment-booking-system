package com.nagarro.admin_service.service;

import com.nagarro.admin_service.config.FeignConfig;
import com.nagarro.admin_service.model.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "appointment-service",configuration = FeignConfig.class)
public interface AppointmentClient {
    @GetMapping("/api/appointments/{id}")
    AppointmentDto getAppointmentById(@PathVariable Long id);

    @GetMapping("/api/appointments/patient/{patientId}")
    List<AppointmentDto> getAppointmentByPatientId(@PathVariable Long patientId);

    @GetMapping("/api/appointments/doctor/{doctorId}")
    List<AppointmentDto> getAppointmentByDoctorId(@PathVariable Long doctorId);

    @DeleteMapping("/api/appointments/{id}")
    void deleteAppointment(@PathVariable Long id);
}