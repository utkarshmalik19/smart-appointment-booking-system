package com.nagarro.admin_service.controller;

import com.nagarro.admin_service.model.*;
import com.nagarro.admin_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // Doctor APIs
    @PostMapping("/doctors/create")
    public ResponseEntity<SignupResponseDto> createDoctor(@RequestBody SignupRequestDto request) {
        return ResponseEntity.ok(adminService.createDoctor(request));
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getDoctorById(id));
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        adminService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // Patient APIs
    @GetMapping("/patients")
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        return ResponseEntity.ok(adminService.getAllPatients());
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getPatientById(id));
    }

    // Appointment APIs
    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAppointmentById(id));
    }

    @GetMapping("/appointments/patient/{patientId}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(adminService.getAppointmentsByPatientId(patientId));
    }

    @GetMapping("/appointments/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(adminService.getAppointmentsByDoctorId(doctorId));
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        adminService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
