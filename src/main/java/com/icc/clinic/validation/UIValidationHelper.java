package com.icc.clinic.validation;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for UI validation feedback
 */
public class UIValidationHelper {

    private static final String ERROR_STYLE = "-fx-border-color: #ff4444; -fx-border-width: 2px;";
    private static final String SUCCESS_STYLE = "-fx-border-color: #44ff44; -fx-border-width: 1px;";
    private static final String NORMAL_STYLE = "-fx-border-color: #cccccc; -fx-border-width: 1px;";

    private Map<Control, Label> errorLabels = new HashMap<>();

    /**
     * Shows validation errors for a control
     */
    public void showError(Control control, String errorMessage) {
        // Set error styling
        control.setStyle(ERROR_STYLE);
        
        // Create or update error label
        Label errorLabel = errorLabels.get(control);
        if (errorLabel == null) {
            errorLabel = new Label();
            errorLabel.setTextFill(Color.RED);
            errorLabel.setStyle("-fx-font-size: 10px;");
            errorLabels.put(control, errorLabel);
        }
        
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

    /**
     * Shows success state for a control
     */
    public void showSuccess(Control control) {
        control.setStyle(SUCCESS_STYLE);
        
        Label errorLabel = errorLabels.get(control);
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    /**
     * Clears validation state for a control
     */
    public void clearValidation(Control control) {
        control.setStyle(NORMAL_STYLE);
        
        Label errorLabel = errorLabels.get(control);
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    /**
     * Clears all validation states
     */
    public void clearAllValidation() {
        for (Control control : errorLabels.keySet()) {
            clearValidation(control);
        }
    }

    /**
     * Validates a text field for required input
     */
    public boolean validateRequired(TextField field, String fieldName) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            showError(field, fieldName + " is required");
            return false;
        } else {
            showSuccess(field);
            return true;
        }
    }

    /**
     * Validates a password field for required input
     */
    public boolean validateRequired(PasswordField field, String fieldName) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            showError(field, fieldName + " is required");
            return false;
        } else {
            showSuccess(field);
            return true;
        }
    }

    /**
     * Validates a combo box for required selection
     */
    public boolean validateRequired(ComboBox<?> field, String fieldName) {
        if (field.getValue() == null) {
            showError(field, fieldName + " is required");
            return false;
        } else {
            showSuccess(field);
            return true;
        }
    }

    /**
     * Validates a date picker for required selection
     */
    public boolean validateRequired(DatePicker field, String fieldName) {
        if (field.getValue() == null) {
            showError(field, fieldName + " is required");
            return false;
        } else {
            showSuccess(field);
            return true;
        }
    }

    /**
     * Validates text field length
     */
    public boolean validateLength(TextField field, String fieldName, int minLength, int maxLength) {
        if (field.getText() != null) {
            int length = field.getText().trim().length();
            if (length < minLength) {
                showError(field, fieldName + " must be at least " + minLength + " characters");
                return false;
            }
            if (length > maxLength) {
                showError(field, fieldName + " must be no more than " + maxLength + " characters");
                return false;
            }
        }
        showSuccess(field);
        return true;
    }

    /**
     * Validates text area length
     */
    public boolean validateLength(TextArea field, String fieldName, int minLength, int maxLength) {
        if (field.getText() != null) {
            int length = field.getText().trim().length();
            if (length < minLength) {
                showError(field, fieldName + " must be at least " + minLength + " characters");
                return false;
            }
            if (length > maxLength) {
                showError(field, fieldName + " must be no more than " + maxLength + " characters");
                return false;
            }
        }
        showSuccess(field);
        return true;
    }

    /**
     * Validates email format
     */
    public boolean validateEmail(TextField field, String fieldName) {
        if (field.getText() != null && !field.getText().trim().isEmpty()) {
            String email = field.getText().trim();
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                showError(field, fieldName + " format is invalid");
                return false;
            }
        }
        showSuccess(field);
        return true;
    }

    /**
     * Validates phone number format
     */
    public boolean validatePhone(TextField field, String fieldName) {
        if (field.getText() != null && !field.getText().trim().isEmpty()) {
            String phone = field.getText().trim();
            if (!phone.matches("^[+]?[0-9\\s\\-\\(\\)]{7,15}$")) {
                showError(field, fieldName + " format is invalid");
                return false;
            }
        }
        showSuccess(field);
        return true;
    }

    /**
     * Validates student ID format
     */
    public boolean validateStudentId(TextField field, String fieldName) {
        if (field.getText() != null && !field.getText().trim().isEmpty()) {
            String studentId = field.getText().trim();
            if (!studentId.matches("^[a-zA-Z0-9\\-]{3,20}$")) {
                showError(field, fieldName + " must be 3-20 characters (letters, numbers, hyphens only)");
                return false;
            }
        }
        showSuccess(field);
        return true;
    }

    /**
     * Validates name format (letters, spaces, hyphens, apostrophes)
     */
    public boolean validateName(TextField field, String fieldName) {
        if (field.getText() != null && !field.getText().trim().isEmpty()) {
            String name = field.getText().trim();
            if (!name.matches("^[a-zA-Z\\s\\-'\\u00C0-\\u017F]+$")) {
                showError(field, fieldName + " must contain only letters, spaces, hyphens, and apostrophes");
                return false;
            }
        }
        showSuccess(field);
        return true;
    }

    /**
     * Validates alphanumeric format (letters, numbers, underscore)
     */
    public boolean validateAlphanumeric(TextField field, String fieldName) {
        if (field.getText() != null && !field.getText().trim().isEmpty()) {
            String value = field.getText().trim();
            if (!value.matches("^[a-zA-Z0-9_]+$")) {
                showError(field, fieldName + " must contain only letters, numbers, and underscores");
                return false;
            }
        }
        showSuccess(field);
        return true;
    }

    /**
     * Gets the error label for a control
     */
    public Label getErrorLabel(Control control) {
        return errorLabels.get(control);
    }

    /**
     * Checks if a control has validation errors
     */
    public boolean hasError(Control control) {
        Label errorLabel = errorLabels.get(control);
        return errorLabel != null && errorLabel.isVisible();
    }
}
