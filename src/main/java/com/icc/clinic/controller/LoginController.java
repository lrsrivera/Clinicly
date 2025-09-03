package com.icc.clinic.controller;

import com.icc.clinic.model.User;
import com.icc.clinic.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import com.icc.clinic.config.ApplicationContextProvider;

@Controller
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @Autowired
    private UserService userService;
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }
        
        userService.authenticateUser(username, password).ifPresentOrElse(
            user -> {
                try {
                    // Load main application view
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                    loader.setControllerFactory(ApplicationContextProvider.getApplicationContext()::getBean);
                    Parent root = loader.load();
                    
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Clinic Management System - Welcome " + user.getUsername());
                } catch (Exception e) {
                    e.printStackTrace(); // Print the real error to the console
                    errorLabel.setText("Error loading main application");
                }
            },
            () -> errorLabel.setText("Invalid username or password")
        );
    }
} 