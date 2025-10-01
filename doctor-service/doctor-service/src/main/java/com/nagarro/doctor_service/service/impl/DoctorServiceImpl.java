package com.nagarro.doctor_service.service.impl;

import com.nagarro.doctor_service.model.AppointmentDto;
import com.nagarro.doctor_service.model.Doctor;
import com.nagarro.doctor_service.model.UserDto;
import com.nagarro.doctor_service.repository.DoctorRepository;
import com.nagarro.doctor_service.service.AppointmentClient;
import com.nagarro.doctor_service.service.AuthClient;
import com.nagarro.doctor_service.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentClient appointmentClient;
    private final AuthClient authClient;


    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Doctor not found"));
    }

    @Override
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    @Override
    public AppointmentDto updateAppointmentStatus(Long appointmentId, AppointmentDto.Status status) {
        return appointmentClient.updateAppointmentStatus(appointmentId,status);
    }

    @Override
    public List<AppointmentDto> getDoctorAppointments(Long doctorId) {
        return appointmentClient.getAppointmentsByDoctor(doctorId);
    }

    @Override
    public List<AppointmentDto> getPendingAppointments(Long doctorId) {
        return appointmentClient.getPendingAppointments(doctorId);
    }

    @Override
    public Doctor updateDoctorDetails(Doctor doctor, String jwtToken) {
        UserDto user = authClient.getUserById(doctor.getUserId(),jwtToken);
        if (user == null || !user.getRole().getRoleName().equals("ROLE_DOCTOR")) {
            throw new IllegalArgumentException("Invalid userId or role mismatch");
        }
        return doctorRepository.save(doctor);
    }
}
