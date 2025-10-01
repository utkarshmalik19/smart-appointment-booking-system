package com.nagarro.doctor_service.service;

import com.nagarro.doctor_service.config.FeignConfig;
import com.nagarro.doctor_service.model.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AuthClient {
    @GetMapping("/api/auth/users/{id}")
    UserDto getUserById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader);
}
