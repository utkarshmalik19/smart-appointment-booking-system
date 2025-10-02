package com.nagarro.doctor_service.controller;

import com.nagarro.doctor_service.model.AppointmentDto;
import com.nagarro.doctor_service.model.Doctor;
import com.nagarro.doctor_service.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<Page<Doctor>> getAllDoctors(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(doctorService.getAllDoctors(page,size));
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
    public ResponseEntity<Page<AppointmentDto>> getAppointments(@PathVariable Long id, @RequestHeader("Authorization") String authHeader, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(doctorService.getDoctorAppointments(id,authHeader,page,size));
    }
    @GetMapping("/{id}/appointments/pending")
    public ResponseEntity<Page<AppointmentDto>> getPendingAppointments(@PathVariable Long id, @RequestHeader("Authorization") String authHeader, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(doctorService.getPendingAppointments(id,authHeader,page,size));
    }
    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(@PathVariable Long appointmentId, @RequestParam AppointmentDto.Status status){
        return ResponseEntity.ok(doctorService.updateAppointmentStatus(appointmentId, status));
    }
}
