package com.nagarro.appointment_service.controller;

import com.nagarro.appointment_service.exception.NotFoundException;
import com.nagarro.appointment_service.model.Appointment;
import com.nagarro.appointment_service.model.AppointmentStatus;
import com.nagarro.appointment_service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/create")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment){
        return ResponseEntity.ok(appointmentService.createAppointment(appointment));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long id){
       return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id){
        Appointment appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getDoctorAppointments(@PathVariable Long doctorId){
        return appointmentService.getAppointmentsForDoctor(doctorId);
    }

    @GetMapping("/doctor/{doctorId}/appointments/pending")
    List<Appointment> getPendingAppointments(@PathVariable("doctorId") Long doctorId){
        return appointmentService.getPendingAppointments(doctorId);
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> getPatientAppointments(@PathVariable Long patientId){
        return appointmentService.getAppointmentsForPatient(patientId);
    }

    @PutMapping("/{id}/status/{status}")
    Appointment updateAppointmentStatus(@PathVariable Long id, @PathVariable AppointmentStatus status){
        return appointmentService.updateAppointmentStatus(id, status);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        if (appointmentService.getAppointmentById(id) == null){
            throw new NotFoundException("Appointment doesn't exist");
        }
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok("Appointment deleted");
    }
}