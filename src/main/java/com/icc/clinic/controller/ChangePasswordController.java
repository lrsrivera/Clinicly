package com.icc.clinic.controller;

import com.icc.clinic.model.User;
import com.icc.clinic.service.PasswordService;
import com.icc.clinic.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ChangePasswordController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private javafx.scene.control.CheckBox ruleLen;
    @FXML private javafx.scene.control.CheckBox ruleLower;
    @FXML private javafx.scene.control.CheckBox ruleUpper;
    @FXML private javafx.scene.control.CheckBox ruleDigit;
    @FXML private javafx.scene.control.CheckBox ruleSpecial;
    @FXML private javafx.scene.control.CheckBox ruleMatch;
    @FXML private javafx.scene.control.CheckBox ruleNotDefault;
    @FXML private javafx.scene.control.Button saveButton;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserService userService;

    private User currentUser;
    private boolean requireCurrentPassword;

    public void setContext(User user, boolean requireCurrentPassword) {
        this.currentUser = user;
        this.requireCurrentPassword = requireCurrentPassword;
        currentPasswordField.setDisable(!requireCurrentPassword);
    }

    @FXML
    private void handleLiveValidate() {
        String next = newPasswordField.getText() == null ? "" : newPasswordField.getText();
        String confirm = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();
        boolean hasLower = next.matches(".*[a-z].*");
        boolean hasUpper = next.matches(".*[A-Z].*");
        boolean hasDigit = next.matches(".*\\d.*");
        boolean hasSpecial = next.matches(".*[@$!%*?&].*");
        ruleLen.setSelected(next.length() >= 8);
        ruleLower.setSelected(hasLower);
        ruleUpper.setSelected(hasUpper);
        ruleDigit.setSelected(hasDigit);
        ruleSpecial.setSelected(hasSpecial);
        ruleMatch.setSelected(!next.isEmpty() && next.equals(confirm));
        ruleNotDefault.setSelected(!passwordService.isDefaultPassword(next));
        boolean allOk = ruleLen.isSelected() && ruleLower.isSelected() && ruleUpper.isSelected() && ruleDigit.isSelected() && ruleSpecial.isSelected() && ruleMatch.isSelected() && ruleNotDefault.isSelected();
        saveButton.setDisable(!allOk);
        if (!allOk) {
            messageLabel.setText(passwordService.getPasswordStrengthDescription(next));
        } else {
            messageLabel.setText("");
        }
    }

    @FXML
    private void handleChangePassword() {
        String current = currentPasswordField.getText();
        String next = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (next == null || next.isBlank()) {
            messageLabel.setText("Enter a new password.");
            return;
        }
        if (!next.equals(confirm)) {
            messageLabel.setText("New password and confirmation do not match.");
            return;
        }
        if (!passwordService.isValidPassword(next) || passwordService.isDefaultPassword(next)) {
            messageLabel.setText(passwordService.getPasswordStrengthDescription(next));
            return;
        }

        boolean ok;
        if (requireCurrentPassword) {
            ok = passwordService.changePassword(currentUser, current, next);
        } else {
            ok = passwordService.selfServicePasswordReset(currentUser, next);
        }

        if (!ok) {
            messageLabel.setText("Unable to change password. Check current password and strength.");
            return;
        }

        // Persist updated user details
        try {
            userService.saveUser(currentUser);
        } catch (Exception persistEx) {
            messageLabel.setText("Failed to save new password: " + persistEx.getMessage());
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            loader.setControllerFactory(com.icc.clinic.config.ApplicationContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setCurrentUser(currentUser);

            Stage stage = (Stage) messageLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.setTitle("Clinic Management System");
            stage.centerOnScreen();
        } catch (Exception e) {
            messageLabel.setText("Error loading main view: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) messageLabel.getScene().getWindow()).close();
    }
}


