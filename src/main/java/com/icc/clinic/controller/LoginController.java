package com.icc.clinic.controller;

import com.icc.clinic.service.UserService;
import com.icc.clinic.service.PasswordService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.icc.clinic.config.ApplicationContextProvider;

@Controller
public class LoginController {

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("IT", "Nurse");
        roleComboBox.setEditable(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String selectedRole = roleComboBox.getValue();

        if (selectedRole == null) {
            showAlert("Please select role.");
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        System.out.println("Login attempt: username=" + username + ", password=" + password + ", role=" + selectedRole);
        userService.authenticateUser(username, password).ifPresentOrElse(
                user -> {
                    System.out.println("Authentication successful for user: " + user.getUsername());
                    //Check if the user’s actual role matches the selected role
                    String actualRole = user.getRole() != null ? user.getRole().getName() : null;

                    if (actualRole == null || !actualRole.equalsIgnoreCase(selectedRole)) {
                        showAlert("You do not have permission to log in as " + selectedRole + ".");
                        return;
                    }

                    boolean needsChange = passwordService.needsPasswordChange(user);
                    boolean enteredDefault = passwordService.isDefaultPassword(password);
                    boolean enteredWeak = !passwordService.isValidPassword(password);

                    if (needsChange || enteredDefault || enteredWeak) {
                        // Redirect to Change Password screen
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/change-password.fxml"));
                            loader.setControllerFactory(ApplicationContextProvider.getApplicationContext()::getBean);
                            Parent root = loader.load();
                            ChangePasswordController controller = loader.getController();
                            // If the user just logged in with a non-default but weak password, require current password entry
                            boolean requireCurrent = !needsChange && !enteredDefault;
                            controller.setContext(user, requireCurrent);
                            Stage stage = (Stage) usernameField.getScene().getWindow();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.setWidth(520);
                            stage.setHeight(720);
                            stage.setTitle("Change Password");
                            stage.centerOnScreen();
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorLabel.setText("Error loading Change Password screen");
                        }
                        return;
                    }

                    try {
                        // Store current user in session for audit logging
                        ApplicationContextProvider.getApplicationContext()
                                .getBean(com.icc.clinic.service.SessionService.class)
                                .setCurrentUser(user);

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                        loader.setControllerFactory(ApplicationContextProvider.getApplicationContext()::getBean);
                        Parent root = loader.load();
                        MainController mainController = loader.getController();
                        mainController.setCurrentUser(user);
                        Stage stage = (Stage) usernameField.getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.setTitle("Clinic Management System - Welcome " + user.getFirstName() + " " + user.getLastName());
                        stage.setWidth(1200);
                        stage.setHeight(800);
                        stage.centerOnScreen();
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorLabel.setText("Error loading main application");
                    }
                },
                () -> {
                    System.out.println("Authentication failed for username: " + username);
                    errorLabel.setText("Invalid username or password");
                }
        );
    }

    // Alert popup method
    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Login Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
