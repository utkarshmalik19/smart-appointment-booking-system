package com.nagarro.doctor_service.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;

    public enum Status{
            PENDING,
            APPROVED,
            CANCELLED
}

}
