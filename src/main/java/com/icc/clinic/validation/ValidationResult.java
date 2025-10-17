package com.icc.clinic.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a validation operation
 */
public class ValidationResult {
    private boolean valid;
    private List<String> errors;
    private List<String> warnings;

    public ValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public ValidationResult(boolean valid) {
        this.valid = valid;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public boolean isValid() {
        return valid && errors.isEmpty();
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }

    public void addErrors(List<String> errors) {
        this.errors.addAll(errors);
        if (!errors.isEmpty()) {
            this.valid = false;
        }
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public void addWarnings(List<String> warnings) {
        this.warnings.addAll(warnings);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }

    public String getAllErrorsAsString() {
        return String.join("; ", errors);
    }

    public String getAllWarningsAsString() {
        return String.join("; ", warnings);
    }

    public void clear() {
        this.valid = true;
        this.errors.clear();
        this.warnings.clear();
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errors=" + errors +
                ", warnings=" + warnings +
                '}';
    }
}
