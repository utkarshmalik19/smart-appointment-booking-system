package com.nagarro.admin_service.service;

import com.nagarro.admin_service.config.FeignConfig;
import com.nagarro.admin_service.model.SignupRequestDto;
import com.nagarro.admin_service.model.SignupResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AuthClient {
    @PostMapping("/api/auth/signup/doctor")
    SignupResponseDto signupDoctor(@RequestBody SignupRequestDto signupRequestDto);
}
