package com.icc.clinic.validation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class containing reusable validation methods
 */
public class FieldValidators {

    // Common patterns
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s\\-'\\u00C0-\\u017F]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-\\(\\)]{7,15}$");
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-]{3,20}$");

    /**
     * Validates if a string is not null and not empty
     */
    public static ValidationResult validateRequired(String value, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (value == null || value.trim().isEmpty()) {
            result.addError(fieldName + " is required");
        }
        return result;
    }

    /**
     * Validates string length
     */
    public static ValidationResult validateLength(String value, String fieldName, int minLength, int maxLength) {
        ValidationResult result = new ValidationResult();
        if (value != null) {
            int length = value.trim().length();
            if (length < minLength) {
                result.addError(fieldName + " must be at least " + minLength + " characters");
            }
            if (length > maxLength) {
                result.addError(fieldName + " must be no more than " + maxLength + " characters");
            }
        }
        return result;
    }

    /**
     * Validates alphanumeric format (letters, numbers, underscore)
     */
    public static ValidationResult validateAlphanumeric(String value, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (value != null && !value.trim().isEmpty()) {
            if (!ALPHANUMERIC_PATTERN.matcher(value).matches()) {
                result.addError(fieldName + " must contain only letters, numbers, and underscores");
            }
        }
        return result;
    }

    /**
     * Validates name format (letters, spaces, hyphens, apostrophes, accented characters)
     */
    public static ValidationResult validateName(String value, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (value != null && !value.trim().isEmpty()) {
            if (!NAME_PATTERN.matcher(value).matches()) {
                result.addError(fieldName + " must contain only letters, spaces, hyphens, and apostrophes");
            }
        }
        return result;
    }

    /**
     * Validates email format
     */
    public static ValidationResult validateEmail(String value, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (value != null && !value.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(value).matches()) {
                result.addError(fieldName + " format is invalid");
            }
        }
        return result;
    }

    /**
     * Validates phone number format
     */
    public static ValidationResult validatePhone(String value, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (value != null && !value.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(value).matches()) {
                result.addError(fieldName + " format is invalid");
            }
        }
        return result;
    }

    /**
     * Validates student ID format
     */
    public static ValidationResult validateStudentId(String value, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (value != null && !value.trim().isEmpty()) {
            if (!STUDENT_ID_PATTERN.matcher(value).matches()) {
                result.addError(fieldName + " must be 3-20 characters (letters, numbers, hyphens only)");
            }
        }
        return result;
    }

    /**
     * Validates date format and range
     */
    public static ValidationResult validateDate(String value, String fieldName, LocalDate minDate, LocalDate maxDate) {
        ValidationResult result = new ValidationResult();
        if (value != null && !value.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(value);
                if (minDate != null && date.isBefore(minDate)) {
                    result.addError(fieldName + " cannot be before " + minDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
                if (maxDate != null && date.isAfter(maxDate)) {
                    result.addError(fieldName + " cannot be after " + maxDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
            } catch (DateTimeParseException e) {
                result.addError(fieldName + " format is invalid");
            }
        }
        return result;
    }

    /**
     * Validates datetime format and range
     */
    public static ValidationResult validateDateTime(LocalDateTime value, String fieldName, LocalDateTime minDateTime, LocalDateTime maxDateTime) {
        ValidationResult result = new ValidationResult();
        if (value != null) {
            if (minDateTime != null && value.isBefore(minDateTime)) {
                result.addError(fieldName + " cannot be before " + minDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            if (maxDateTime != null && value.isAfter(maxDateTime)) {
                result.addError(fieldName + " cannot be after " + maxDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
        }
        return result;
    }

    /**
     * Validates if value is in allowed list
     */
    public static ValidationResult validateAllowedValues(String value, String fieldName, String[] allowedValues) {
        ValidationResult result = new ValidationResult();
        if (value != null && !value.trim().isEmpty()) {
            boolean found = false;
            for (String allowed : allowedValues) {
                if (allowed.equalsIgnoreCase(value.trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.addError(fieldName + " must be one of: " + String.join(", ", allowedValues));
            }
        }
        return result;
    }

    /**
     * Validates if end time is after start time
     */
    public static ValidationResult validateTimeOrder(LocalDateTime startTime, LocalDateTime endTime, String startFieldName, String endFieldName) {
        ValidationResult result = new ValidationResult();
        if (startTime != null && endTime != null) {
            if (!endTime.isAfter(startTime)) {
                result.addError(endFieldName + " must be after " + startFieldName);
            }
        }
        return result;
    }

    /**
     * Validates appointment duration (reasonable range)
     */
    public static ValidationResult validateAppointmentDuration(LocalDateTime startTime, LocalDateTime endTime, int minMinutes, int maxMinutes) {
        ValidationResult result = new ValidationResult();
        if (startTime != null && endTime != null) {
            long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
            if (durationMinutes < minMinutes) {
                result.addError("Appointment duration must be at least " + minMinutes + " minutes");
            }
            if (durationMinutes > maxMinutes) {
                result.addError("Appointment duration cannot exceed " + maxMinutes + " minutes");
            }
        }
        return result;
    }

    /**
     * Validates business hours (8 AM - 6 PM)
     */
    public static ValidationResult validateBusinessHours(LocalDateTime dateTime, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (dateTime != null) {
            int hour = dateTime.getHour();
            if (hour < 8 || hour >= 18) {
                result.addError(fieldName + " must be between 8:00 AM and 6:00 PM");
            }
        }
        return result;
    }

    /**
     * Validates if date is not in the past
     */
    public static ValidationResult validateNotPast(LocalDateTime dateTime, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (dateTime != null) {
            if (dateTime.isBefore(LocalDateTime.now())) {
                result.addError(fieldName + " cannot be in the past");
            }
        }
        return result;
    }

    /**
     * Validates if date is not in the past (for dates only)
     */
    public static ValidationResult validateNotPastDate(LocalDate date, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (date != null) {
            if (date.isBefore(LocalDate.now())) {
                result.addError(fieldName + " cannot be in the past");
            }
        }
        return result;
    }

        /**
     * Checks if input contains dangerous SQL or script characters.
     */
    public static boolean isSafeInput(String input) {
        if (input == null) return true;
        String lower = input.toLowerCase();
        // Reject SQL or HTML/script injections
        return !(lower.contains("drop ") ||
                lower.contains("delete ") ||
                lower.contains("insert ") ||
                lower.contains("update ") ||
                lower.contains("<script") ||
                lower.contains("--") ||
                lower.contains(";") ||
                lower.matches(".*(['\"\\\\]).*"));
    }

}
