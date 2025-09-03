package com.icc.clinic.repository;

import com.icc.clinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByStudentId(String studentId);
    
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.studentId) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Patient> searchPatients(String query);

    List<Patient> findByArchivedFalse();

    List<Patient> findByArchivedTrue();
} 