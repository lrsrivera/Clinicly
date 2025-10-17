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
    private EncryptionService encryptionService;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public List<Patient> getAllPatients() {
        List<Patient> patients = patientRepository.findByArchivedFalse();
        System.out.println("[PatientService] Found " + patients.size() + " patients from database");
        
        // Decrypt sensitive data when reading
        List<Patient> decryptedPatients = patients.stream()
                .map(this::decryptSensitiveData)
                .collect(java.util.stream.Collectors.toList());
        
        System.out.println("[PatientService] Decrypted " + decryptedPatients.size() + " patients");
        if (!decryptedPatients.isEmpty()) {
            Patient first = decryptedPatients.get(0);
            System.out.println("[PatientService] First patient - ID: " + first.getId() + 
                             ", FirstName: " + first.getFirstName() + 
                             ", LastName: " + first.getLastName());
        }
        
        return decryptedPatients;
    }
    
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(this::decryptSensitiveData);
    }
    
    public Optional<Patient> getPatientByStudentId(String studentId) {
        return patientRepository.findByStudentId(studentId);
    }
    
    @Transactional
    public Patient savePatient(Patient patient) {
        // Encrypt sensitive data before saving
        Patient encryptedPatient = encryptSensitiveData(patient);
        return patientRepository.save(encryptedPatient);
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
    
    // Encryption methods for sensitive patient data
    public Patient encryptSensitiveData(Patient patient) {
        if (patient == null) return patient;
        
        Patient encryptedPatient = new Patient();
        encryptedPatient.setId(patient.getId());
        encryptedPatient.setStudentId(patient.getStudentId()); // Student ID not encrypted (used for search)
        encryptedPatient.setFirstName(encryptionService.encrypt(patient.getFirstName()));
        encryptedPatient.setLastName(encryptionService.encrypt(patient.getLastName()));
        encryptedPatient.setDateOfBirth(patient.getDateOfBirth()); // Date not encrypted (used for queries)
        encryptedPatient.setGender(patient.getGender()); // Gender not encrypted (used for filtering)
        encryptedPatient.setGrade(patient.getGrade()); // Grade not encrypted (used for filtering)
        encryptedPatient.setContactNumber(encryptionService.encrypt(patient.getContactNumber()));
        encryptedPatient.setEmail(encryptionService.encrypt(patient.getEmail()));
        encryptedPatient.setAddress(encryptionService.encrypt(patient.getAddress()));
        encryptedPatient.setAllergies(encryptionService.encrypt(patient.getAllergies()));
        encryptedPatient.setMedicalHistory(encryptionService.encrypt(patient.getMedicalHistory()));
        encryptedPatient.setCreatedAt(patient.getCreatedAt());
        encryptedPatient.setArchived(patient.isArchived());
        
        return encryptedPatient;
    }
    
    public Patient decryptSensitiveData(Patient patient) {
        if (patient == null) return patient;
        
        System.out.println("[PatientService] Decrypting patient ID: " + patient.getId());
        System.out.println("[PatientService] Original FirstName: " + patient.getFirstName());
        System.out.println("[PatientService] Original LastName: " + patient.getLastName());
        
        Patient decryptedPatient = new Patient();
        decryptedPatient.setId(patient.getId());
        decryptedPatient.setStudentId(patient.getStudentId());
        decryptedPatient.setFirstName(encryptionService.decrypt(patient.getFirstName()));
        decryptedPatient.setLastName(encryptionService.decrypt(patient.getLastName()));
        decryptedPatient.setDateOfBirth(patient.getDateOfBirth());
        decryptedPatient.setGender(patient.getGender());
        decryptedPatient.setGrade(patient.getGrade());
        decryptedPatient.setContactNumber(encryptionService.decrypt(patient.getContactNumber()));
        decryptedPatient.setEmail(encryptionService.decrypt(patient.getEmail()));
        decryptedPatient.setAddress(encryptionService.decrypt(patient.getAddress()));
        decryptedPatient.setAllergies(encryptionService.decrypt(patient.getAllergies()));
        decryptedPatient.setMedicalHistory(encryptionService.decrypt(patient.getMedicalHistory()));
        decryptedPatient.setCreatedAt(patient.getCreatedAt());
        decryptedPatient.setArchived(patient.isArchived());
        
        System.out.println("[PatientService] Decrypted FirstName: " + decryptedPatient.getFirstName());
        System.out.println("[PatientService] Decrypted LastName: " + decryptedPatient.getLastName());
        
        return decryptedPatient;
    }
} 