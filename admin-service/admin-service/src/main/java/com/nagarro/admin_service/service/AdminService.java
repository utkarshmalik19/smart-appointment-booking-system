package com.nagarro.admin_service.service;

import com.nagarro.admin_service.model.*;

import java.util.List;

public interface AdminService {
    SignupResponseDto createDoctor(SignupRequestDto request);
    List<DoctorDto> getAllDoctors();
    DoctorDto getDoctorById(Long id);
    void deleteDoctor(Long id);
    List<PatientDto> getAllPatients();
    PatientDto getPatientById(Long id);
    AppointmentDto getAppointmentById(Long id);
    List<AppointmentDto> getAppointmentsByPatientId(Long id);
    List<AppointmentDto> getAppointmentsByDoctorId(Long id);
    void deleteAppointment(Long id);
}
