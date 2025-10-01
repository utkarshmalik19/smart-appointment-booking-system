package com.nagarro.appointment_service.service;

import com.nagarro.appointment_service.config.FeignConfig;
import com.nagarro.appointment_service.dto.AppointmentEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service",configuration = FeignConfig.class)
public interface NotificationClient {

    @PostMapping("/api/notifications/send")
    void sendNotification(@RequestBody AppointmentEvent event);
}