package com.nagarro.patient_service.service;

import com.nagarro.patient_service.model.AppointmentDto;
import com.nagarro.patient_service.model.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> getAllPatients();
    Patient getPatientById(Long id, String authHeader);
    Patient updatePatientDetails(Patient patient, String jwtToken);
    AppointmentDto bookAppointment(Long patientId, AppointmentDto appointmentDto);
    List<AppointmentDto> getAppointments(Long patientId);
    AppointmentDto getAppointmentById(Long appointmentId);
    AppointmentDto cancelAppointment(Long appointmentId);
}
