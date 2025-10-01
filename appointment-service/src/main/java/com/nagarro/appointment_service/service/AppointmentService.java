package com.nagarro.appointment_service.service;

import com.nagarro.appointment_service.model.Appointment;
import com.nagarro.appointment_service.model.AppointmentStatus;

import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(Appointment appointment);
    Appointment cancelAppointment(Long id);
    Appointment getAppointmentById(Long id);
    List<Appointment> getAppointmentsForDoctor(Long doctorId);
    void validateDoctorAvailability(Appointment appointment);
    List<Appointment> getPendingAppointments(Long doctorId);
    List<Appointment> getAppointmentsForPatient(Long patientId);
    Appointment updateAppointmentStatus(Long id, AppointmentStatus status);
    void deleteAppointment(Long id);

}