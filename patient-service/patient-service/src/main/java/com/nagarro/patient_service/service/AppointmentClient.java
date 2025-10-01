package com.nagarro.patient_service.service;

import com.nagarro.patient_service.model.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "appointment-service", path = "/api/appointments")
public interface AppointmentClient {

    @PostMapping("/create")
    AppointmentDto createAppointment(@RequestBody AppointmentDto appointment);

    @GetMapping("/patient/{patientId}")
    List<AppointmentDto> getAppointmentByPatientId(@PathVariable("patientId") Long patientId);

    @GetMapping("/{appointmentId}")
    AppointmentDto getAppointmentById(@PathVariable("appointmentId") Long appointmentId);

    @PutMapping("/{id}/cancel")
    AppointmentDto cancelAppointment(@PathVariable Long id);
}
