package com.icc.clinic.service;

import com.icc.clinic.model.PasswordAudit;
import com.icc.clinic.model.User;
import com.icc.clinic.repository.PasswordAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class PasswordService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordAuditRepository passwordAuditRepository;

    // Default passwords for each role
    public static final String DEFAULT_NURSE_PASSWORD = "nurse123";
    public static final String DEFAULT_IT_PASSWORD = "admin123";

    // Password validation pattern
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    /**
     * Get default password for a role
     */
    public String getDefaultPasswordForRole(String roleName) {
        if ("NURSE".equals(roleName)) {
            return DEFAULT_NURSE_PASSWORD;
        } else if ("IT".equals(roleName)) {
            return DEFAULT_IT_PASSWORD;
        }
        return DEFAULT_NURSE_PASSWORD; // Default fallback
    }

    /**
     * Check if password meets requirements
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Check if password is a default password
     */
    public boolean isDefaultPassword(String password) {
        return DEFAULT_NURSE_PASSWORD.equals(password) || DEFAULT_IT_PASSWORD.equals(password);
    }

    /**
     * Get password strength description
     */
    public String getPasswordStrengthDescription(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[@$!%*?&].*");
        
        StringBuilder issues = new StringBuilder();
        if (!hasLower) issues.append("lowercase letter, ");
        if (!hasUpper) issues.append("uppercase letter, ");
        if (!hasDigit) issues.append("number, ");
        if (!hasSpecial) issues.append("special character (@$!%*?&), ");
        
        if (issues.length() > 0) {
            return "Password must contain: " + issues.toString().replaceAll(", $", "");
        }
        
        return "Strong password!";
    }

    /**
     * Change user password
     */
    public boolean changePassword(User user, String currentPassword, String newPassword) {
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // Validate new password
        if (!isValidPassword(newPassword)) {
            return false;
        }

        // Check if new password is default
        if (isDefaultPassword(newPassword)) {
            return false;
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setIsDefaultPassword(false);
        user.setPasswordChangedAt(LocalDateTime.now());

        // Log password change
        PasswordAudit audit = new PasswordAudit(user, "CHANGE", user.getPasswordResetCount());
        passwordAuditRepository.save(audit);

        return true;
    }

    /**
     * Reset password to default (IT only)
     */
    public void resetPasswordToDefault(User user) {
        String defaultPassword = getDefaultPasswordForRole(user.getRole().getName());
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setIsDefaultPassword(true);
        user.setPasswordResetCount(0);
        user.setLastPasswordReset(LocalDateTime.now());

        // Log admin reset
        PasswordAudit audit = new PasswordAudit(user, "ADMIN_RESET", 0);
        passwordAuditRepository.save(audit);
    }

    /**
     * Self-service password reset
     */
    public boolean selfServicePasswordReset(User user, String newPassword) {
        // Check reset limit
        if (user.getPasswordResetCount() >= 2) {
            return false; // Requires IT intervention
        }

        // Validate new password
        if (!isValidPassword(newPassword)) {
            return false;
        }

        // Check if new password is default
        if (isDefaultPassword(newPassword)) {
            return false;
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setIsDefaultPassword(false);
        user.setPasswordResetCount(user.getPasswordResetCount() + 1);
        user.setLastPasswordReset(LocalDateTime.now());
        user.setPasswordChangedAt(LocalDateTime.now());

        // Log password reset
        boolean flagged = user.getPasswordResetCount() >= 2;
        PasswordAudit audit = new PasswordAudit(user, "RESET", user.getPasswordResetCount());
        audit.setFlagged(flagged);
        passwordAuditRepository.save(audit);

        return true;
    }

    /**
     * Check if user needs to change password
     */
    public boolean needsPasswordChange(User user) {
        return user.getIsDefaultPassword() != null && user.getIsDefaultPassword();
    }
}
