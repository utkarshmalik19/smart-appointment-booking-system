package com.nagarro.admin_service.service;

import com.nagarro.admin_service.model.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService {
    SignupResponseDto createDoctor(SignupRequestDto request);
    Page<DoctorDto> getAllDoctors(int page, int size);
    DoctorDto getDoctorById(Long id);
    void deleteDoctor(Long id);
    Page<PatientDto> getAllPatients(int page, int size);
    PatientDto getPatientById(Long id);
    AppointmentDto getAppointmentById(Long id);
    Page<AppointmentDto> getAppointmentsByPatientId(Long id, int page, int size);
    Page<AppointmentDto> getAppointmentsByDoctorId(Long id, int page, int size);
    void deleteAppointment(Long id);
}
