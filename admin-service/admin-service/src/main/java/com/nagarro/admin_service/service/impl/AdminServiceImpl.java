package com.nagarro.admin_service.service.impl;

import com.nagarro.admin_service.exception.InvalidInputException;
import com.nagarro.admin_service.exception.NotFoundException;
import com.nagarro.admin_service.model.*;
import com.nagarro.admin_service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        if(request.getUsername() == null || request.getPassword() == null){
            throw new InvalidInputException("Username or id should not be blank");
        }
        return authClient.signupDoctor(request);
    }

    @Override
    public Page<DoctorDto> getAllDoctors(int page, int size) {
        return doctorClient.getAllDoctors(page,size);
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
    public Page<PatientDto> getAllPatients(int page, int size) {
        return patientClient.getAllPatients(page,size);
    }

    @Override
    public PatientDto getPatientById(Long id) {
        return patientClient.getPatientById(id);
    }

    @Override
    public AppointmentDto getAppointmentById(Long id) {
        if(appointmentClient.getAppointmentById(id)==null){
            throw new NotFoundException("Appointment doesn't exists");
        }
        return appointmentClient.getAppointmentById(id);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByPatientId(Long id, int page, int size) {
        if(appointmentClient.getAppointmentByPatientId(id,page,size)==null){
            throw new NotFoundException("Appointment doesn't exists");
        }
        return appointmentClient.getAppointmentByPatientId(id,page,size);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByDoctorId(Long id, int page, int size) {
        if(appointmentClient.getAppointmentByDoctorId(id,page,size)==null){
            throw new NotFoundException("Appointment doesn't exists");
        }
        return appointmentClient.getAppointmentByDoctorId(id,page,size);
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentClient.deleteAppointment(id);
    }
}
