package com.icc.clinic.controller;

import com.icc.clinic.model.Patient;
import com.icc.clinic.service.PatientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.icc.clinic.config.ApplicationContextProvider;

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
    
    private Patient patient;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private boolean readOnly = false;
    
    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("Male", "Female", "Other");
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
        if (!validateFields()) {
            return;
        }
        
        if (patient == null) {
            patient = new Patient();
        }
        
        patient.setStudentId(studentIdField.getText());
        patient.setFirstName(firstNameField.getText());
        patient.setLastName(lastNameField.getText());
        patient.setDateOfBirth(dateOfBirthPicker.getValue().format(dateFormatter));
        patient.setGender(genderComboBox.getValue());
        patient.setGrade(gradeField.getText());
        patient.setContactNumber(contactNumberField.getText());
        patient.setAddress(addressField.getText());
        patient.setAllergies(allergiesField.getText());
        patient.setMedicalHistory(medicalHistoryField.getText());
        
        patientService.savePatient(patient);
        closeWindow();
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private boolean validateFields() {
        if (studentIdField.getText().isEmpty() ||
            firstNameField.getText().isEmpty() ||
            lastNameField.getText().isEmpty() ||
            dateOfBirthPicker.getValue() == null ||
            genderComboBox.getValue() == null ||
            gradeField.getText().isEmpty()) {
            
            showError("Validation Error", "Please fill in all required fields");
            return false;
        }
        return true;
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