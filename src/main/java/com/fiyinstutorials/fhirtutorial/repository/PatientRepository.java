package com.fiyinstutorials.fhirtutorial.repository;

import com.fiyinstutorials.fhirtutorial.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByPatientId(String patientId);

    Optional<Patient> findByPatientId(String patientId);
}
