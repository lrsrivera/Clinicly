package com.icc.clinic.controller;

import com.icc.clinic.model.Role;
import com.icc.clinic.model.User;
import com.icc.clinic.service.RBACService;
import com.icc.clinic.service.UserService;
import com.icc.clinic.service.PasswordService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserManagementController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Boolean> activeColumn;

    @FXML private TextField usernameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private CheckBox activeCheckBox;

    @FXML private Button addUserButton;
    @FXML private Button editUserButton;
    @FXML private Button deleteUserButton;
    @FXML private Button refreshButton;
    @FXML private Button saveUserButton;
    // Logs UI
    @FXML private javafx.scene.control.TableView<com.icc.clinic.model.AdminActionLog> logsTable;
    @FXML private javafx.scene.control.TableColumn<com.icc.clinic.model.AdminActionLog, String> logTimeColumn;
    @FXML private javafx.scene.control.TableColumn<com.icc.clinic.model.AdminActionLog, String> logAdminColumn;
    @FXML private javafx.scene.control.TableColumn<com.icc.clinic.model.AdminActionLog, String> logTargetColumn;
    @FXML private javafx.scene.control.TableColumn<com.icc.clinic.model.AdminActionLog, String> logActionColumn;
    @FXML private javafx.scene.control.TableColumn<com.icc.clinic.model.AdminActionLog, String> logDetailsColumn;
    @FXML private javafx.scene.control.DatePicker logStartDate;
    @FXML private javafx.scene.control.DatePicker logEndDate;
    
    @FXML private Label roleDescriptionLabel;
    
    private boolean isEditMode = false;

    @Autowired
    private UserService userService;

    @Autowired
    private RBACService rbacService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private com.icc.clinic.service.AdminActionLogService adminActionLogService;

    @Autowired
    private com.icc.clinic.service.SessionService sessionService;

    private User selectedUser;
    private User currentUser;
    
    // Method to set current user (called from MainController)
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("UserManagementController: Current user set to: " + (user != null ? user.getUsername() : "NULL"));
    }
    
    // Method to be called when the User Management tab is selected
    public void onTabSelected() {
        System.out.println("UserManagementController: Tab selected, current user is: " + (currentUser != null ? currentUser.getUsername() : "NULL"));
        if (currentUser != null && logsTable != null) {
            logsTable.getItems().setAll(adminActionLogService.getAll());
        }
    }

    @FXML
    public void initialize() {
        setupTable();
        setupComboBoxes();
        loadUsers();
        loadRoles();
        setupFormFields();
        updateButtonStates();
        setupLogsTable();
    }

    private void setupTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                user.getRole() != null ? user.getRole().getName() : "No Role"
            );
        });
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Add selection listener
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedUser = newSelection;
            if (newSelection != null) {
                populateForm(newSelection);
            } else {
                clearForm();
            }
        });
        
        // Add role selection listener for real-time feedback
        roleComboBox.valueProperty().addListener((obs, oldRole, newRole) -> {
            updateRoleDescription(newRole);
            // Show temporary password indicator when role is selected
            if (newRole != null && !isEditMode) {
                passwordField.setText("temporary/default");
                passwordField.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
                updatePasswordTooltip(newRole.getName());
            }
        });
    }

    private void setupFormFields() {
        // Initially disable all form fields
        setFormFieldsEnabled(false);
    }
    
    private void setFormFieldsEnabled(boolean enabled) {
        usernameField.setDisable(!enabled);
        firstNameField.setDisable(!enabled);
        lastNameField.setDisable(!enabled);
        emailField.setDisable(!enabled);
        // Password field is always read-only for new users, editable only when editing existing users
        if (isEditMode) {
            passwordField.setDisable(!enabled);
        } else {
            passwordField.setDisable(true); // Always disabled for new users
        }
        roleComboBox.setDisable(!enabled);
        activeCheckBox.setDisable(!enabled);
    }
    
    private void updateButtonStates() {
        if (isEditMode) {
            // Edit mode: Show Save button, hide Edit button
            editUserButton.setVisible(false);
            saveUserButton.setVisible(true);
            addUserButton.setDisable(true);
            deleteUserButton.setDisable(true);
        } else {
            // Normal mode: Show Edit button, hide Save button
            editUserButton.setVisible(true);
            saveUserButton.setVisible(false);
            addUserButton.setDisable(false);
            deleteUserButton.setDisable(selectedUser == null);
        }
    }

    private void setupComboBoxes() {
        roleComboBox.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role role) {
                if (role == null) return "";
                return role.getName() + " - " + role.getDescription();
            }

            @Override
            public Role fromString(String string) {
                return null;
            }
        });
        
        // Add cell factory for better display
        roleComboBox.setCellFactory(listView -> new ListCell<Role>() {
            @Override
            protected void updateItem(Role role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(role.getName() + " - " + role.getDescription());
                    // Add some styling based on role
                    if ("IT".equals(role.getName())) {
                        setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2;");
                    } else if ("NURSE".equals(role.getName())) {
                        setStyle("-fx-background-color: #f3e5f5; -fx-text-fill: #7b1fa2;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        System.out.println("[UserManagementController] Loaded " + users.size() + " users");
        for (User user : users) {
            System.out.println("[UserManagementController] User: " + user.getUsername() + 
                ", FirstName: " + user.getFirstName() + 
                ", LastName: " + user.getLastName() + 
                ", Email: " + user.getEmail());
        }
        usersTable.getItems().setAll(users);
    }

    private void setupLogsTable() {
        if (logsTable == null) return; // logs tab not visible in some contexts
        logTimeColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
            cd.getValue().getTimestamp().toString()));
        logAdminColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
            cd.getValue().getAdminUser() != null ? cd.getValue().getAdminUser().getUsername() : ""));
        logTargetColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
            cd.getValue().getTargetUser() != null ? cd.getValue().getTargetUser().getUsername() : ""));
        logActionColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getAction()));
        logDetailsColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
            cd.getValue().getDetails() == null ? "" : cd.getValue().getDetails()));
        
        // Add double-click functionality
        logsTable.setRowFactory(tv -> {
            javafx.scene.control.TableRow<com.icc.clinic.model.AdminActionLog> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleViewFullDetails();
                }
            });
            return row;
        });
        
        logsTable.getItems().setAll(adminActionLogService.getAll());
    }

    private void loadRoles() {
        List<Role> roles = rbacService.getAllRoles();
        roleComboBox.getItems().setAll(roles);
    }

    private void populateForm(User user) {
        usernameField.setText(user.getUsername());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        passwordField.clear(); // Don't populate password for security
        roleComboBox.setValue(user.getRole());
        activeCheckBox.setSelected(user.getActive());
        
        // Update role description when user is selected
        updateRoleDescription(user.getRole());
        
        // Update button states
        updateButtonStates();
    }
    
    private void updateRoleDescription(Role role) {
        if (role != null && roleDescriptionLabel != null) {
            String description = getDetailedRoleDescription(role);
            roleDescriptionLabel.setText(description);
            
            // Update styling based on role
            if ("IT".equals(role.getName())) {
                roleDescriptionLabel.setStyle("-fx-text-fill: #1976d2; -fx-font-weight: bold;");
            } else if ("NURSE".equals(role.getName())) {
                roleDescriptionLabel.setStyle("-fx-text-fill: #7b1fa2; -fx-font-weight: bold;");
            } else {
                roleDescriptionLabel.setStyle("-fx-text-fill: #333;");
            }
        }
    }
    
    private String getDetailedRoleDescription(Role role) {
        if (role == null) return "Select a role to see details";
        
        switch (role.getName()) {
            case "IT":
                return "IT Administrator - Full system access including user management, system configuration, and all clinical operations";
            case "NURSE":
                return "Nurse - Clinical operations including patient management, appointment scheduling, and data import/export";
            default:
                return role.getDescription();
        }
    }
    
    private void updatePasswordTooltip(String roleName) {
        String defaultPassword = passwordService.getDefaultPasswordForRole(roleName);
        String tooltipText = String.format(
            "Default password: %s\n\n" +
            "⚠️ User must change password on first login\n" +
            "Password requirements:\n" +
            "• At least 8 characters\n" +
            "• Uppercase and lowercase letters\n" +
            "• Numbers and special characters (@$!%%*?&)\n" +
            "• Cannot be the default password",
            defaultPassword
        );
        
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setStyle("-fx-font-size: 11px; -fx-max-width: 300px;");
        passwordField.setTooltip(tooltip);
    }

    private void clearForm() {
        usernameField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
        passwordField.setStyle(""); // Reset styling
        passwordField.setTooltip(null); // Clear tooltip
        roleComboBox.setValue(null);
        activeCheckBox.setSelected(true);
        selectedUser = null;
        isEditMode = false;
        
        // Disable form fields and update button states
        setFormFieldsEnabled(false);
        updateButtonStates();
        
        // Clear role description
        if (roleDescriptionLabel != null) {
            roleDescriptionLabel.setText("Select a role to see details");
            roleDescriptionLabel.setStyle("-fx-text-fill: #333;");
        }
    }

    @FXML
    private void handleAddUser() {
        // Prepare form for creating a new user
        clearForm();
        selectedUser = null;

        // Enter save mode so the Save button is visible
        isEditMode = true;
        setFormFieldsEnabled(true);

        // Password for new users is auto-assigned; keep field disabled and show placeholder
        passwordField.setDisable(true);
        passwordField.setText("temporary/default");
        passwordField.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
        updatePasswordTooltip("NURSE");

        updateButtonStates();

        // Focus on username field
        usernameField.requestFocus();
    }

    @FXML
    private void handleEditUser() {
        if (selectedUser == null) {
            showErrorMessage("Please select a user to edit.");
            return;
        }

        // Enable edit mode
        isEditMode = true;
        setFormFieldsEnabled(true);
        updateButtonStates();
        
        // Focus on first name field
        firstNameField.requestFocus();
    }

    @FXML
    private void handleSaveUser() {
        if (validateForm()) {
            try {
                if (isEditMode && selectedUser != null) {
                    // Store old values for logging
                    String oldUsername = selectedUser.getUsername();
                    String oldFirstName = selectedUser.getFirstName();
                    String oldLastName = selectedUser.getLastName();
                    String oldEmail = selectedUser.getEmail();
                    String oldRole = selectedUser.getRole() != null ? selectedUser.getRole().getName() : "None";
                    boolean oldActive = selectedUser.getActive();
                    
                    // Editing existing user
                    selectedUser.setUsername(usernameField.getText());
                    selectedUser.setFirstName(firstNameField.getText());
                    selectedUser.setLastName(lastNameField.getText());
                    selectedUser.setEmail(emailField.getText());
                    if (!passwordField.getText().isEmpty()) {
                        selectedUser.setPassword(passwordField.getText());
                    }
                    selectedUser.setRole(roleComboBox.getValue());
                    selectedUser.setActive(activeCheckBox.isSelected());

                    userService.saveUser(selectedUser);
                    
                    // Log the update action with better formatting
                    StringBuilder details = new StringBuilder();
                    details.append("User Updated:\n");
                    if (!oldUsername.equals(selectedUser.getUsername())) {
                        details.append("• Username: ").append(oldUsername).append(" → ").append(selectedUser.getUsername()).append("\n");
                    }
                    if (!oldFirstName.equals(selectedUser.getFirstName())) {
                        details.append("• First Name: ").append(oldFirstName).append(" → ").append(selectedUser.getFirstName()).append("\n");
                    }
                    if (!oldLastName.equals(selectedUser.getLastName())) {
                        details.append("• Last Name: ").append(oldLastName).append(" → ").append(selectedUser.getLastName()).append("\n");
                    }
                    if (!oldEmail.equals(selectedUser.getEmail())) {
                        details.append("• Email: ").append(oldEmail).append(" → ").append(selectedUser.getEmail()).append("\n");
                    }
                    if (!oldRole.equals(selectedUser.getRole().getName())) {
                        details.append("• Role: ").append(oldRole).append(" → ").append(selectedUser.getRole().getName()).append("\n");
                    }
                    if (oldActive != selectedUser.getActive()) {
                        details.append("• Active Status: ").append(oldActive).append(" → ").append(selectedUser.getActive()).append("\n");
                    }
                    
                    // Choose action user from session if needed
                    User actionUser = currentUser != null ? currentUser : sessionService.getCurrentUser();
                    System.out.println("Logging UPDATE_USER - Current User: " + (actionUser != null ? actionUser.getUsername() : "NULL"));
                    adminActionLogService.log(actionUser, selectedUser, "UPDATE_USER", details.toString());
                    
                    loadUsers();
                    showSuccessMessage("User updated successfully!");
                } else {
                    // Adding new user
                    User user = new User();
                    user.setUsername(usernameField.getText());
                    user.setFirstName(firstNameField.getText());
                    user.setLastName(lastNameField.getText());
                    user.setEmail(emailField.getText());
                    // Use actual default password for the role
                    String actualPassword = passwordService.getDefaultPasswordForRole(roleComboBox.getValue().getName());
                    user.setPassword(actualPassword);
                    user.setRole(roleComboBox.getValue());
                    user.setActive(activeCheckBox.isSelected());
                    
                    // Set default password flag
                    user.setIsDefaultPassword(true);
                    if (user.getIsDefaultPassword()) {
                        user.setPasswordChangedAt(null);
                    } else {
                        user.setPasswordChangedAt(java.time.LocalDateTime.now());
                    }

                    userService.saveUser(user);
                    
                    // Log the create action with better formatting
                    String details = String.format("New User Created:\n• Name: %s %s\n• Username: %s\n• Role: %s\n• Active: %s", 
                        user.getFirstName(), user.getLastName(), user.getUsername(), 
                        user.getRole().getName(), user.getActive());
                    
                    // Choose action user from session if needed
                    User actionUser = currentUser != null ? currentUser : sessionService.getCurrentUser();
                    System.out.println("Logging CREATE_USER - Current User: " + (actionUser != null ? actionUser.getUsername() : "NULL"));
                    adminActionLogService.log(actionUser, user, "CREATE_USER", details);
                    
                    loadUsers();
                    showSuccessMessage("User added successfully!");
                }
                
                // Exit edit mode
                isEditMode = false;
                setFormFieldsEnabled(false);
                updateButtonStates();
                
                // Refresh logs if available
                if (logsTable != null) logsTable.getItems().setAll(adminActionLogService.getAll());
                
            } catch (Exception e) {
                showErrorMessage("Error saving user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDeleteUser() {
        if (selectedUser == null) {
            showErrorMessage("Please select a user to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete user: " + selectedUser.getUsername() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Store user info for logging before deletion
                    String deletedUserInfo = String.format("User Deleted:\n• Name: %s %s\n• Username: %s\n• Role: %s", 
                        selectedUser.getFirstName(), selectedUser.getLastName(), 
                        selectedUser.getUsername(), 
                        selectedUser.getRole() != null ? selectedUser.getRole().getName() : "None");
                    
                    userService.deleteUser(selectedUser.getId());
                    
                    // Choose action user from session if needed
                    User actionUser = currentUser != null ? currentUser : sessionService.getCurrentUser();
                    System.out.println("Logging DELETE_USER - Current User: " + (actionUser != null ? actionUser.getUsername() : "NULL"));
                    adminActionLogService.log(actionUser, selectedUser, "DELETE_USER", deletedUserInfo);
                    
                    loadUsers();
                    clearForm();
                    showSuccessMessage("User deleted successfully!");
                    
                    // Refresh logs if available
                    if (logsTable != null) logsTable.getItems().setAll(adminActionLogService.getAll());
                } catch (Exception e) {
                    showErrorMessage("Error deleting user: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
        usersTable.refresh();
        loadRoles();
    }
    
    @FXML
    private void handleFixEncryption() {
        try {
            userService.forceFixItAdminEncryption();
            loadUsers(); // Reload to show the fix
            showSuccessMessage("Encryption fix applied! Please refresh the view.");
        } catch (Exception e) {
            showErrorMessage("Error fixing encryption: " + e.getMessage());
        }
    }

    @FXML
    private void handleResetPassword() {
        if (selectedUser == null) {
            showErrorMessage("Please select a user to reset.");
            return;
        }

        // Combined dialog: username + password in one window
        Dialog<ButtonType> authDialog = new Dialog<>();
        authDialog.setTitle("Confirm IT Identity");
        authDialog.setHeaderText("Enter your IT username and password to proceed");
        ButtonType confirmType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        authDialog.getDialogPane().getButtonTypes().addAll(confirmType, ButtonType.CANCEL);

        TextField itUserField = new TextField();
        String defaultUser = currentUser != null ? currentUser.getUsername() :
            (sessionService.getCurrentUser() != null ? sessionService.getCurrentUser().getUsername() : "");
        itUserField.setText(defaultUser);
        itUserField.setPromptText("IT Username");
        PasswordField itPwdField = new PasswordField();
        itPwdField.setPromptText("IT Password");

        VBox form = new VBox(8, new Label("Username:"), itUserField, new Label("Password:"), itPwdField);
        authDialog.getDialogPane().setContent(form);

        var result = authDialog.showAndWait();
        if (result.isEmpty() || result.get() != confirmType) return;

        String itUsername = itUserField.getText() != null ? itUserField.getText().trim() : "";
        String itPassword = itPwdField.getText();
        if (itUsername.isEmpty() || itPassword == null || itPassword.isEmpty()) {
            showErrorMessage("Please enter both IT username and password.");
            return;
        }

        var auth = userService.authenticateUser(itUsername, itPassword);
        if (auth.isEmpty() || !rbacService.userHasRole(auth.get(), "IT")) {
            showErrorMessage("Authentication failed. Only IT can reset passwords.");
            return;
        }

        try {
            passwordService.resetPasswordToDefault(selectedUser);
            userService.saveUser(selectedUser);
            
            String details = String.format("Password Reset:\n• User: %s %s (%s)\n• Role: %s\n• New Password: %s", 
                selectedUser.getFirstName(), selectedUser.getLastName(), selectedUser.getUsername(),
                selectedUser.getRole().getName(), passwordService.getDefaultPasswordForRole(selectedUser.getRole().getName()));
            
            // Debug logging
            // Prefer session user if available
            User actionUser = currentUser != null ? currentUser : sessionService.getCurrentUser();
            System.out.println("Logging RESET_PASSWORD - Current User: " + (actionUser != null ? actionUser.getUsername() : "NULL"));
            adminActionLogService.log(actionUser, selectedUser, "RESET_PASSWORD", details);
            if (logsTable != null) logsTable.getItems().setAll(adminActionLogService.getAll());
            showSuccessMessage("Password reset to default. User must change it on next login.");
        } catch (Exception ex) {
            showErrorMessage("Failed to reset password: " + ex.getMessage());
        }
    }

    @FXML
    private void handleFilterLogs() {
        if (logStartDate == null || logEndDate == null || logsTable == null) return;
        java.time.LocalDate s = logStartDate.getValue();
        java.time.LocalDate e = logEndDate.getValue();
        if (s == null || e == null) {
            logsTable.getItems().setAll(adminActionLogService.getAll());
            return;
        }
        logsTable.getItems().setAll(adminActionLogService.getBetween(s.atStartOfDay(), e.atTime(23,59,59)));
    }

    @FXML
    private void handleRefreshLogs() {
        if (logsTable != null) logsTable.getItems().setAll(adminActionLogService.getAll());
    }
    
    // Intentionally no UI or handler to clear logs; logs are immutable for audit purposes
    
    @FXML
    private void handleViewFullDetails() {
        if (logsTable == null) return;
        
        com.icc.clinic.model.AdminActionLog selectedLog = logsTable.getSelectionModel().getSelectedItem();
        if (selectedLog == null) {
            showErrorMessage("Please select a log entry to view details.");
            return;
        }
        
        // Create a dialog to show full details
        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Full Log Details");
        dialog.setHeaderText("Complete Details for Log Entry");
        
        // Create content
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.setPadding(new javafx.geometry.Insets(15));
        
        // Add detailed information
        javafx.scene.control.Label timestampLabel = new javafx.scene.control.Label("Timestamp: " + selectedLog.getTimestamp().toString());
        javafx.scene.control.Label adminLabel = new javafx.scene.control.Label("Admin User: " + (selectedLog.getAdminUser() != null ? selectedLog.getAdminUser().getUsername() : "Unknown"));
        javafx.scene.control.Label targetLabel = new javafx.scene.control.Label("Target User: " + (selectedLog.getTargetUser() != null ? selectedLog.getTargetUser().getUsername() : "N/A"));
        javafx.scene.control.Label actionLabel = new javafx.scene.control.Label("Action: " + selectedLog.getAction());
        
        javafx.scene.control.Label detailsLabel = new javafx.scene.control.Label("Details:");
        detailsLabel.setStyle("-fx-font-weight: bold;");
        
        javafx.scene.control.TextArea detailsArea = new javafx.scene.control.TextArea(selectedLog.getDetails() != null ? selectedLog.getDetails() : "No details available");
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefRowCount(8);
        detailsArea.setPrefColumnCount(60);
        
        content.getChildren().addAll(timestampLabel, adminLabel, targetLabel, actionLabel, detailsLabel, detailsArea);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
        
        // Style the dialog
        dialog.getDialogPane().setStyle("-fx-background-color: #f4f4f4;");
        
        dialog.showAndWait();
    }

    private boolean validateForm() {
        if (usernameField.getText().trim().isEmpty()) {
            showErrorMessage("Username is required.");
            return false;
        }
        if (firstNameField.getText().trim().isEmpty()) {
            showErrorMessage("First name is required.");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            showErrorMessage("Last name is required.");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showErrorMessage("Email is required.");
            return false;
        }
        if (roleComboBox.getValue() == null) {
            showErrorMessage("Role is required.");
            return false;
        }
        // Password validation is handled automatically for new users

        // Check for duplicate username
        if (selectedUser == null || !selectedUser.getUsername().equals(usernameField.getText())) {
            if (userService.userExists(usernameField.getText())) {
                showErrorMessage("Username already exists.");
                return false;
            }
        }

        // Check for duplicate email
        if (selectedUser == null || !selectedUser.getEmail().equals(emailField.getText())) {
            if (userService.emailExists(emailField.getText())) {
                showErrorMessage("Email already exists.");
                return false;
            }
        }

        return true;
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
