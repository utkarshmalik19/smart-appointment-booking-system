package com.nagarro.doctor_service.service;

import com.nagarro.doctor_service.model.AppointmentDto;
import com.nagarro.doctor_service.model.Doctor;

import java.util.List;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(Long id, String authHeader);
    void deleteDoctor(Long id);
    AppointmentDto updateAppointmentStatus(Long appointmentId, AppointmentDto.Status status);
    List<AppointmentDto> getDoctorAppointments(Long doctorId, String authHeader);
    List<AppointmentDto> getPendingAppointments(Long doctorId, String authHeader);
    Doctor updateDoctorDetails(Doctor doctor, String jwtToken);

}
