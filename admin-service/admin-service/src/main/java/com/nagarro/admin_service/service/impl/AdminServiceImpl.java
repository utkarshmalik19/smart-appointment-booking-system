package com.nagarro.admin_service.service.impl;

import com.nagarro.admin_service.model.*;
import com.nagarro.admin_service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final DoctorClient doctorClient;
    private final PatientClient patientClient;
    private final AppointmentClient appointmentClient;
    private final AuthClient authClient;

    @Override
    public SignupResponseDto createDoctor(SignupRequestDto request) {
        return authClient.signupDoctor(request);
    }

    @Override
    public List<DoctorDto> getAllDoctors() {
        return doctorClient.getAllDoctors();
    }

    @Override
    public DoctorDto getDoctorById(Long id) {
        return doctorClient.getDoctorById(id);
    }

    @Override
    public void deleteDoctor(Long id) {
        doctorClient.deleteDoctor(id);
    }

    @Override
    public List<PatientDto> getAllPatients() {
        return patientClient.getAllPatients();
    }

    @Override
    public PatientDto getPatientById(Long id) {
        return patientClient.getPatientById(id);
    }

    @Override
    public AppointmentDto getAppointmentById(Long id) {
        return appointmentClient.getAppointmentById(id);
    }

    @Override
    public List<AppointmentDto> getAppointmentsByPatientId(Long id) {
        return appointmentClient.getAppointmentByPatientId(id);
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDoctorId(Long id) {
        return appointmentClient.getAppointmentByDoctorId(id);
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentClient.deleteAppointment(id);
    }
}
