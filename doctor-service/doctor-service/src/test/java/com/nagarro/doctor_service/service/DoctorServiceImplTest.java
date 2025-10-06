package com.nagarro.doctor_service.service;

import com.nagarro.doctor_service.exception.NotFoundException;
import com.nagarro.doctor_service.exception.UnauthorizedException;
import com.nagarro.doctor_service.model.AppointmentDto;
import com.nagarro.doctor_service.model.Doctor;
import com.nagarro.doctor_service.model.Role;
import com.nagarro.doctor_service.model.UserDto;
import com.nagarro.doctor_service.repository.DoctorRepository;
import com.nagarro.doctor_service.service.impl.DoctorServiceImpl;
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

class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentClient appointmentClient;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctor;
    private UserDto user;
    private UserDto loggedInUser;
    private Role roleDoctor;
    private Role roleAdmin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        roleDoctor = new Role(2L, "ROLE_DOCTOR");
        roleAdmin = new Role(3L, "ROLE_ADMIN");

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUserId(10L);
        doctor.setSpeciality("Cardiology");

        user = new UserDto(10L, "drsmith", "password",roleDoctor);
        loggedInUser = new UserDto(10L, "drsmith", "password",roleDoctor);
    }

    @Test
    void testGetAllDoctors() {
        Page<Doctor> page = new PageImpl<>(List.of(doctor));
        when(doctorRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Doctor> result = doctorService.getAllDoctors(0, 5);

        assertEquals(1, result.getTotalElements());
        verify(doctorRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetDoctorById_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(authClient.getUserById(eq(10L), anyString())).thenReturn(user);
        when(authClient.getCurrentUser(anyString())).thenReturn(loggedInUser);

        Doctor result = doctorService.getDoctorById(1L, "Bearer token");

        assertNotNull(result);
        assertEquals("Cardiology", result.getSpeciality());
    }

    @Test
    void testGetDoctorById_NotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> doctorService.getDoctorById(1L, "token"));
    }

    @Test
    void testGetDoctorById_UnauthorizedUser() {
        loggedInUser.setId(99L);
        loggedInUser.setRole(roleDoctor);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(authClient.getUserById(eq(10L), anyString())).thenReturn(user);
        when(authClient.getCurrentUser(anyString())).thenReturn(loggedInUser);

        assertThrows(UnauthorizedException.class, () -> doctorService.getDoctorById(1L, "token"));
    }

    @Test
    void testDeleteDoctor() {
        doctorService.deleteDoctor(1L);
        verify(doctorRepository).deleteById(1L);
    }

    @Test
    void testUpdateAppointmentStatus() {
        AppointmentDto appointment = new AppointmentDto();
        appointment.setStatus(AppointmentDto.Status.APPROVED);

        when(appointmentClient.updateAppointmentStatus(1L, AppointmentDto.Status.APPROVED))
                .thenReturn(appointment);

        AppointmentDto result = doctorService.updateAppointmentStatus(1L, AppointmentDto.Status.APPROVED);

        assertEquals(AppointmentDto.Status.APPROVED, result.getStatus());
        verify(appointmentClient).updateAppointmentStatus(1L, AppointmentDto.Status.APPROVED);
    }

    @Test
    void testGetDoctorAppointments_Success() {
        Page<AppointmentDto> appointments = new PageImpl<>(List.of(new AppointmentDto()));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(authClient.getUserById(eq(10L), anyString())).thenReturn(user);
        when(authClient.getCurrentUser(anyString())).thenReturn(loggedInUser);
        when(appointmentClient.getAppointmentsByDoctor(1L, 0, 5)).thenReturn(appointments);

        Page<AppointmentDto> result = doctorService.getDoctorAppointments(1L, "token", 0, 5);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetPendingAppointments_Success() {
        Page<AppointmentDto> appointments = new PageImpl<>(List.of(new AppointmentDto()));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(authClient.getUserById(eq(10L), anyString())).thenReturn(user);
        when(authClient.getCurrentUser(anyString())).thenReturn(loggedInUser);
        when(appointmentClient.getPendingAppointments(1L, 0, 5)).thenReturn(appointments);

        Page<AppointmentDto> result = doctorService.getPendingAppointments(1L, "token", 0, 5);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testUpdateDoctorDetails_Success() {
        when(authClient.getUserById(eq(10L), anyString())).thenReturn(user);
        when(authClient.getCurrentUser(anyString())).thenReturn(loggedInUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        Doctor result = doctorService.updateDoctorDetails(doctor, "authHeader");

        assertNotNull(result);
        verify(doctorRepository).save(doctor);
    }

    @Test
    void testUpdateDoctorDetails_Unauthorized() {
        loggedInUser.setId(99L);
        loggedInUser.setRole(roleDoctor);

        when(authClient.getUserById(eq(10L), anyString())).thenReturn(user);
        when(authClient.getCurrentUser(anyString())).thenReturn(loggedInUser);

        assertThrows(UnauthorizedException.class,
                () -> doctorService.updateDoctorDetails(doctor, "token"));
    }
}