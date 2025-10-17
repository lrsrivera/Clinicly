package com.icc.clinic.controller;

import com.icc.clinic.model.Patient;
import com.icc.clinic.service.PatientService;
import com.icc.clinic.validation.ValidationService;
import com.icc.clinic.validation.ValidationResult;
import com.icc.clinic.validation.UIValidationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PatientFormController {
    
    @FXML private TextField studentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField gradeField;
    @FXML private TextField contactNumberField;
    @FXML private TextArea addressField;
    @FXML private TextArea allergiesField;
    @FXML private TextArea medicalHistoryField;
    @FXML private Button saveButton;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private ValidationService validationService;
    
    private UIValidationHelper uiValidationHelper = new UIValidationHelper();
    
    private Patient patient;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private boolean readOnly = false;
    
    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("Male", "Female");

        // Prevent typing manually in the DatePicker
        dateOfBirthPicker.getEditor().setDisable(true);
        dateOfBirthPicker.getEditor().setOpacity(1); // keeps it visible
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
        if (patient != null) {
            studentIdField.setText(patient.getStudentId());
            firstNameField.setText(patient.getFirstName());
            lastNameField.setText(patient.getLastName());
            dateOfBirthPicker.setValue(LocalDate.parse(patient.getDateOfBirth(), dateFormatter));
            genderComboBox.setValue(patient.getGender());
            gradeField.setText(patient.getGrade());
            contactNumberField.setText(patient.getContactNumber());
            addressField.setText(patient.getAddress());
            allergiesField.setText(patient.getAllergies());
            medicalHistoryField.setText(patient.getMedicalHistory());
        }
    }
    
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        studentIdField.setDisable(readOnly);
        firstNameField.setDisable(readOnly);
        lastNameField.setDisable(readOnly);
        dateOfBirthPicker.setDisable(readOnly);
        genderComboBox.setDisable(readOnly);
        gradeField.setDisable(readOnly);
        contactNumberField.setDisable(readOnly);
        addressField.setDisable(readOnly);
        allergiesField.setDisable(readOnly);
        medicalHistoryField.setDisable(readOnly);
        if (saveButton != null) saveButton.setVisible(!readOnly);
    }
    
    @FXML
    private void handleSave() {
        // Clear any previous validation errors
        uiValidationHelper.clearAllValidation();
        
        // Create patient object from form data
        Patient patientToSave = new Patient();
        patientToSave.setStudentId(studentIdField.getText());
        patientToSave.setFirstName(firstNameField.getText());
        patientToSave.setLastName(lastNameField.getText());
        patientToSave.setDateOfBirth(dateOfBirthPicker.getValue() != null ? dateOfBirthPicker.getValue().format(dateFormatter) : null);
        patientToSave.setGender(genderComboBox.getValue());
        patientToSave.setGrade(gradeField.getText());
        patientToSave.setContactNumber(contactNumberField.getText());
        patientToSave.setAddress(addressField.getText());
        patientToSave.setAllergies(allergiesField.getText());
        patientToSave.setMedicalHistory(medicalHistoryField.getText());
        
        // If editing existing patient, preserve the ID
        if (patient != null) {
            patientToSave.setId(patient.getId());
        }
        
        // Validate patient data
        ValidationResult result = validationService.validatePatient(patientToSave, patient != null);
        
        if (!result.isValid()) {
            // Show validation errors
            showValidationErrors(result.getErrors());
            return;
        }
        
        try {
            // Save patient
            patientService.savePatient(patientToSave);
            closeWindow();
        } catch (Exception e) {
            showError("Error", "Failed to save patient: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private void showValidationErrors(List<String> errors) {
        StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n\n");
        for (String error : errors) {
            errorMessage.append("• ").append(error).append("\n");
        }
        showError("Validation Error", errorMessage.toString());
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) studentIdField.getScene().getWindow();
        stage.close();
    }
} 