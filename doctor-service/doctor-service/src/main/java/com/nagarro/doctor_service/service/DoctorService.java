package com.nagarro.doctor_service.service;

import com.nagarro.doctor_service.model.AppointmentDto;
import com.nagarro.doctor_service.model.Doctor;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DoctorService {
    Page<Doctor> getAllDoctors(int page, int size);
    Doctor getDoctorById(Long id, String authHeader);
    void deleteDoctor(Long id);
    AppointmentDto updateAppointmentStatus(Long appointmentId, AppointmentDto.Status status);
    Page<AppointmentDto> getDoctorAppointments(Long doctorId, String authHeader, int page, int size);
    Page<AppointmentDto> getPendingAppointments(Long doctorId, String authHeader, int page, int size);
    Doctor updateDoctorDetails(Doctor doctor, String jwtToken);

}
