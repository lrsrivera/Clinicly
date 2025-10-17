package com.icc.clinic.service;

import com.icc.clinic.model.User;
import com.icc.clinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RBACService rbacService;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @PostConstruct
    public void init() {
        // RBAC initialization is now handled by RBACService
        // This method is kept for backward compatibility
        
        // Create default IT admin user if it doesn't exist
        createDefaultITAdmin();
    }
    
    private void createDefaultITAdmin() {
        try {
            Optional<User> existingAdmin = userRepository.findByUsername("it_admin");
            if (existingAdmin.isEmpty()) {
                System.out.println("Creating default IT admin user...");
                User admin = new User();
                admin.setUsername("it_admin");
                admin.setFirstName("IT");
                admin.setLastName("Administrator");
                admin.setEmail("it@clinic.com");
                admin.setPassword("admin123"); // This will be encoded by saveUser
                admin.setActive(true);
                admin.setIsDefaultPassword(true); // Must change password on first login
                admin.setPasswordChangedAt(null); // Not changed yet
                
                // Set IT role
                rbacService.getRoleByName("IT").ifPresent(admin::setRole);
                
                saveUser(admin);
                System.out.println("Default IT admin user created successfully");
            } else {
                System.out.println("IT admin user already exists - resetting password to ensure it's correct");
                User admin = existingAdmin.get();
                admin.setPassword("admin123"); // Reset password
                admin.setIsDefaultPassword(true); // Must change password on first login
                admin.setPasswordChangedAt(null); // Not changed yet
                saveUser(admin);
                System.out.println("IT admin password reset successfully");
            }
        } catch (Exception e) {
            System.err.println("Error creating/resetting IT admin: " + e.getMessage());
        }
    }
    
    public Optional<User> authenticateUser(String username, String password) {
        System.out.println("UserService: Authenticating user: " + username);
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            System.out.println("UserService: User not found: " + username);
            return Optional.empty();
        }
        
        User user = userOpt.get();
        System.out.println("UserService: User found - Active: " + user.getActive() + ", Role: " + (user.getRole() != null ? user.getRole().getName() : "null"));
        System.out.println("UserService: Stored password hash: " + user.getPassword());
        System.out.println("UserService: Entered password: " + password);
        
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("UserService: Password matches: " + passwordMatches);
        
        if (user.getActive() && passwordMatches) {
            System.out.println("UserService: Authentication successful for: " + username);
            // Decrypt sensitive data before returning
            return Optional.of(decryptSensitiveData(user));
        } else {
            System.out.println("UserService: Authentication failed for: " + username);
            return Optional.empty();
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Decrypt sensitive data when reading
        return users.stream()
                .map(this::decryptSensitiveData)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<User> getActiveUsers() {
        List<User> users = userRepository.findByActive(true);
        // Decrypt sensitive data when reading
        return users.stream()
                .map(this::decryptSensitiveData)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            // Password is not encoded, encode it
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        // Encrypt sensitive data before saving
        User encryptedUser = encryptSensitiveData(user);
        return userRepository.save(encryptedUser);
    }
    
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::decryptSensitiveData);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::decryptSensitiveData);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::decryptSensitiveData);
    }
    
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    // Manual method to force fix it_admin encryption (can be called from controller)
    public void forceFixItAdminEncryption() {
        System.out.println("[UserService] Force-fixing it_admin encryption...");
        try {
            User itAdmin = userRepository.findByUsername("it_admin").orElse(null);
            if (itAdmin != null) {
                // Reset to correct values
                itAdmin.setFirstName("IT");
                itAdmin.setLastName("Administrator");
                itAdmin.setEmail("it@clinic.com");
                
                // Re-encrypt with correct data
                User encryptedUser = encryptSensitiveData(itAdmin);
                userRepository.save(encryptedUser);
                System.out.println("[UserService] Successfully force-fixed it_admin encryption");
            } else {
                System.err.println("[UserService] it_admin user not found!");
            }
        } catch (Exception e) {
            System.err.println("[UserService] Error force-fixing it_admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method to re-encrypt all users (for fixing inconsistent data)
    public void reEncryptAllUsers() {
        System.out.println("[UserService] Re-encrypting all users...");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            // Check if data is already encrypted
            if (encryptionService.isEncrypted(user.getFirstName())) {
                System.out.println("[UserService] User " + user.getUsername() + " already encrypted, skipping");
                continue;
            }
            
            // Re-encrypt the user
            User encryptedUser = encryptSensitiveData(user);
            userRepository.save(encryptedUser);
            System.out.println("[UserService] Re-encrypted user: " + user.getUsername());
        }
        System.out.println("[UserService] Re-encryption completed.");
    }
    
    // Method to fix corrupted encrypted data by resetting to plain text and re-encrypting
    public void fixCorruptedEncryption() {
        System.out.println("[UserService] Fixing corrupted encryption...");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            // Force fix for it_admin user specifically
            if ("it_admin".equals(user.getUsername())) {
                System.out.println("[UserService] Force-fixing it_admin user encryption...");
                
                // Reset to correct values
                user.setFirstName("IT");
                user.setLastName("Administrator");
                user.setEmail("it@clinic.com");
                
                // Re-encrypt with correct data
                User encryptedUser = encryptSensitiveData(user);
                userRepository.save(encryptedUser);
                System.out.println("[UserService] Force-fixed and re-encrypted it_admin user");
                continue;
            }
            
            // Check if data appears to be encrypted but decryption fails
            if (encryptionService.isEncrypted(user.getFirstName())) {
                try {
                    // Try to decrypt - if it fails, the data is corrupted
                    String decryptedFirstName = encryptionService.decrypt(user.getFirstName());
                    if (decryptedFirstName.equals(user.getFirstName())) {
                        // Decryption failed, data is corrupted
                        System.out.println("[UserService] Detected corrupted encryption for user: " + user.getUsername());
                        
                        // Reset to default values and re-encrypt
                        if ("nurse".equals(user.getUsername())) {
                            user.setFirstName("Nurse");
                            user.setLastName("User");
                            user.setEmail("nurse@clinic.com");
                        }
                        
                        // Re-encrypt with correct data
                        User encryptedUser = encryptSensitiveData(user);
                        userRepository.save(encryptedUser);
                        System.out.println("[UserService] Fixed and re-encrypted user: " + user.getUsername());
                    }
                } catch (Exception e) {
                    System.err.println("[UserService] Error checking encryption for user " + user.getUsername() + ": " + e.getMessage());
                }
            }
        }
        System.out.println("[UserService] Corruption fix completed.");
    }
    
    // Encryption methods for sensitive data
    public User encryptSensitiveData(User user) {
        if (user == null) return user;
        
        User encryptedUser = new User();
        encryptedUser.setId(user.getId());
        encryptedUser.setUsername(user.getUsername()); // Username is not encrypted (used for login)
        encryptedUser.setPassword(user.getPassword()); // Password is already hashed
        encryptedUser.setFirstName(encryptionService.encrypt(user.getFirstName()));
        encryptedUser.setLastName(encryptionService.encrypt(user.getLastName()));
        encryptedUser.setEmail(encryptionService.encrypt(user.getEmail()));
        encryptedUser.setRole(user.getRole());
        encryptedUser.setActive(user.getActive());
        encryptedUser.setIsDefaultPassword(user.getIsDefaultPassword());
        encryptedUser.setPasswordChangedAt(user.getPasswordChangedAt());
        encryptedUser.setLastPasswordReset(user.getLastPasswordReset());
        encryptedUser.setPasswordResetCount(user.getPasswordResetCount());
        
        return encryptedUser;
    }
    
    public User decryptSensitiveData(User user) {
        if (user == null) return user;
        
        System.out.println("[UserService] Decrypting user: " + user.getUsername());
        System.out.println("[UserService] Original FirstName: " + user.getFirstName());
        System.out.println("[UserService] Original LastName: " + user.getLastName());
        System.out.println("[UserService] Original Email: " + user.getEmail());
        
        User decryptedUser = new User();
        decryptedUser.setId(user.getId());
        decryptedUser.setUsername(user.getUsername());
        decryptedUser.setPassword(user.getPassword());
        
        // Special handling for it_admin user with corrupted encryption
        if ("it_admin".equals(user.getUsername()) && encryptionService.isEncrypted(user.getFirstName())) {
            System.out.println("[UserService] Detected corrupted encryption for it_admin, using fallback values");
            decryptedUser.setFirstName("IT");
            decryptedUser.setLastName("Administrator");
            decryptedUser.setEmail("it@clinic.com");
        } else {
            decryptedUser.setFirstName(encryptionService.decrypt(user.getFirstName()));
            decryptedUser.setLastName(encryptionService.decrypt(user.getLastName()));
            decryptedUser.setEmail(encryptionService.decrypt(user.getEmail()));
        }
        
        decryptedUser.setRole(user.getRole());
        decryptedUser.setActive(user.getActive());
        decryptedUser.setIsDefaultPassword(user.getIsDefaultPassword());
        decryptedUser.setPasswordChangedAt(user.getPasswordChangedAt());
        decryptedUser.setLastPasswordReset(user.getLastPasswordReset());
        decryptedUser.setPasswordResetCount(user.getPasswordResetCount());
        
        System.out.println("[UserService] Decrypted FirstName: " + decryptedUser.getFirstName());
        System.out.println("[UserService] Decrypted LastName: " + decryptedUser.getLastName());
        System.out.println("[UserService] Decrypted Email: " + decryptedUser.getEmail());
        
        return decryptedUser;
    }
} 