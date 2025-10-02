package com.nagarro.patient_service.controller;

import com.nagarro.patient_service.model.AppointmentDto;
import com.nagarro.patient_service.model.Patient;
import com.nagarro.patient_service.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;


    @PostMapping("/update")
    public ResponseEntity<Patient> updatePatientDetails(@RequestBody Patient patient, @RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(patientService.updatePatientDetails(patient,authHeader));
    }
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id, @RequestHeader("Authorization" )String authHeader) {
        return ResponseEntity.ok(patientService.getPatientById(id,authHeader));
    }
    @PostMapping("/{id}/appointments")
    public ResponseEntity<AppointmentDto> bookAppointment(@PathVariable Long id, @RequestBody AppointmentDto appointmentDto){
        return ResponseEntity.ok(patientService.bookAppointment(id,appointmentDto));
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentDto>> getAppointments(@PathVariable Long id,@RequestHeader("Authorization" )String authHeader){
        return ResponseEntity.ok(patientService.getAppointments(id,authHeader));
    }

    @PutMapping("/appointment/{appointmentId}/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(@PathVariable Long appointmentId,@RequestHeader("Authorization" )String authHeader){
        return ResponseEntity.ok(patientService.cancelAppointment(appointmentId,authHeader));
    }


}
