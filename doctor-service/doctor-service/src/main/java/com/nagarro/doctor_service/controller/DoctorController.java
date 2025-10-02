package com.nagarro.doctor_service.controller;

import com.nagarro.doctor_service.model.AppointmentDto;
import com.nagarro.doctor_service.model.Doctor;
import com.nagarro.doctor_service.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id,@RequestHeader("Authorization" )String authHeader) {
        return ResponseEntity.ok(doctorService.getDoctorById(id,authHeader));
    }
    @PostMapping("/update")
    public ResponseEntity<Doctor> updateDoctorDetails(@RequestBody Doctor doctor, @RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(doctorService.updateDoctorDetails(doctor,authHeader));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentDto>> getAppointments(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(doctorService.getDoctorAppointments(id,authHeader));
    }
    @GetMapping("/{id}/appointments/pending")
    public ResponseEntity<List<AppointmentDto>> getPendingAppointments(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(doctorService.getPendingAppointments(id,authHeader));
    }
    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(@PathVariable Long appointmentId, @RequestParam AppointmentDto.Status status){
        return ResponseEntity.ok(doctorService.updateAppointmentStatus(appointmentId, status));
    }
}
