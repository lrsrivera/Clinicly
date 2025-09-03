package com.icc.clinic.service;

import com.icc.clinic.model.Patient;
import com.icc.clinic.model.Appointment;
import com.icc.clinic.repository.PatientRepository;
import com.icc.clinic.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public List<Patient> getAllPatients() {
        return patientRepository.findByArchivedFalse();
    }
    
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }
    
    public Optional<Patient> getPatientByStudentId(String studentId) {
        return patientRepository.findByStudentId(studentId);
    }
    
    @Transactional
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }
    
    @Transactional
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
    
    public List<Patient> searchPatients(String query) {
        return patientRepository.searchPatients(query);
    }
    
    @Transactional
    public Appointment addAppointment(Long patientId, Appointment appointment) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        appointment.setPatient(patient);
        return appointmentRepository.save(appointment);
    }
    
    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
    
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByDateRange(startDate, endDate);
    }
    
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findByArchivedFalse();
    }
    
    public List<Patient> getArchivedPatients() {
        return patientRepository.findByArchivedTrue();
    }
    
    public List<Appointment> getArchivedAppointments() {
        return appointmentRepository.findByArchivedTrue();
    }
} 