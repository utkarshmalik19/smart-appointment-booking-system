package com.nagarro.doctor_service.service;

import com.nagarro.doctor_service.config.FeignConfig;
import com.nagarro.doctor_service.model.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "appointment-service", configuration = FeignConfig.class)
public interface AppointmentClient {
    @GetMapping("/doctor/{doctorId}")
    List<AppointmentDto> getAppointmentsByDoctor(@PathVariable("doctorId") Long doctorId);

    @GetMapping("/doctor/{doctorId}/appointments/pending")
    List<AppointmentDto> getPendingAppointments(@PathVariable("doctorId") Long doctorId);

    @PutMapping("/{id}/status/{status}")
    AppointmentDto updateAppointmentStatus(@PathVariable("id") Long id, @PathVariable("status") AppointmentDto.Status status);
}
