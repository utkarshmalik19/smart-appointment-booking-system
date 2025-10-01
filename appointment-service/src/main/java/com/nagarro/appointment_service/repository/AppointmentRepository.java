package com.nagarro.appointment_service.repository;

import com.nagarro.appointment_service.model.Appointment;
import com.nagarro.appointment_service.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Appointment a " +
            "WHERE a.doctorId = :doctorId " +
            "AND a.status <> 'CANCELLED' " +
            "AND a.startTime < :endTime " +
            "AND a.endTime > :startTime")
    boolean existsByDoctorIdAndTimeOverlap(Long doctorId,
                                           LocalDateTime startTime,
                                           LocalDateTime endTime);

    // Robust overlap for patient
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Appointment a " +
            "WHERE a.patientId = :patientId " +
            "AND a.status <> 'CANCELLED' " +
            "AND a.startTime < :endTime " +
            "AND a.endTime > :startTime")
    boolean existsByPatientIdAndTimeOverlap(Long patientId,
                                            LocalDateTime startTime,
                                            LocalDateTime endTime);


}