package com.nagarro.notification_service.service;

import com.nagarro.notification_service.config.RabbitMQConfig;
import com.nagarro.notification_service.dto.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleAppointmentEvent(AppointmentEvent event){
      //  sendConfirmationMail(event);
    }

    private void sendConfirmationMail(AppointmentEvent event){
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom("noreply@smartappointments.com"); // required for Mailtrap (can be any email-like string)
//            message.setTo(event.getPatientEmail());
//            message.setSubject("Appointment Confirmation");
//            message.setText(String.format(
//                    "Dear patient, your appointment (ID %d) with Doctor %d is confirmed for %s.\n\nRegards, \nABC Hospital",
//                    event.getAppointmentId(), event.getDoctorId(), event.getAppointmentTime()
//            ));
//            mailSender.send(message);
//            System.out.println("Confirmation email sent to " + event.getPatientEmail());
//        } catch (Exception e) {
//            System.err.println("Failed to send email: " + e.getMessage());
//        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("utkarshmalik19@gmail.com"); // optional
        message.setTo(event.getPatientEmail());
        message.setSubject("Appointment Confirmation");
        message.setText(String.format(
                "Dear patient, your appointment  confirmed.\n\nRegards, \nABC Hospital",
                event.getAppointmentId(), event.getDoctorId(), event.getAppointmentTime()
        ));
        mailSender.send(message);
        System.out.println("Mail sent successfully");
    }
}
