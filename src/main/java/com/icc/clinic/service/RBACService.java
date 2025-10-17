package com.icc.clinic.service;

import com.icc.clinic.model.Permission;
import com.icc.clinic.model.Role;
import com.icc.clinic.model.User;
import com.icc.clinic.repository.PermissionRepository;
import com.icc.clinic.repository.RoleRepository;
import com.icc.clinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Transactional
public class RBACService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeRBAC() {
        createDefaultPermissions();
        createDefaultRoles();
        createDefaultUsers();
    }

    private void createDefaultPermissions() {
        // Patient Management Permissions
        createPermissionIfNotExists("PATIENT_CREATE", "Create new patients", "PATIENT", "CREATE");
        createPermissionIfNotExists("PATIENT_READ", "View patient information", "PATIENT", "READ");
        createPermissionIfNotExists("PATIENT_UPDATE", "Update patient information", "PATIENT", "UPDATE");
        createPermissionIfNotExists("PATIENT_DELETE", "Delete patients", "PATIENT", "DELETE");
        createPermissionIfNotExists("PATIENT_ARCHIVE", "Archive/unarchive patients", "PATIENT", "ARCHIVE");

        // Appointment Management Permissions
        createPermissionIfNotExists("APPOINTMENT_CREATE", "Schedule appointments", "APPOINTMENT", "CREATE");
        createPermissionIfNotExists("APPOINTMENT_READ", "View appointments", "APPOINTMENT", "READ");
        createPermissionIfNotExists("APPOINTMENT_UPDATE", "Update appointment status", "APPOINTMENT", "UPDATE");
        createPermissionIfNotExists("APPOINTMENT_DELETE", "Delete appointments", "APPOINTMENT", "DELETE");
        createPermissionIfNotExists("APPOINTMENT_ARCHIVE", "Archive/unarchive appointments", "APPOINTMENT", "ARCHIVE");

        // Data Management Permissions
        createPermissionIfNotExists("DATA_IMPORT", "Import data from Excel", "DATA", "IMPORT");
        createPermissionIfNotExists("DATA_EXPORT", "Export data", "DATA", "EXPORT");

        // User Management Permissions
        createPermissionIfNotExists("USER_CREATE", "Create user accounts", "USER", "CREATE");
        createPermissionIfNotExists("USER_READ", "View user accounts", "USER", "READ");
        createPermissionIfNotExists("USER_UPDATE", "Update user accounts", "USER", "UPDATE");
        createPermissionIfNotExists("USER_DELETE", "Delete user accounts", "USER", "DELETE");

        // System Management Permissions
        createPermissionIfNotExists("SYSTEM_CONFIG", "Configure system settings", "SYSTEM", "CONFIG");
        createPermissionIfNotExists("SYSTEM_BACKUP", "Backup/restore system", "SYSTEM", "BACKUP");
        createPermissionIfNotExists("SYSTEM_LOGS", "View system logs", "SYSTEM", "LOGS");
    }

    private void createPermissionIfNotExists(String name, String description, String resource, String action) {
        if (!permissionRepository.existsByName(name)) {
            Permission permission = new Permission(name, description, resource, action);
            permissionRepository.save(permission);
        }
    }

    private void createDefaultRoles() {
        // Create NURSE role
        Role nurseRole = createRoleIfNotExists("NURSE", "Clinical staff with patient and appointment management access");
        assignPermissionsToRole(nurseRole, Arrays.asList(
            "PATIENT_CREATE", "PATIENT_READ", "PATIENT_UPDATE", "PATIENT_ARCHIVE",
            "APPOINTMENT_CREATE", "APPOINTMENT_READ", "APPOINTMENT_UPDATE", "APPOINTMENT_ARCHIVE",
            "DATA_IMPORT", "DATA_EXPORT"
        ));

        // Create IT role
        Role itRole = createRoleIfNotExists("IT", "IT administrator with full system access");
        assignPermissionsToRole(itRole, Arrays.asList(
            "PATIENT_CREATE", "PATIENT_READ", "PATIENT_UPDATE", "PATIENT_DELETE", "PATIENT_ARCHIVE",
            "APPOINTMENT_CREATE", "APPOINTMENT_READ", "APPOINTMENT_UPDATE", "APPOINTMENT_DELETE", "APPOINTMENT_ARCHIVE",
            "DATA_IMPORT", "DATA_EXPORT",
            "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
            "SYSTEM_CONFIG", "SYSTEM_BACKUP", "SYSTEM_LOGS"
        ));
    }

    private Role createRoleIfNotExists(String name, String description) {
        Optional<Role> existingRole = roleRepository.findByName(name);
        if (existingRole.isPresent()) {
            return existingRole.get();
        }
        
        Role role = new Role(name, description);
        return roleRepository.save(role);
    }

    private void assignPermissionsToRole(Role role, List<String> permissionNames) {
        Set<Permission> permissions = new HashSet<>();
        for (String permissionName : permissionNames) {
            Optional<Permission> permission = permissionRepository.findByName(permissionName);
            permission.ifPresent(permissions::add);
        }
        role.setPermissions(permissions);
        roleRepository.save(role);
    }

    private void createDefaultUsers() {
        // Create default IT user
        createUserIfNotExists("it_admin", "admin123", "IT", "Administrator", "it@clinic.com", "IT");
        
        // Create default Nurse user
        createUserIfNotExists("nurse", "nurse123", "Nurse", "User", "nurse@clinic.com", "NURSE");
    }

    private void createUserIfNotExists(String username, String password, String firstName, String lastName, String email, String roleName) {
        if (!userRepository.findByUsername(username).isPresent()) {
            Optional<Role> role = roleRepository.findByName(roleName);
            if (role.isPresent()) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setRole(role.get());
                user.setActive(true);
                
                // Set default password flags
                user.setIsDefaultPassword(true);
                user.setPasswordChangedAt(null);
                user.setPasswordResetCount(0);
                
                userRepository.save(user);
            }
        }
    }

    // Public methods for role and permission management
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Optional<Permission> getPermissionByName(String name) {
        return permissionRepository.findByName(name);
    }

    public boolean userHasPermission(User user, String permissionName) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return user.hasPermission(permissionName);
    }

    public boolean userHasRole(User user, String roleName) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return user.hasRole(roleName);
    }

    public List<User> getUsersByRole(String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isPresent()) {
            return userRepository.findByRole(role.get());
        }
        return new ArrayList<>();
    }
}
