package com.nagarro.patient_service.model;

import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long id;
    private Long doctorId;
    private Long patientId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus appointmentStatus;

    public enum AppointmentStatus{
        PENDING,
        APPROVED,
        CANCELLED
    }
}
