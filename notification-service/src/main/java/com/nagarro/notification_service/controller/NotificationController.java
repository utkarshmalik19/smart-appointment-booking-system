package com.nagarro.notification_service.controller;

import com.nagarro.notification_service.dto.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final JavaMailSender mailSender;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody AppointmentEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("utkarshmalik19@gmail.com");
        message.setTo(event.getPatientEmail());
        message.setSubject("Appointment Confirmation");
        message.setText("Dear patient, your appointment is confirmed.");
        mailSender.send(message);

        return ResponseEntity.ok("Mail sent to " + event.getPatientEmail());
    }
}