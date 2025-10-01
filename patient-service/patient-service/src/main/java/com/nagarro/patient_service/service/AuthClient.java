package com.nagarro.patient_service.service;

import com.nagarro.patient_service.config.FeignConfig;
import com.nagarro.patient_service.model.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AuthClient {
    @GetMapping("/api/auth/users/{id}")
    UserDto getUserById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader);
    @GetMapping("/api/auth/authenticate")
    UserDto getUserFromToken(@RequestHeader("Authorization") String authHeader);
}
