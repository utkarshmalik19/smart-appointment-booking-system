package com.nagarro.admin_service.model;

import lombok.Data;

@Data
public class SignupRequestDto {
    private String username;
    private String password;
}