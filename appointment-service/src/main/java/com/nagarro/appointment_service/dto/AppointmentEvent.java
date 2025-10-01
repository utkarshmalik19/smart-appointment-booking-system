package com.nagarro.appointment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AppointmentEvent implements Serializable {
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String appointmentTime;
    private String patientEmail;
}
