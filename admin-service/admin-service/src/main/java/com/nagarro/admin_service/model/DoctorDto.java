package com.nagarro.admin_service.model;

import lombok.Data;

@Data
public class DoctorDto {
    private Long id;
    private Long userId;
    private String speciality;
    private String phone;
    private int yearsOfExperience;
}
