package com.nagarro.appointment_service.service;

import com.nagarro.appointment_service.model.Appointment;
import com.nagarro.appointment_service.model.AppointmentStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(Appointment appointment);
    Appointment cancelAppointment(Long id);
    Appointment getAppointmentById(Long id);
    Page<Appointment> getAppointmentsForDoctor(Long doctorId, int page, int size);
    void validateDoctorAvailability(Appointment appointment);
    Page<Appointment> getPendingAppointments(Long doctorId, int page, int size);
    Page<Appointment> getAppointmentsForPatient(Long patientId, int page, int size);
    Appointment updateAppointmentStatus(Long id, AppointmentStatus status);
    void deleteAppointment(Long id);

}