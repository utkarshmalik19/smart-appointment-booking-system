package com.nagarro.patient_service.repository;

import com.nagarro.patient_service.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByUserId(Long userId);

}
