package com.icc.clinic.validation;

import com.icc.clinic.model.Patient;
import com.icc.clinic.model.User;
import com.icc.clinic.model.Appointment;
import com.icc.clinic.repository.PatientRepository;
import com.icc.clinic.repository.UserRepository;
import com.icc.clinic.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Central validation service for all CRUD operations
 */
@Service
public class ValidationService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Constants for validation
    private static final String[] GENDER_VALUES = {"Male", "Female"};
    private static final String[] GRADE_VALUES = {
        "Kindergarten", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", 
        "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12", "Faculty"
    };
    private static final String[] APPOINTMENT_STATUS_VALUES = {"Scheduled", "Completed", "Cancelled", "No-show"};

    /**
     * Validates a Patient object for create/update operations
     */
    public ValidationResult validatePatient(Patient patient, boolean isUpdate) {
        ValidationResult result = new ValidationResult();

        // Student ID validation
        ValidationResult studentIdResult = FieldValidators.validateRequired(patient.getStudentId(), "Student ID");
        result.addErrors(studentIdResult.getErrors());
        
        if (studentIdResult.isValid()) {
            studentIdResult = FieldValidators.validateStudentId(patient.getStudentId(), "Student ID");
            result.addErrors(studentIdResult.getErrors());
            
            // Check uniqueness (only for new patients or if student ID changed)
            if (studentIdResult.isValid()) {
                boolean exists = patientRepository.existsByStudentId(patient.getStudentId());
                if (exists && (!isUpdate || !patientRepository.findByStudentId(patient.getStudentId()).get().getId().equals(patient.getId()))) {
                    result.addError("Student ID already exists");
                }
            }
        }

        // First Name validation
        ValidationResult firstNameResult = FieldValidators.validateRequired(patient.getFirstName(), "First Name");
        result.addErrors(firstNameResult.getErrors());
        if (firstNameResult.isValid()) {
            firstNameResult = FieldValidators.validateLength(patient.getFirstName(), "First Name", 2, 50);
            result.addErrors(firstNameResult.getErrors());
            if (firstNameResult.isValid()) {
                firstNameResult = FieldValidators.validateName(patient.getFirstName(), "First Name");
                result.addErrors(firstNameResult.getErrors());
            }
        }

        // Last Name validation
        ValidationResult lastNameResult = FieldValidators.validateRequired(patient.getLastName(), "Last Name");
        result.addErrors(lastNameResult.getErrors());
        if (lastNameResult.isValid()) {
            lastNameResult = FieldValidators.validateLength(patient.getLastName(), "Last Name", 2, 50);
            result.addErrors(lastNameResult.getErrors());
            if (lastNameResult.isValid()) {
                lastNameResult = FieldValidators.validateName(patient.getLastName(), "Last Name");
                result.addErrors(lastNameResult.getErrors());
            }
        }

        // Date of Birth validation
        ValidationResult dobResult = FieldValidators.validateRequired(patient.getDateOfBirth(), "Date of Birth");
        result.addErrors(dobResult.getErrors());
        if (dobResult.isValid()) {
            dobResult = FieldValidators.validateDate(patient.getDateOfBirth(), "Date of Birth", 
                LocalDate.of(1900, 1, 1), LocalDate.now());
            result.addErrors(dobResult.getErrors());
        }

        // Gender validation
        ValidationResult genderResult = FieldValidators.validateRequired(patient.getGender(), "Gender");
        result.addErrors(genderResult.getErrors());
        if (genderResult.isValid()) {
            genderResult = FieldValidators.validateAllowedValues(patient.getGender(), "Gender", GENDER_VALUES);
            result.addErrors(genderResult.getErrors());
        }

        // Grade validation
        ValidationResult gradeResult = FieldValidators.validateRequired(patient.getGrade(), "Grade");
        result.addErrors(gradeResult.getErrors());
        if (gradeResult.isValid()) {
            gradeResult = FieldValidators.validateAllowedValues(patient.getGrade(), "Grade", GRADE_VALUES);
            result.addErrors(gradeResult.getErrors());
        }

        // Contact Number validation (optional)
        if (patient.getContactNumber() != null && !patient.getContactNumber().trim().isEmpty()) {
            ValidationResult contactResult = FieldValidators.validatePhone(patient.getContactNumber(), "Contact Number");
            result.addErrors(contactResult.getErrors());
        }

        // Address validation (optional)
        if (patient.getAddress() != null && !patient.getAddress().trim().isEmpty()) {
            ValidationResult addressResult = FieldValidators.validateLength(patient.getAddress(), "Address", 1, 500);
            result.addErrors(addressResult.getErrors());
        }

        // Allergies validation (optional)
        if (patient.getAllergies() != null && !patient.getAllergies().trim().isEmpty()) {
            ValidationResult allergiesResult = FieldValidators.validateLength(patient.getAllergies(), "Allergies", 1, 1000);
            result.addErrors(allergiesResult.getErrors());
        }

        // Medical History validation (optional)
        if (patient.getMedicalHistory() != null && !patient.getMedicalHistory().trim().isEmpty()) {
            ValidationResult medicalResult = FieldValidators.validateLength(patient.getMedicalHistory(), "Medical History", 1, 1000);
            result.addErrors(medicalResult.getErrors());
        }

        // Additional safety checks for SQL/script injection
        if (!FieldValidators.isSafeInput(patient.getStudentId())) {
            result.addError("Student ID contains disallowed characters");
        }
        if (!FieldValidators.isSafeInput(patient.getFirstName())) {
            result.addError("First name contains disallowed characters");
        }
        if (!FieldValidators.isSafeInput(patient.getLastName())) {
            result.addError("Last name contains disallowed characters");
        }
        if (!FieldValidators.isSafeInput(patient.getContactNumber())) {
            result.addError("Contact number contains disallowed characters");
        }
        if (!FieldValidators.isSafeInput(patient.getEmail())) {
            result.addError("Email contains disallowed characters");
        }
        if (!FieldValidators.isSafeInput(patient.getAddress())) {
            result.addError("Address contains disallowed characters");
        }
        if (!FieldValidators.isSafeInput(patient.getAllergies())) {
            result.addError("Allergies contains disallowed characters");
        }
        if (!FieldValidators.isSafeInput(patient.getMedicalHistory())) {
            result.addError("Medical history contains disallowed characters");
        }

        return result;
    }

    /**
     * Validates a User object for create/update operations
     */
    public ValidationResult validateUser(User user, boolean isUpdate) {
        ValidationResult result = new ValidationResult();

        // Username validation
        ValidationResult usernameResult = FieldValidators.validateRequired(user.getUsername(), "Username");
        result.addErrors(usernameResult.getErrors());
        
        if (usernameResult.isValid()) {
            usernameResult = FieldValidators.validateLength(user.getUsername(), "Username", 3, 20);
            result.addErrors(usernameResult.getErrors());
            
            if (usernameResult.isValid()) {
                usernameResult = FieldValidators.validateAlphanumeric(user.getUsername(), "Username");
                result.addErrors(usernameResult.getErrors());
                
                // Check uniqueness (only for new users or if username changed)
                if (usernameResult.isValid()) {
                    boolean exists = userRepository.existsByUsername(user.getUsername());
                    if (exists && (!isUpdate || !userRepository.findByUsername(user.getUsername()).get().getId().equals(user.getId()))) {
                        result.addError("Username already exists");
                    }
                }
            }
        }

        // Password validation (only for new users or when password is being changed)
        if (!isUpdate || (user.getPassword() != null && !user.getPassword().startsWith("$2a$"))) {
            ValidationResult passwordResult = FieldValidators.validateRequired(user.getPassword(), "Password");
            result.addErrors(passwordResult.getErrors());
            if (passwordResult.isValid()) {
                passwordResult = FieldValidators.validateLength(user.getPassword(), "Password", 8, 100);
                result.addErrors(passwordResult.getErrors());
            }
        }

        // First Name validation
        ValidationResult firstNameResult = FieldValidators.validateRequired(user.getFirstName(), "First Name");
        result.addErrors(firstNameResult.getErrors());
        if (firstNameResult.isValid()) {
            firstNameResult = FieldValidators.validateLength(user.getFirstName(), "First Name", 2, 50);
            result.addErrors(firstNameResult.getErrors());
            if (firstNameResult.isValid()) {
                firstNameResult = FieldValidators.validateName(user.getFirstName(), "First Name");
                result.addErrors(firstNameResult.getErrors());
            }
        }

        // Last Name validation
        ValidationResult lastNameResult = FieldValidators.validateRequired(user.getLastName(), "Last Name");
        result.addErrors(lastNameResult.getErrors());
        if (lastNameResult.isValid()) {
            lastNameResult = FieldValidators.validateLength(user.getLastName(), "Last Name", 2, 50);
            result.addErrors(lastNameResult.getErrors());
            if (lastNameResult.isValid()) {
                lastNameResult = FieldValidators.validateName(user.getLastName(), "Last Name");
                result.addErrors(lastNameResult.getErrors());
            }
        }

        // Email validation
        ValidationResult emailResult = FieldValidators.validateRequired(user.getEmail(), "Email");
        result.addErrors(emailResult.getErrors());
        if (emailResult.isValid()) {
            emailResult = FieldValidators.validateEmail(user.getEmail(), "Email");
            result.addErrors(emailResult.getErrors());
            
            // Check uniqueness (only for new users or if email changed)
            if (emailResult.isValid()) {
                boolean exists = userRepository.existsByEmail(user.getEmail());
                if (exists && (!isUpdate || !userRepository.findByEmail(user.getEmail()).get().getId().equals(user.getId()))) {
                    result.addError("Email already exists");
                }
            }
        }

        // Role validation
        if (user.getRole() == null) {
            result.addError("Role is required");
        } else if (!roleRepository.existsById(user.getRole().getId())) {
            result.addError("Selected role does not exist");
        }

        return result;
    }

    /**
     * Validates an Appointment object for create/update operations
     */
    public ValidationResult validateAppointment(Appointment appointment, boolean isUpdate) {
        ValidationResult result = new ValidationResult();

        // Patient validation
        if (appointment.getPatient() == null) {
            result.addError("Patient is required");
        } else if (!patientRepository.existsById(appointment.getPatient().getId())) {
            result.addError("Selected patient does not exist");
        }

        // Start Time validation
        if (appointment.getStartTime() == null) {
            result.addError("Start time is required");
        } else {
            ValidationResult startTimeResult = FieldValidators.validateNotPast(appointment.getStartTime(), "Start time");
            result.addErrors(startTimeResult.getErrors());
            
            if (startTimeResult.isValid()) {
                startTimeResult = FieldValidators.validateBusinessHours(appointment.getStartTime(), "Start time");
                result.addErrors(startTimeResult.getErrors());
            }
        }

        // End Time validation
        if (appointment.getEndTime() == null) {
            result.addError("End time is required");
        } else {
            ValidationResult endTimeResult = FieldValidators.validateNotPast(appointment.getEndTime(), "End time");
            result.addErrors(endTimeResult.getErrors());
            
            if (endTimeResult.isValid()) {
                endTimeResult = FieldValidators.validateBusinessHours(appointment.getEndTime(), "End time");
                result.addErrors(endTimeResult.getErrors());
            }
        }

        // Time order validation
        if (appointment.getStartTime() != null && appointment.getEndTime() != null) {
            ValidationResult timeOrderResult = FieldValidators.validateTimeOrder(
                appointment.getStartTime(), appointment.getEndTime(), "Start time", "End time");
            result.addErrors(timeOrderResult.getErrors());
            
            if (timeOrderResult.isValid()) {
                ValidationResult durationResult = FieldValidators.validateAppointmentDuration(
                    appointment.getStartTime(), appointment.getEndTime(), 15, 240); // 15 min to 4 hours
                result.addErrors(durationResult.getErrors());
            }
        }

        // Purpose validation
        ValidationResult purposeResult = FieldValidators.validateRequired(appointment.getPurpose(), "Purpose");
        result.addErrors(purposeResult.getErrors());
        if (purposeResult.isValid()) {
            purposeResult = FieldValidators.validateLength(appointment.getPurpose(), "Purpose", 5, 200);
            result.addErrors(purposeResult.getErrors());
        }

        // Status validation
        ValidationResult statusResult = FieldValidators.validateRequired(appointment.getStatus(), "Status");
        result.addErrors(statusResult.getErrors());
        if (statusResult.isValid()) {
            statusResult = FieldValidators.validateAllowedValues(appointment.getStatus(), "Status", APPOINTMENT_STATUS_VALUES);
            result.addErrors(statusResult.getErrors());
        }

        // Notes validation (optional)
        if (appointment.getNotes() != null && !appointment.getNotes().trim().isEmpty()) {
            ValidationResult notesResult = FieldValidators.validateLength(appointment.getNotes(), "Notes", 1, 500);
            result.addErrors(notesResult.getErrors());
        }

        return result;
    }

    /**
     * Validates login credentials
     */
    public ValidationResult validateLogin(String username, String password) {
        ValidationResult result = new ValidationResult();

        ValidationResult usernameResult = FieldValidators.validateRequired(username, "Username");
        result.addErrors(usernameResult.getErrors());

        ValidationResult passwordResult = FieldValidators.validateRequired(password, "Password");
        result.addErrors(passwordResult.getErrors());

        // Safety check for login inputs
        if (!FieldValidators.isSafeInput(username)) {
            result.addError("Username contains disallowed characters");
        }
        if (!FieldValidators.isSafeInput(password)) {
            result.addError("Password contains disallowed characters");
        }

        return result;
    }

}
