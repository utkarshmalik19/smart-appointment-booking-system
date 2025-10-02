package com.nagarro.appointment_service.service.impl;

import com.nagarro.appointment_service.config.RabbitMQConfig;
import com.nagarro.appointment_service.dto.AppointmentEvent;
import com.nagarro.appointment_service.exception.AppointmentBookingException;
import com.nagarro.appointment_service.exception.NotFoundException;
import com.nagarro.appointment_service.model.Appointment;
import com.nagarro.appointment_service.model.AppointmentStatus;
import com.nagarro.appointment_service.repository.AppointmentRepository;
import com.nagarro.appointment_service.service.AppointmentService;
import com.nagarro.appointment_service.service.NotificationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final LocalTime START_HOUR = LocalTime.of(9,0);
    private final LocalTime END_HOUR = LocalTime.of(17,0);
    private final NotificationClient notificationClient;
    @Override
    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        // NULL CHECKS
        if (appointment.getDoctorId() == null || appointment.getPatientId() == null) {
            throw new AppointmentBookingException("DoctorId and PatientId are required");
        }
        if (appointment.getStartTime() == null || appointment.getEndTime() == null) {
            throw new AppointmentBookingException("Start and End time are required");
        }

        // PAST DATE CHECK
        if (appointment.getStartTime().isBefore(java.time.LocalDateTime.now())) {
            throw new AppointmentBookingException("Cannot book an appointment in the past");
        }

        // TIME ORDER VALIDATION
        if (!appointment.getEndTime().isAfter(appointment.getStartTime())) {
            throw new AppointmentBookingException("End time must be after start time");
        }

        // DURATION VALIDATION (15 mins – 2 hours)
        long minutes = java.time.Duration.between(
                appointment.getStartTime(),
                appointment.getEndTime()
        ).toMinutes();
        if (minutes < 15) {
            throw new AppointmentBookingException("Appointment must be at least 15 minutes long");
        }
        if (minutes > 120) {
            throw new AppointmentBookingException("Appointment cannot exceed 2 hours");
        }
        validateDoctorAvailability(appointment);
       boolean conflict = appointmentRepository.existsByDoctorIdAndTimeOverlap(appointment.getDoctorId(), appointment.getStartTime(),appointment.getEndTime());

       if (conflict){
            throw new AppointmentBookingException("Appointment time not available");
       }
        // PATIENT OVERLAP VALIDATION
        boolean patientConflict = appointmentRepository.existsByPatientIdAndTimeOverlap(
                appointment.getPatientId(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );
        if (patientConflict) {
            throw new AppointmentBookingException("You already have an appointment in the selected time slot");
        }
        Appointment savedAppointment = appointmentRepository.save(appointment);
String email = "firipa1069@rograc.com";

        //BUILD AND PUBLISH EVENT
        AppointmentEvent event = new AppointmentEvent(
                savedAppointment.getId(),
                savedAppointment.getPatientId(),
                savedAppointment.getDoctorId(),
                savedAppointment.getStartTime().toString(),
                email
        );
        notificationClient.sendNotification(event);
        // Publish event
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
        System.out.println("Published appointment "+ event);
        return savedAppointment;
    }

    @Override
    @Transactional
    public Appointment cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Appointment does not exists"));
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppointmentBookingException("Appointment is already cancelled");
        }
        if (appointment.getEndTime().isBefore(LocalDateTime.now())) {
            throw new AppointmentBookingException("Cannot cancel an appointment that has already ended");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppointmentBookingException("Cannot cancel a completed appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);

    }

    @Override
    public Appointment getAppointmentById(Long id) {
        if (id == null) {
            throw new AppointmentBookingException("Appointment ID is required");
        }
        return appointmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Appointment does not exists"));
    }

    @Override
    public Page<Appointment> getAppointmentsForDoctor(Long doctorId, int page, int size) {
        if (doctorId == null) {
            throw new AppointmentBookingException("Doctor ID is required");
        }
        Pageable pageable = PageRequest.of(page,size);
        return appointmentRepository.findByDoctorId(doctorId,pageable);
    }

    @Override
    public void validateDoctorAvailability(Appointment appointment) {
        LocalTime start = appointment.getStartTime().toLocalTime();
        LocalTime end = appointment.getEndTime().toLocalTime();

        if (start.isBefore(START_HOUR) || end.isAfter(END_HOUR)) {
            throw new AppointmentBookingException("Appointment must be within working hours (09:00 - 17:00)");
        }
    }

    @Override
    public Page<Appointment> getPendingAppointments(Long doctorId, int page, int size) {
        if (doctorId == null) {
            throw new AppointmentBookingException("Doctor ID is required");
        }
        Pageable pageable = PageRequest.of(page,size);
        return appointmentRepository.findByDoctorIdAndStatus(doctorId,AppointmentStatus.PENDING, pageable);

    }

    @Override
    public Page<Appointment> getAppointmentsForPatient(Long patientId, int page, int size) {
        if (patientId == null) {
            throw new AppointmentBookingException("Patient ID is required");
        }
        Pageable pageable = PageRequest.of(page,size);
        return appointmentRepository.findByPatientId(patientId,pageable);
    }

    @Override
    @Transactional
    public Appointment updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
        appointment.setStatus(status);
        return appointment;
    }


    @Override
    public void deleteAppointment(Long id) {
        if(appointmentRepository.findById(id) == null){
            throw new NotFoundException("Appointment doesnt not exist");
        }
        appointmentRepository.deleteById(id);
    }
}
