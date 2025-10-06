package com.nagarro.admin_service.service;

import com.nagarro.admin_service.exception.InvalidInputException;
import com.nagarro.admin_service.exception.NotFoundException;
import com.nagarro.admin_service.model.*;
import com.nagarro.admin_service.service.*;
import com.nagarro.admin_service.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {
    @Mock
    private DoctorClient doctorClient;

    @Mock
    private PatientClient patientClient;

    @Mock
    private AppointmentClient appointmentClient;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test: Invalid doctor creation throws exception
    @Test
    void createDoctor_ShouldThrowException_WhenUsernameOrPasswordMissing() {
        SignupRequestDto request = new SignupRequestDto();
        request.setUsername(null);
        request.setPassword(null);

        assertThrows(InvalidInputException.class, () -> adminService.createDoctor(request));
        verifyNoInteractions(authClient);
    }

    // Test: Successful doctor creation
    @Test
    void createDoctor_ShouldReturnResponse_WhenValidInput() {
        SignupRequestDto request = new SignupRequestDto();
        request.setUsername("doctor1");
        request.setPassword("password");

        SignupResponseDto response = new SignupResponseDto(1L, "doctor1");

        when(authClient.signupDoctor(request)).thenReturn(response);

        SignupResponseDto result = adminService.createDoctor(request);

        assertEquals(1L, result.getId());
        assertEquals("doctor1", result.getUsername());
        verify(authClient, times(1)).signupDoctor(request);
    }

    // Test: Fetch all doctors (paginated)
    @Test
    void getAllDoctors_ShouldReturnPagedDoctors() {
        DoctorDto doctor = new DoctorDto();
        doctor.setId(1L);
        doctor.setSpeciality("Cardiology");
        doctor.setPhone("1234567890");
        doctor.setYearsOfExperience(10);

        Page<DoctorDto> page = new PageImpl<>(List.of(doctor));
        when(doctorClient.getAllDoctors(0, 5)).thenReturn(page);

        Page<DoctorDto> result = adminService.getAllDoctors(0, 5);

        assertEquals(1, result.getContent().size());
        assertEquals("Cardiology", result.getContent().get(0).getSpeciality());
        verify(doctorClient, times(1)).getAllDoctors(0, 5);
    }

    // Test: Get doctor by ID
    @Test
    void getDoctorById_ShouldReturnDoctor() {
        DoctorDto doctor = new DoctorDto();
        doctor.setId(2L);
        doctor.setSpeciality("Dermatology");

        when(doctorClient.getDoctorById(2L)).thenReturn(doctor);

        DoctorDto result = adminService.getDoctorById(2L);

        assertEquals("Dermatology", result.getSpeciality());
        verify(doctorClient, times(1)).getDoctorById(2L);
    }

    // Test: Appointment not found
    @Test
    void getAppointmentById_ShouldThrowNotFound_WhenNullReturned() {
        when(appointmentClient.getAppointmentById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> adminService.getAppointmentById(1L));
    }

    // Test: Delete doctor
    @Test
    void deleteDoctor_ShouldInvokeClient() {
        doNothing().when(doctorClient).deleteDoctor(5L);

        adminService.deleteDoctor(5L);

        verify(doctorClient, times(1)).deleteDoctor(5L);
    }
}