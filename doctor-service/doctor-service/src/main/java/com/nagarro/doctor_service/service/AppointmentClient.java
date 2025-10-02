package com.nagarro.doctor_service.service;

import com.nagarro.doctor_service.config.FeignConfig;
import com.nagarro.doctor_service.model.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "appointment-service", configuration = FeignConfig.class)
public interface AppointmentClient {
    @GetMapping("/api/appointments/doctor/{doctorId}")
    Page<AppointmentDto> getAppointmentsByDoctor(@PathVariable("doctorId") Long doctorId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size);

    @GetMapping("/api/appointments/doctor/{doctorId}/appointments/pending")
    Page<AppointmentDto> getPendingAppointments(@PathVariable("doctorId") Long doctorId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size);

    @PutMapping("/api/appointments/{id}/status/{status}")
    AppointmentDto updateAppointmentStatus(@PathVariable("id") Long id, @PathVariable("status") AppointmentDto.Status status);
}
