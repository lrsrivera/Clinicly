package com.icc.clinic.util;

import com.icc.clinic.model.User;
import com.icc.clinic.model.Patient;
import com.icc.clinic.repository.UserRepository;
import com.icc.clinic.repository.PatientRepository;
import com.icc.clinic.service.EncryptionService;
import com.icc.clinic.service.UserService;
import com.icc.clinic.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DataEncryptionMigration {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PatientService patientService;
    
    @PostConstruct
    public void migrateExistingData() {
        try {
            System.out.println("[MIGRATION] Starting data encryption migration...");
            
            // Migrate User data
            System.out.println("[MIGRATION] Migrating User data...");
            userService.fixCorruptedEncryption();
            userService.reEncryptAllUsers();
            
            // Migrate Patient data
            System.out.println("[MIGRATION] Migrating Patient data...");
            migratePatientData();
            
            System.out.println("[MIGRATION] Migration completed.");
            
        } catch (Exception e) {
            System.err.println("[MIGRATION] Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void migratePatientData() {
        List<Patient> patients = patientRepository.findAll();
        System.out.println("[MIGRATION] Found " + patients.size() + " patients to migrate");
        
        for (Patient patient : patients) {
            // Check if patient data is already encrypted
            if (!isPatientDataEncrypted(patient)) {
                System.out.println("[MIGRATION] Encrypting patient: " + patient.getStudentId());
                Patient encryptedPatient = patientService.encryptSensitiveData(patient);
                patientRepository.save(encryptedPatient);
            } else {
                System.out.println("[MIGRATION] Patient already encrypted: " + patient.getStudentId());
            }
        }
        
        System.out.println("[MIGRATION] Patient data migration completed");
    }
    
    private boolean isPatientDataEncrypted(Patient patient) {
        // Check if first name is encrypted (Base64 encoded)
        return encryptionService.isEncrypted(patient.getFirstName());
    }
    
    private boolean isDataEncrypted(User user) {
        // Check if first name is encrypted (Base64 encoded)
        return encryptionService.isEncrypted(user.getFirstName());
    }
}
