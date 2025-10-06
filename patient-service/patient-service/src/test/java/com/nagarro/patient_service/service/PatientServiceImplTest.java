package com.nagarro.patient_service.service;

import com.nagarro.patient_service.exception.NotFoundException;
import com.nagarro.patient_service.exception.UnauthorizedException;
import com.nagarro.patient_service.model.AppointmentDto;
import com.nagarro.patient_service.model.Patient;
import com.nagarro.patient_service.model.Role;
import com.nagarro.patient_service.model.UserDto;
import com.nagarro.patient_service.repository.PatientRepository;
import com.nagarro.patient_service.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentClient appointmentClient;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private UserDto user;
    private UserDto loggedInUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role patientRole = new Role(2L, "ROLE_PATIENT");
        patient = new Patient();
        patient.setId(1L);
        patient.setUserId(10L);
        patient.setName("John Doe");

        user = new UserDto(10L, "johndoe", "password",patientRole);
        loggedInUser = new UserDto(10L, "johndoe", "password", patientRole);
    }

    @Test
    void testGetAllPatients() {
        Page<Patient> page = new PageImpl<>(List.of(patient));
        when(patientRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Patient> result = patientService.getAllPatients(0, 5);
        assertEquals(1, result.getTotalElements());
        verify(patientRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetPatientById_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(authClient.getUserById(eq(10L), anyString())).thenReturn(user);
        when(authClient.getCurrentUser(anyString())).thenReturn(loggedInUser);

        Patient result = patientService.getPatientById(1L, "Bearer token");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testGetPatientById_PatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> patientService.getPatientById(1L, "token"));
    }

    @Test
    void testGetPatientById_UnauthorizedUser() {
        loggedInUser.setId(99L); // another user
        loggedInUser.setRole(new Role(2L, "ROLE_PATIENT"));

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(authClient.getUserById(eq(10L), anyString())).thenReturn(user);
        when(authClient.getCurrentUser(anyString())).thenReturn(loggedInUser);

        assertThrows(UnauthorizedException.class, () -> patientService.getPatientById(1L, "token"));
    }

    @Test
    void testUpdatePatientDetails_NewPatient() {
        when(authClient.getUserById(eq(10L), anyString())).thenReturn(user);
        when(patientRepository.findByUserId(10L)).thenReturn(null);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient updated = patientService.updatePatientDetails(patient, "jwtToken");

        assertEquals("John Doe", updated.getName());
        verify(patientRepository, times(2)).save(any(Patient.class)); // once for creation, once for return
    }

    @Test
    void testBookAppointment() {
        AppointmentDto appointment = new AppointmentDto();
        appointment.setDoctorId(2L);

        when(appointmentClient.createAppointment(any(AppointmentDto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentDto result = patientService.bookAppointment(1L, appointment);

        assertEquals(1L, result.getPatientId());
        verify(appointmentClient).createAppointment(any(AppointmentDto.class));
    }

    @Test
    void testCancelAppointment() {
        AppointmentDto appointment = new AppointmentDto();
        appointment.setAppointmentStatus(AppointmentDto.AppointmentStatus.CANCELLED);

        when(appointmentClient.getAppointmentById(1L)).thenReturn(appointment);
        when(appointmentClient.cancelAppointment(1L)).thenReturn(appointment);

        AppointmentDto result = patientService.cancelAppointment(1L, "token");

        assertEquals(AppointmentDto.AppointmentStatus.CANCELLED, result.getAppointmentStatus());
        verify(appointmentClient).cancelAppointment(1L);
    }
}