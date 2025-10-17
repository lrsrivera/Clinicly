package com.icc.clinic.controller;

import com.icc.clinic.model.Appointment;
import com.icc.clinic.model.Patient;
import com.icc.clinic.service.PatientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.icc.clinic.config.ApplicationContextProvider;

@Controller
public class AppointmentFormController {
    
    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> startHourComboBox;
    @FXML private ComboBox<String> startMinuteComboBox;
    @FXML private ComboBox<String> startAmPmComboBox;
    @FXML private ComboBox<String> endHourComboBox;
    @FXML private ComboBox<String> endMinuteComboBox;
    @FXML private ComboBox<String> endAmPmComboBox;
    @FXML private ComboBox<String> purposeComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea notesField;
    
    @Autowired
    private PatientService patientService;
    
    private Appointment appointment;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    @FXML
    public void initialize() {
        // Load patients
        List<Patient> patients = patientService.getAllPatients();
        patientComboBox.getItems().addAll(patients);
        
        // Set patient display format
        patientComboBox.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient != null ? 
                    patient.getStudentId() + " - " + patient.getFirstName() + " " + patient.getLastName() : "";
            }
            
            @Override
            public Patient fromString(String string) {
                return null;
            }
        });
        
        // Set up hour options
        for (int hour = 1; hour <= 12; hour++) {
            String hourStr = String.format("%02d", hour);
            startHourComboBox.getItems().add(hourStr);
            endHourComboBox.getItems().add(hourStr);
        }
        
        // Set up minute options
        for (int minute = 0; minute < 60; minute += 15) {
            String minuteStr = String.format("%02d", minute);
            startMinuteComboBox.getItems().add(minuteStr);
            endMinuteComboBox.getItems().add(minuteStr);
        }
        
        // Set up AM/PM options
        startAmPmComboBox.getItems().addAll("AM", "PM");
        endAmPmComboBox.getItems().addAll("AM", "PM");
        
        // Set up purpose options
        purposeComboBox.getItems().addAll(
            "Regular Check-up",
            "Consultation",
            "Follow-up",
            "Emergency",
            "Vaccination",
            "Other"
        );
        
        // Set up status options
        statusComboBox.getItems().addAll(
            "Scheduled",
            "Completed",
            "Cancelled",
            "No-show"
        );
        statusComboBox.setValue("Scheduled");
        
        // Set default date to today
        datePicker.setValue(LocalDate.now());
    }
    
    @FXML
    private void handleSave() {
        if (!validateFields()) {
            return;
        }
        
        if (appointment == null) {
            appointment = new Appointment();
        }
        
        LocalDate date = datePicker.getValue();
        LocalTime startTime = parseTime(startHourComboBox.getValue(), startMinuteComboBox.getValue(), startAmPmComboBox.getValue());
        LocalTime endTime = parseTime(endHourComboBox.getValue(), endMinuteComboBox.getValue(), endAmPmComboBox.getValue());
        
        if (startTime.isAfter(endTime)) {
            showError("Validation Error", "Start time must be before end time");
            return;
        }
        
        appointment.setPatient(patientComboBox.getValue());
        appointment.setStartTime(LocalDateTime.of(date, startTime));
        appointment.setEndTime(LocalDateTime.of(date, endTime));
        appointment.setPurpose(purposeComboBox.getValue());
        appointment.setStatus(statusComboBox.getValue());
        appointment.setNotes(notesField.getText());
        
        patientService.addAppointment(appointment.getPatient().getId(), appointment);
        closeWindow();
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private LocalTime parseTime(String hour, String minute, String amPm) {
        int hourInt = Integer.parseInt(hour);
        if (amPm.equals("PM") && hourInt != 12) {
            hourInt += 12;
        } else if (amPm.equals("AM") && hourInt == 12) {
            hourInt = 0;
        }
        return LocalTime.of(hourInt, Integer.parseInt(minute));
    }
    
    private boolean validateFields() {
        if (patientComboBox.getValue() == null ||
            datePicker.getValue() == null ||
            startHourComboBox.getValue() == null ||
            startMinuteComboBox.getValue() == null ||
            startAmPmComboBox.getValue() == null ||
            endHourComboBox.getValue() == null ||
            endMinuteComboBox.getValue() == null ||
            endAmPmComboBox.getValue() == null ||
            purposeComboBox.getValue() == null ||
            statusComboBox.getValue() == null) {
            
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
        Stage stage = (Stage) patientComboBox.getScene().getWindow();
        stage.close();
    }
} 