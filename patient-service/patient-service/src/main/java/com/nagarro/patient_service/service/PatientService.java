package com.nagarro.patient_service.service;

import com.nagarro.patient_service.model.AppointmentDto;
import com.nagarro.patient_service.model.Patient;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientService {
    Page<Patient> getAllPatients(int page, int size);
    Patient getPatientById(Long id, String authHeader);
    Patient updatePatientDetails(Patient patient, String jwtToken);
    AppointmentDto bookAppointment(Long patientId, AppointmentDto appointmentDto);
    Page<AppointmentDto> getAppointments(Long patientId, String authHeader, int page, int size);
    AppointmentDto cancelAppointment(Long appointmentId, String authHeader);
}
