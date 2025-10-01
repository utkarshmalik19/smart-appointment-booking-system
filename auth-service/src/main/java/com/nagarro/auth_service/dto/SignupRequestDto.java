package com.nagarro.auth_service.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    private String username;
    private String password;
}
