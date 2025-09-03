package com.icc.clinic.repository;

import com.icc.clinic.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    
    @Query("SELECT a FROM Appointment a WHERE a.startTime BETWEEN :startDate AND :endDate")
    List<Appointment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId ORDER BY a.startTime DESC")
    List<Appointment> findLatestAppointmentByPatientId(Long patientId);

    List<Appointment> findByArchivedFalse();

    List<Appointment> findByArchivedTrue();
} 