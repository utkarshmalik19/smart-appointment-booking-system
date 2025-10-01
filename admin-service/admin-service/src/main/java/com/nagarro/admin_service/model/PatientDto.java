package com.nagarro.admin_service.model;

import lombok.Data;

@Data
public class PatientDto{
    private Long id;
    private Long userId;
    private String phone;
    private Integer age;
    private String gender;
}