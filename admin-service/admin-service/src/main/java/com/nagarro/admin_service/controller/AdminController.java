package com.nagarro.admin_service.controller;

import com.nagarro.admin_service.model.*;
import com.nagarro.admin_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<DoctorDto>> getAllDoctors(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllDoctors(page,size));
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getDoctorById(id));
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) {
        adminService.deleteDoctor(id);
        return ResponseEntity.ok("Doctor deleted");
    }

    // Patient APIs
    @GetMapping("/patients")
    public ResponseEntity<Page<PatientDto>> getAllPatients(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllPatients(page, size));
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
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByPatientId(@PathVariable Long patientId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAppointmentsByPatientId(patientId,page,size));
    }

    @GetMapping("/appointments/doctor/{doctorId}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByDoctorId(@PathVariable Long doctorId,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAppointmentsByDoctorId(doctorId,page,size));
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        adminService.deleteAppointment(id);
        return ResponseEntity.ok("Appointment deleted");
    }
}
