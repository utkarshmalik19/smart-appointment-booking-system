package com.nagarro.appointment_service.service;

import com.nagarro.appointment_service.config.RabbitMQConfig;
import com.nagarro.appointment_service.dto.AppointmentEvent;
import com.nagarro.appointment_service.exception.AppointmentBookingException;
import com.nagarro.appointment_service.exception.NotFoundException;
import com.nagarro.appointment_service.model.Appointment;
import com.nagarro.appointment_service.model.AppointmentStatus;
import com.nagarro.appointment_service.repository.AppointmentRepository;
import com.nagarro.appointment_service.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private NotificationClient notificationClient;
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctorId(10L);
        appointment.setPatientId(20L);
        appointment.setStartTime(LocalDateTime.now().plusHours(1));
        appointment.setEndTime(LocalDateTime.now().plusHours(2));
        appointment.setStatus(AppointmentStatus.PENDING);
    }

    // CREATE APPOINTMENT SUCCESS
    @Test
    void testCreateAppointment_Success() {
        Appointment appointment = new Appointment();
        appointment.setDoctorId(1L);
        appointment.setPatientId(2L);
        // Within working hours 10:00–11:00
        appointment.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
        appointment.setEndTime(appointment.getStartTime().plusMinutes(30));

        when(appointmentRepository.existsByDoctorIdAndTimeOverlap(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndTimeOverlap(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> {
            Appointment saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Appointment result = appointmentService.createAppointment(appointment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(notificationClient, times(1)).sendNotification(any(AppointmentEvent.class));
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(RabbitMQConfig.EXCHANGE), eq(RabbitMQConfig.ROUTING_KEY), any(AppointmentEvent.class));
    }


    // Missing doctor/patient ID
    @Test
    void testCreateAppointment_MissingDoctorId() {
        appointment.setDoctorId(null);
        assertThrows(AppointmentBookingException.class, () -> appointmentService.createAppointment(appointment));
    }

    // Start time before now
    @Test
    void testCreateAppointment_PastDate() {
        appointment.setStartTime(LocalDateTime.now().minusHours(1));
        appointment.setEndTime(LocalDateTime.now().plusHours(1));
        assertThrows(AppointmentBookingException.class, () -> appointmentService.createAppointment(appointment));
    }

    // End before start
    @Test
    void testCreateAppointment_EndBeforeStart() {
        appointment.setEndTime(appointment.getStartTime().minusMinutes(10));
        assertThrows(AppointmentBookingException.class, () -> appointmentService.createAppointment(appointment));
    }

    // Duration less than 15 minutes
    @Test
    void testCreateAppointment_ShortDuration() {
        appointment.setEndTime(appointment.getStartTime().plusMinutes(10));
        assertThrows(AppointmentBookingException.class, () -> appointmentService.createAppointment(appointment));
    }

    // Duration more than 2 hours
    @Test
    void testCreateAppointment_LongDuration() {
        appointment.setEndTime(appointment.getStartTime().plusHours(3));
        assertThrows(AppointmentBookingException.class, () -> appointmentService.createAppointment(appointment));
    }

    // Doctor time conflict
    @Test
    void testCreateAppointment_DoctorConflict() {
        when(appointmentRepository.existsByDoctorIdAndTimeOverlap(anyLong(), any(), any())).thenReturn(true);
        assertThrows(AppointmentBookingException.class, () -> appointmentService.createAppointment(appointment));
    }

    // Cancel appointment success
    @Test
    void testCancelAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.cancelAppointment(1L);

        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    // Cancel already cancelled appointment
    @Test
    void testCancelAppointment_AlreadyCancelled() {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        assertThrows(AppointmentBookingException.class, () -> appointmentService.cancelAppointment(1L));
    }

    // Cancel non-existent appointment
    @Test
    void testCancelAppointment_NotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> appointmentService.cancelAppointment(1L));
    }

    // Get appointment by ID success
    @Test
    void testGetAppointmentById_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        Appointment result = appointmentService.getAppointmentById(1L);
        assertEquals(1L, result.getId());
    }

    // Get appointment by ID null
    @Test
    void testGetAppointmentById_NullId() {
        assertThrows(AppointmentBookingException.class, () -> appointmentService.getAppointmentById(null));
    }

    // Get doctor appointments
    @Test
    void testGetAppointmentsForDoctor_Success() {
        Page<Appointment> page = new PageImpl<>(List.of(appointment));
        when(appointmentRepository.findByDoctorId(eq(10L), any(Pageable.class))).thenReturn(page);

        Page<Appointment> result = appointmentService.getAppointmentsForDoctor(10L, 0, 5);
        assertEquals(1, result.getTotalElements());
    }

    // Validate doctor availability - out of hours
    @Test
    void testValidateDoctorAvailability_OutOfHours() {
        appointment.setStartTime(LocalDateTime.now().withHour(8)); // Before 9
        appointment.setEndTime(LocalDateTime.now().withHour(10));
        assertThrows(AppointmentBookingException.class, () -> appointmentService.validateDoctorAvailability(appointment));
    }

    // Get pending appointments
    @Test
    void testGetPendingAppointments_Success() {
        Page<Appointment> page = new PageImpl<>(List.of(appointment));
        when(appointmentRepository.findByDoctorIdAndStatus(eq(10L), eq(AppointmentStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);

        Page<Appointment> result = appointmentService.getPendingAppointments(10L, 0, 5);
        assertEquals(1, result.getTotalElements());
    }

    // Update appointment status
    @Test
    void testUpdateAppointmentStatus_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        Appointment result = appointmentService.updateAppointmentStatus(1L, AppointmentStatus.COMPLETED);
        assertEquals(AppointmentStatus.COMPLETED, result.getStatus());
    }

    // Delete appointment success
    @Test
    void testDeleteAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        appointmentService.deleteAppointment(1L);
        verify(appointmentRepository).deleteById(1L);
    }

    // Delete appointment not found
    @Test
    void testDeleteAppointment_NotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> appointmentService.deleteAppointment(1L));
    }
}