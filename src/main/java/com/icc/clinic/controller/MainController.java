package com.icc.clinic.controller;

import com.icc.clinic.model.Patient;
import com.icc.clinic.model.Appointment;
import com.icc.clinic.service.PatientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.icc.clinic.config.ApplicationContextProvider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;

@Controller
public class MainController {
    
    @FXML private TextField searchField;
    @FXML private TableView<Patient> patientsTable;
    @FXML private TableColumn<Patient, String> studentIdColumn;
    @FXML private TableColumn<Patient, String> nameColumn;
    @FXML private TableColumn<Patient, String> gradeColumn;
    @FXML private TableColumn<Patient, String> lastVisitColumn;
    @FXML private TableColumn<Patient, Void> actionsColumn;
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> patientColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> dateColumn;
    @FXML private TableColumn<Appointment, String> purposeColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private TableColumn<Appointment, Void> appointmentActionsColumn;
    
    @FXML private DatePicker reportStartDate;
    @FXML private DatePicker reportEndDate;
    @FXML private TableView<Object> reportTable;
    
    @FXML private TextField studentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private ComboBox<String> gradeComboBox;
    @FXML private TextField contactNumberField;
    @FXML private TextArea addressField;
    @FXML private TextArea allergiesField;
    @FXML private TextArea medicalHistoryField;
    @FXML private TextArea visitNotesField;
    
    @FXML private ComboBox<Patient> appointmentPatientComboBox;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private ComboBox<String> appointmentStartHourComboBox;
    @FXML private ComboBox<String> appointmentStartMinuteComboBox;
    @FXML private ComboBox<String> appointmentStartAmPmComboBox;
    @FXML private ComboBox<String> appointmentEndHourComboBox;
    @FXML private ComboBox<String> appointmentEndMinuteComboBox;
    @FXML private ComboBox<String> appointmentEndAmPmComboBox;
    @FXML private TextField appointmentPurposeField;
    @FXML private TextField appointmentNotesField;
    @FXML private DatePicker filterStartDate;
    @FXML private DatePicker filterEndDate;
    @FXML private TableColumn<Appointment, Number> appointmentNoColumn;
    @FXML private TableColumn<Appointment, String> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> appointmentStartTimeColumn;
    @FXML private TableColumn<Appointment, String> appointmentEndTimeColumn;
    @FXML private TableColumn<Appointment, String> appointmentNameColumn;
    @FXML private TableColumn<Appointment, String> appointmentStudentNoColumn;
    @FXML private TableColumn<Appointment, String> appointmentPurposeColumn;
    @FXML private TableColumn<Appointment, String> appointmentStatusColumn;
    
    @FXML private TabPane tabPane;
    @FXML private Tab patientsListTab;
    
    @FXML private TableView<Patient> archivedPatientsTable;
    @FXML private TableColumn<Patient, String> archivedPatientStudentIdColumn;
    @FXML private TableColumn<Patient, String> archivedPatientNameColumn;
    @FXML private TableColumn<Patient, String> archivedPatientGradeColumn;
    @FXML private TableColumn<Patient, Void> archivedPatientActionsColumn;
    @FXML private TableView<Appointment> archivedAppointmentsTable;
    @FXML private TableColumn<Appointment, Number> archivedAppointmentNoColumn;
    @FXML private TableColumn<Appointment, String> archivedAppointmentDateColumn;
    @FXML private TableColumn<Appointment, String> archivedAppointmentStartTimeColumn;
    @FXML private TableColumn<Appointment, String> archivedAppointmentEndTimeColumn;
    @FXML private TableColumn<Appointment, String> archivedAppointmentNameColumn;
    @FXML private TableColumn<Appointment, String> archivedAppointmentStudentNoColumn;
    @FXML private TableColumn<Appointment, String> archivedAppointmentPurposeColumn;
    @FXML private TableColumn<Appointment, String> archivedAppointmentStatusColumn;
    @FXML private TableColumn<Appointment, Void> archivedAppointmentActionsColumn;
    
    @FXML private TextField archivedPatientFilterField;
    @FXML private TextField archivedAppointmentFilterField;
    @FXML private DatePicker archivedAppointmentDateFilter;
    
    @Autowired
    private PatientService patientService;
    
    private List<Appointment> allAppointments;
    private List<Patient> allArchivedPatients = List.of();
    private List<Appointment> allArchivedAppointments = List.of();
    
    @FXML
    public void initialize() {
        try {
            patientsTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            genderComboBox.getItems().setAll("Male", "Female", "Other");
            gradeComboBox.getItems().setAll(
                "Kindergarten",
                "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6",
                "Grade 7", "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12",
                "Teacher", "Staff", "Nurse", "Admin"
            );
            setupPatientsTable();
            setupAppointmentsTable();
            setupAppointmentsManagement();
            loadPatients();
            loadAppointments();
            setupArchiveTab();
            loadArchivedPatients();
            loadArchivedAppointments();
            // Add listener to reload patients when Patients List tab is selected
            if (tabPane != null && patientsListTab != null) {
                tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                    if (newTab == patientsListTab) {
                        loadPatients();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Initialization Error", e.getMessage());
        }
    }
    
    private void setupPatientsTable() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue();
            return new SimpleStringProperty(
                patient.getFirstName() + " " + patient.getLastName()
            );
        });
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        
        // Add action buttons
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button archiveButton = new Button("Archive");
            {
                viewButton.getStyleClass().add("action-view");
                editButton.getStyleClass().add("action-edit");
                deleteButton.getStyleClass().add("action-delete");
                archiveButton.getStyleClass().add("action-archive");
                viewButton.setMinWidth(60);
                editButton.setMinWidth(60);
                deleteButton.setMinWidth(60);
                archiveButton.setMinWidth(60);
                // Set button colors
                viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;"); // Blue
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // Green
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;"); // Red
                archiveButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;"); // Orange
                viewButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    handleViewPatient(patient);
                });
                editButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    handleEditPatient(patient);
                });
                deleteButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    handleDeletePatient(patient);
                });
                archiveButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Archive Patient");
                    alert.setHeaderText(null);
                    alert.setContentText("Archiving this patient will also archive all their appointments. Proceed?");
                    ButtonType proceed = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
                    ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(proceed, cancel);
                    alert.showAndWait().ifPresent(type -> {
                        if (type == proceed) {
                            archivePatientAndAppointments(patient);
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(6, viewButton, editButton, deleteButton, archiveButton);
                    buttons.setStyle("-fx-alignment: center-left;");
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void setupAppointmentsTable() {
        appointmentNoColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStartTime().toLocalDate().toString()));
        appointmentStartTimeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStartTime().toLocalTime().toString()));
        appointmentEndTimeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEndTime().toLocalTime().toString()));
        appointmentNameColumn.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue().getPatient();
            return new SimpleStringProperty(patient.getFirstName() + " " + patient.getLastName());
        });
        appointmentStudentNoColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPatient().getStudentId()));
        appointmentPurposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        appointmentStatusColumn.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> statusCombo = new ComboBox<>();
            {
                statusCombo.getItems().addAll("Completed", "Cancelled", "Rescheduled");
                statusCombo.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    appt.setStatus(statusCombo.getValue());
                    updateComboStyle(statusCombo, statusCombo.getValue());
                    // Optionally, save status change to DB here
                });
            }
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    statusCombo.setValue(status);
                    updateComboStyle(statusCombo, status);
                    setGraphic(statusCombo);
                }
            }
            private void updateComboStyle(ComboBox<String> combo, String value) {
                if ("Completed".equals(value)) {
                    combo.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                } else if ("Rescheduled".equals(value)) {
                    combo.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                } else if ("Cancelled".equals(value)) {
                    combo.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                } else {
                    combo.setStyle("");
                }
            }
        });
    }
    
    private void setupAppointmentsManagement() {
        List<Patient> patients = patientService.getAllPatients();
        appointmentPatientComboBox.getItems().setAll(patients);
        appointmentPatientComboBox.setConverter(new javafx.util.StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient == null ? "" : patient.getStudentId() + " - " + patient.getFirstName() + " " + patient.getLastName();
            }
            @Override
            public Patient fromString(String string) { return null; }
        });
        // Populate hour, minute, and AM/PM ComboBoxes for start and end time
        var hours = java.util.Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        var minutes = new java.util.ArrayList<String>();
        for (int m = 0; m < 60; m += 5) minutes.add(String.format("%02d", m));
        var ampm = java.util.Arrays.asList("AM", "PM");
        appointmentStartHourComboBox.getItems().setAll(hours);
        appointmentStartMinuteComboBox.getItems().setAll(minutes);
        appointmentStartAmPmComboBox.getItems().setAll(ampm);
        appointmentEndHourComboBox.getItems().setAll(hours);
        appointmentEndMinuteComboBox.getItems().setAll(minutes);
        appointmentEndAmPmComboBox.getItems().setAll(ampm);
        // Optionally set default values
        appointmentStartHourComboBox.setValue("08");
        appointmentStartMinuteComboBox.setValue("00");
        appointmentStartAmPmComboBox.setValue("AM");
        appointmentEndHourComboBox.setValue("08");
        appointmentEndMinuteComboBox.setValue("30");
        appointmentEndAmPmComboBox.setValue("AM");
        // Setup appointment table columns
        appointmentNoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(appointmentsTable.getItems().indexOf(cellData.getValue()) + 1));
        appointmentDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartTime().toLocalDate().toString()));
        appointmentStartTimeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartTime().toLocalTime().toString()));
        appointmentEndTimeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndTime().toLocalTime().toString()));
        appointmentNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPatient().getFirstName() + " " + cellData.getValue().getPatient().getLastName()));
        appointmentStudentNoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPatient().getStudentId()));
        appointmentPurposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        appointmentActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button archiveButton = new Button("Archive");
            {
                archiveButton.setMinWidth(60);
                archiveButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                archiveButton.setOnAction(event -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Archive Appointment");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want to archive this appointment?");
                    ButtonType proceed = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
                    ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(proceed, cancel);
                    alert.showAndWait().ifPresent(type -> {
                        if (type == proceed) {
                            archiveAppointment(appt);
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(archiveButton);
                }
            }
        });
        loadAllAppointments();
    }
    
    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            List<Patient> patients = patientService.searchPatients(query);
            patientsTable.getItems().setAll(patients);
        } else {
            loadPatients();
        }
    }
    
    @FXML
    private void handleAddPatient() {
        // Validate required fields
        if (studentIdField.getText().isEmpty() ||
            firstNameField.getText().isEmpty() ||
            lastNameField.getText().isEmpty() ||
            dateOfBirthPicker.getValue() == null ||
            genderComboBox.getValue() == null ||
            gradeComboBox.getValue() == null) {
            showError("Validation Error", "Please fill in all required fields");
            return;
        }
        try {
            com.icc.clinic.model.Patient patient = new com.icc.clinic.model.Patient();
            patient.setStudentId(studentIdField.getText());
            patient.setFirstName(firstNameField.getText());
            patient.setLastName(lastNameField.getText());
            patient.setDateOfBirth(dateOfBirthPicker.getValue().toString());
            patient.setGender(genderComboBox.getValue());
            patient.setGrade(gradeComboBox.getValue());
            patient.setContactNumber(contactNumberField.getText());
            patient.setAddress(addressField.getText());
            patient.setAllergies(allergiesField.getText());
            patient.setMedicalHistory(medicalHistoryField.getText());
            // Save patient
            patientService.savePatient(patient);
            // Optionally handle visit notes here if your model supports it
            // Clear form
            studentIdField.clear();
            firstNameField.clear();
            lastNameField.clear();
            dateOfBirthPicker.setValue(null);
            genderComboBox.setValue(null);
            gradeComboBox.setValue(null);
            contactNumberField.clear();
            addressField.clear();
            allergiesField.clear();
            medicalHistoryField.clear();
            visitNotesField.clear();
            // Reload patients table
            loadPatients();
            // Refresh patient ComboBox in Appointment Management
            setupAppointmentsManagement();
            showSuccessDialog("Patient added Successfully!");
        } catch (Exception e) {
            showError("Error", "Failed to add patient: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditPatient(Patient patient) {
        try {
            java.net.URL url = getClass().getResource("/fxml/patient-form.fxml");
            System.out.println("Patient form FXML URL: " + url);
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(ApplicationContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();
            PatientFormController controller = loader.getController();
            controller.setPatient(patient);
            Stage stage = new Stage();
            stage.setTitle("Edit Patient");
            stage.setScene(new Scene(root));
            stage.setWidth(500);
            stage.setHeight(600);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadPatients(); // Ensure table refresh after editing
        } catch (Exception e) {
            e.printStackTrace(); // Print real error for debugging
            showError("Error", "Could not open patient form\n" + e.getMessage());
        }
    }
    
    private void handleDeletePatient(Patient patient) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Patient");
        alert.setContentText("Are you sure you want to delete this patient?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                patientService.deletePatient(patient.getId());
                loadPatients();
            }
        });
    }
    
    @FXML
    private void handleFilterAppointments() {
        LocalDate start = filterStartDate.getValue();
        LocalDate end = filterEndDate.getValue();
        if (start == null && end == null) {
            appointmentsTable.getItems().setAll(allAppointments);
            return;
        }
        List<Appointment> filtered = allAppointments.stream().filter(appt -> {
            LocalDate apptDate = appt.getStartTime().toLocalDate();
            boolean afterStart = (start == null) || !apptDate.isBefore(start);
            boolean beforeEnd = (end == null) || !apptDate.isAfter(end);
            return afterStart && beforeEnd;
        }).toList();
        appointmentsTable.getItems().setAll(filtered);
    }
    
    @FXML
    private void handleNewAppointment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/appointment-form.fxml"));
            loader.setControllerFactory(ApplicationContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("New Appointment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            loadAppointments();
        } catch (IOException e) {
            showError("Error", "Could not open appointment form");
        }
    }
    
    @FXML
    private void handleGenerateReport() {
        LocalDate start = reportStartDate.getValue();
        LocalDate end = reportEndDate.getValue();
        
        if (start != null && end != null) {
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(23, 59, 59);
            List<Appointment> appointments = patientService.getAppointmentsByDateRange(startDateTime, endDateTime);
            // TODO: Generate and display report
        }
    }
    
    @FXML
    private void handleExportReport() {
        // TODO: Implement report export functionality
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(ApplicationContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();
            
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            showError("Error", "Could not return to login screen");
        }
    }
    
    @FXML
    private void handleImportExcel() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Import Patients from Excel");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        Stage stage = (Stage) patientsTable.getScene().getWindow();
        java.io.File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            int imported = 0, skipped = 0;
            StringBuilder skippedDetails = new StringBuilder();
            HashSet<String> seenStudentIds = new HashSet<>();
            try (InputStream is = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(is)) {
                Sheet sheet = workbook.getSheetAt(0);
                boolean header = true;
                for (Row row : sheet) {
                    if (header) { header = false; continue; }
                    try {
                        String studentId = getCellString(row, 0);
                        String firstName = getCellString(row, 1);
                        String lastName = getCellString(row, 2);
                        String dob = getCellString(row, 3);
                        String gender = getCellString(row, 4);
                        String grade = getCellString(row, 5);
                        String contact = getCellString(row, 6);
                        String address = getCellString(row, 7);
                        String allergies = getCellString(row, 8);
                        String medHistory = getCellString(row, 9);
                        if (studentId.isBlank() || firstName.isBlank() || lastName.isBlank() || dob.isBlank() || gender.isBlank() || grade.isBlank()) {
                            skipped++;
                            skippedDetails.append("Row ").append(row.getRowNum()+1).append(": Missing required fields.\n");
                            continue;
                        }
                        if (seenStudentIds.contains(studentId)) {
                            skipped++;
                            skippedDetails.append("Row ").append(row.getRowNum()+1).append(": Duplicate student_id in file.\n");
                            continue;
                        }
                        if (patientService.getPatientByStudentId(studentId).isPresent()) {
                            skipped++;
                            skippedDetails.append("Row ").append(row.getRowNum()+1).append(": Student ID already exists.\n");
                            continue;
                        }
                        seenStudentIds.add(studentId);
                        Patient patient = new Patient();
                        patient.setStudentId(studentId);
                        patient.setFirstName(firstName);
                        patient.setLastName(lastName);
                        patient.setDateOfBirth(dob);
                        patient.setGender(gender);
                        patient.setGrade(grade);
                        patient.setContactNumber(contact);
                        patient.setAddress(address);
                        patient.setAllergies(allergies);
                        patient.setMedicalHistory(medHistory);
                        patientService.savePatient(patient);
                        imported++;
                    } catch (Exception ex) {
                        skipped++;
                        skippedDetails.append("Row ").append(row.getRowNum()+1).append(": Error - ").append(ex.getMessage()).append("\n");
                    }
                }
                loadPatients();
                showSuccessDialog(imported + " patients imported. " + (skipped > 0 ? (skipped + " skipped.\n" + skippedDetails) : ""));
            } catch (Exception e) {
                showError("Import Error", "Failed to import: " + e.getMessage());
            }
        }
    }
    
    private String getCellString(Row row, int col) {
        org.apache.poi.ss.usermodel.Cell cell = row.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
            if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                java.util.Date date = cell.getDateCellValue();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(date);
            } else {
                // For numeric (non-date) cells, format as integer if possible
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d)) {
                    // It's an integer value
                    return String.valueOf((long) d);
                } else {
                    // It's a real number, keep as string
                    return String.valueOf(d);
                }
            }
        } else {
            cell.setCellType(org.apache.poi.ss.usermodel.CellType.STRING);
            return cell.getStringCellValue().trim();
        }
    }
    
    @FXML
    private void handleScheduleAppointment() {
        try {
            Patient selectedPatient = appointmentPatientComboBox.getValue();
            LocalDate date = appointmentDatePicker.getValue();
            if (selectedPatient == null || date == null) {
                showError("Validation Error", "Please select a patient and date");
                return;
            }
            String startHour = appointmentStartHourComboBox.getValue();
            String startMinute = appointmentStartMinuteComboBox.getValue();
            String startAmPm = appointmentStartAmPmComboBox.getValue();
            String endHour = appointmentEndHourComboBox.getValue();
            String endMinute = appointmentEndMinuteComboBox.getValue();
            String endAmPm = appointmentEndAmPmComboBox.getValue();
            if (startHour == null || startMinute == null || startAmPm == null || endHour == null || endMinute == null || endAmPm == null) {
                showError("Validation Error", "Please select valid start and end times");
                return;
            }
            // Parse start time
            int startHourValue = Integer.parseInt(startHour);
            if (startAmPm.equals("PM") && startHourValue != 12) startHourValue += 12;
            if (startAmPm.equals("AM") && startHourValue == 12) startHourValue = 0;
            LocalDateTime startTime = date.atTime(startHourValue, Integer.parseInt(startMinute));
            // Parse end time
            int endHourValue = Integer.parseInt(endHour);
            if (endAmPm.equals("PM") && endHourValue != 12) endHourValue += 12;
            if (endAmPm.equals("AM") && endHourValue == 12) endHourValue = 0;
            LocalDateTime endTime = date.atTime(endHourValue, Integer.parseInt(endMinute));
            if (!endTime.isAfter(startTime)) {
                showError("Validation Error", "End time must be after start time");
                return;
            }
            Appointment appointment = new Appointment();
            appointment.setPatient(selectedPatient);
            appointment.setStartTime(startTime);
            appointment.setEndTime(endTime);
            appointment.setPurpose(appointmentPurposeField.getText());
            appointment.setNotes(appointmentNotesField.getText());
            appointment.setStatus("Scheduled");
            // Save appointment
            patientService.addAppointment(selectedPatient.getId(), appointment);
            // Clear form
            appointmentPatientComboBox.setValue(null);
            appointmentDatePicker.setValue(null);
            appointmentStartHourComboBox.setValue(null);
            appointmentStartMinuteComboBox.setValue(null);
            appointmentStartAmPmComboBox.setValue(null);
            appointmentEndHourComboBox.setValue(null);
            appointmentEndMinuteComboBox.setValue(null);
            appointmentEndAmPmComboBox.setValue(null);
            appointmentPurposeField.clear();
            appointmentNotesField.clear();
            // Refresh appointments table
            loadAppointments();
            showSuccessDialog("Appointment scheduled successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to schedule appointment: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleShowReport() {
        LocalDate start = reportStartDate.getValue();
        LocalDate end = reportEndDate.getValue();
        if (start == null || end == null) {
            showError("Validation Error", "Please select both start and end dates.");
            return;
        }
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        List<Appointment> appointments = patientService.getAppointmentsByDateRange(startDateTime, endDateTime);
        // Display count in a dialog
        showSuccessDialog("Appointments from " + start + " to " + end + ": " + appointments.size());
        // Optionally, display in reportTable
        reportTable.getItems().setAll(appointments);
    }
    
    @FXML
    private void showScheduleInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("How to Schedule Appointments");
        alert.setHeaderText(null);
        alert.setContentText("You can schedule clinic appointments with existing patients here! If you don't see the patient, please check if they have been added to the system. Otherwise, add the patient using the 'Add Patient' tab.");
        alert.showAndWait();
    }
    
    private void loadPatients() {
        List<Patient> patients = patientService.getAllPatients();
        System.out.println("[DEBUG] Loaded patients: " + patients.size());
        patientsTable.getItems().setAll(patients);
    }
    
    private void loadAppointments() {
        List<Appointment> appointments = patientService.getAllAppointments();
        appointmentsTable.getItems().setAll(appointments);
    }
    
    private void loadAllAppointments() {
        allAppointments = patientService.getAllAppointments();
        appointmentsTable.getItems().setAll(allAppointments);
        
        // Update cell value factories
        appointmentNoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(appointmentsTable.getItems().indexOf(cellData.getValue()) + 1));
        appointmentDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartTime().toLocalDate().toString()));
        appointmentStartTimeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartTime().toLocalTime().toString()));
        appointmentEndTimeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndTime().toLocalTime().toString()));
        appointmentNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPatient().getFirstName() + " " + cellData.getValue().getPatient().getLastName()));
        appointmentStudentNoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPatient().getStudentId()));
        appointmentPurposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        appointmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showSuccessDialog(String messageText) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Success");
        VBox vbox = new VBox(16);
        vbox.setStyle("-fx-padding: 32; -fx-alignment: center;");
        ImageView checkImage = new ImageView(new javafx.scene.image.Image(getClass().getResource("/check.gif").toExternalForm()));
        checkImage.setFitWidth(80);
        checkImage.setFitHeight(80);
        checkImage.setPreserveRatio(true);
        Label message = new Label(messageText);
        message.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-padding: 12 0 0 0;");
        Button okButton = new Button("Ok");
        okButton.setStyle("-fx-font-size: 15px; -fx-padding: 8 32 8 32; -fx-background-radius: 8; -fx-background-color: #1756a9; -fx-text-fill: white;");
        okButton.setOnAction(e -> dialog.close());
        vbox.getChildren().addAll(checkImage, message, okButton);
        Scene scene = new Scene(vbox);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
    
    private void handleViewPatient(Patient patient) {
        try {
            java.net.URL url = getClass().getResource("/fxml/patient-form.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(ApplicationContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();
            PatientFormController controller = loader.getController();
            controller.setPatient(patient);
            controller.setReadOnly(true);
            Stage stage = new Stage();
            stage.setTitle("View Patient");
            stage.setScene(new Scene(root));
            stage.setWidth(500);
            stage.setHeight(600);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Could not open patient view\n" + e.getMessage());
        }
    }
    
    private void archivePatientAndAppointments(Patient patient) {
        patient.setArchived(true);
        List<Appointment> appointments = patientService.getPatientAppointments(patient.getId());
        for (Appointment appt : appointments) {
            appt.setArchived(true);
            // Save appointment archive status
            patientService.addAppointment(patient.getId(), appt);
        }
        patientService.savePatient(patient);
        loadPatients();
        loadAppointments();
        loadArchivedPatients();
        loadArchivedAppointments();
        showSuccessDialog("Patient and all their appointments archived.");
    }
    
    private void archiveAppointment(Appointment appt) {
        appt.setArchived(true);
        patientService.addAppointment(appt.getPatient().getId(), appt);
        loadAppointments();
        loadArchivedAppointments();
        showSuccessDialog("Appointment archived.");
    }
    
    private void setupArchiveTab() {
        // Archived Patients Table
        archivedPatientStudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        archivedPatientNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));
        archivedPatientGradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        archivedPatientActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button unarchiveButton = new Button("Unarchive");
            {
                unarchiveButton.setMinWidth(80);
                unarchiveButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
                unarchiveButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Unarchive Patient");
                    alert.setHeaderText(null);
                    alert.setContentText("Unarchiving this patient will NOT automatically unarchive their appointments. Proceed?");
                    ButtonType proceed = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
                    ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(proceed, cancel);
                    alert.showAndWait().ifPresent(type -> {
                        if (type == proceed) {
                            patient.setArchived(false);
                            patientService.savePatient(patient);
                            loadArchivedPatients();
                            loadPatients();
                            showSuccessDialog("Patient unarchived.");
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(unarchiveButton);
                }
            }
        });
        // Archived Appointments Table
        archivedAppointmentNoColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        archivedAppointmentDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().toLocalDate().toString()));
        archivedAppointmentStartTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().toLocalTime().toString()));
        archivedAppointmentEndTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().toLocalTime().toString()));
        archivedAppointmentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatient().getFirstName() + " " + cellData.getValue().getPatient().getLastName()));
        archivedAppointmentStudentNoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatient().getStudentId()));
        archivedAppointmentPurposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        archivedAppointmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        archivedAppointmentActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button unarchiveButton = new Button("Unarchive");
            {
                unarchiveButton.setMinWidth(80);
                unarchiveButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
                unarchiveButton.setOnAction(event -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Unarchive Appointment");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want to unarchive this appointment?");
                    ButtonType proceed = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
                    ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(proceed, cancel);
                    alert.showAndWait().ifPresent(type -> {
                        if (type == proceed) {
                            appt.setArchived(false);
                            patientService.addAppointment(appt.getPatient().getId(), appt);
                            loadArchivedAppointments();
                            loadAppointments();
                            showSuccessDialog("Appointment unarchived.");
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(unarchiveButton);
                }
            }
        });
        // Add listeners for filtering
        if (archivedPatientFilterField != null) {
            archivedPatientFilterField.textProperty().addListener((obs, oldVal, newVal) -> filterArchivedPatients(newVal));
        }
        if (archivedAppointmentFilterField != null) {
            archivedAppointmentFilterField.textProperty().addListener((obs, oldVal, newVal) -> filterArchivedAppointments(newVal));
        }
        if (archivedAppointmentDateFilter != null) {
            archivedAppointmentDateFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterArchivedAppointments(archivedAppointmentFilterField != null ? archivedAppointmentFilterField.getText() : ""));
        }
    }
    
    private void loadArchivedPatients() {
        allArchivedPatients = patientService.getArchivedPatients();
        filterArchivedPatients(archivedPatientFilterField != null ? archivedPatientFilterField.getText() : "");
    }
    
    private void loadArchivedAppointments() {
        allArchivedAppointments = patientService.getArchivedAppointments();
        filterArchivedAppointments(archivedAppointmentFilterField != null ? archivedAppointmentFilterField.getText() : "");
    }
    
    private void filterArchivedPatients(String filter) {
        if (filter == null || filter.isBlank()) {
            archivedPatientsTable.getItems().setAll(allArchivedPatients);
        } else {
            String lower = filter.toLowerCase();
            archivedPatientsTable.getItems().setAll(
                allArchivedPatients.stream().filter(p ->
                    p.getFirstName().toLowerCase().contains(lower) ||
                    p.getLastName().toLowerCase().contains(lower)
                ).toList()
            );
        }
    }
    
    private void filterArchivedAppointments(String filter) {
        String lower = filter == null ? "" : filter.toLowerCase();
        java.time.LocalDate date = archivedAppointmentDateFilter != null ? archivedAppointmentDateFilter.getValue() : null;
        archivedAppointmentsTable.getItems().setAll(
            allArchivedAppointments.stream().filter(a -> {
                boolean nameMatch = lower.isBlank() ||
                    a.getPatient().getFirstName().toLowerCase().contains(lower) ||
                    a.getPatient().getLastName().toLowerCase().contains(lower);
                boolean dateMatch = date == null || (a.getStartTime() != null && a.getStartTime().toLocalDate().equals(date));
                return nameMatch && dateMatch;
            }).toList()
        );
    }
} 