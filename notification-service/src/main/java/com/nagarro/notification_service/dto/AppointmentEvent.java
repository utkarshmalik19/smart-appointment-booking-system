package com.nagarro.notification_service.dto;

import lombok.Data;

@Data
public class AppointmentEvent {
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String appointmentTime;
    private String patientEmail;
}
