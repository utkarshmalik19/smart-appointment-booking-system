package com.nagarro.patient_service.service.impl;

import com.nagarro.patient_service.exception.NotFoundException;
import com.nagarro.patient_service.exception.UnauthorizedException;
import com.nagarro.patient_service.model.AppointmentDto;
import com.nagarro.patient_service.model.Patient;
import com.nagarro.patient_service.model.UserDto;
import com.nagarro.patient_service.repository.PatientRepository;
import com.nagarro.patient_service.service.AppointmentClient;
import com.nagarro.patient_service.service.AuthClient;
import com.nagarro.patient_service.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentClient appointmentClient;
    private final AuthClient authClient;


    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getPatientById(Long id, String authHeader) {
        Patient patient = patientRepository.findById(id).orElseThrow(()-> new NotFoundException("Patient not found"));
        UserDto user = authClient.getUserById(patient.getUserId(), authHeader);
        UserDto loggedInUser = authClient.getUserFromToken(authHeader);
        if (!loggedInUser.getId().equals(patient.getUserId())) {
            throw new UnauthorizedException("You can only access your own details");
        }
        if (user == null || !user.getRole().getRoleName().equals("ROLE_PATIENT")) {
            throw new UnauthorizedException("Unauthorized or role mismatch");
        }
        // Check if requested patient ID matches the JWT user ID
        if (!user.getId().equals(patient.getUserId())) {
            throw new IllegalArgumentException("You are not authorized to view this patient details");
        }
        return patient;
    }

    @Override
    public Patient updatePatientDetails(Patient patient, String jwtToken) {
        UserDto user = authClient.getUserById(patient.getUserId(),jwtToken);
        Patient updatedPatient = patientRepository.findByUserId(user.getId());
        if(updatedPatient == null) {
           updatedPatient = patientRepository.save(patient);
        }else {
            updatedPatient.setAge(patient.getAge());
            updatedPatient.setGender(patient.getGender());
            updatedPatient.setName(patient.getName());
            updatedPatient.setPhone(patient.getPhone());
        }
        if (user == null || !user.getRole().getRoleName().equals("ROLE_PATIENT")) {
            throw new IllegalArgumentException("Invalid userId or role mismatch");
        }
        return patientRepository.save(updatedPatient);
    }

    @Override
    public AppointmentDto bookAppointment(Long patientId, AppointmentDto appointmentDto) {
        appointmentDto.setPatientId(patientId);
        return appointmentClient.createAppointment(appointmentDto);
    }

    @Override
    public List<AppointmentDto> getAppointments(Long patientId) {
        List<AppointmentDto> allAppointments = appointmentClient.getAppointmentByPatientId(patientId);
        //FILTER OUT CANCELLED APPOINTMENTS
        return allAppointments.stream().filter(appointment -> appointment.getAppointmentStatus() != AppointmentDto.AppointmentStatus.CANCELLED)
                .toList();
    }

    @Override
    public AppointmentDto getAppointmentById(Long appointmentId) {
        return appointmentClient.getAppointmentById(appointmentId);
    }

    @Override
    public AppointmentDto cancelAppointment(Long appointmentId) {
        AppointmentDto appointment = appointmentClient.getAppointmentById(appointmentId);
        appointment.setAppointmentStatus(AppointmentDto.AppointmentStatus.CANCELLED);
        return appointmentClient.cancelAppointment(appointmentId);

    }
}
