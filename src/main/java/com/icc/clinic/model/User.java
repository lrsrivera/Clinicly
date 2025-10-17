package com.icc.clinic.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "password_reset_count")
    private Integer passwordResetCount = 0;

    @Column(name = "last_password_reset")
    private java.time.LocalDateTime lastPasswordReset;

    @Column(name = "is_default_password")
    private Boolean isDefaultPassword = true;

    @Column(name = "password_changed_at")
    private java.time.LocalDateTime passwordChangedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Constructors
    public User() {}

    public User(String username, String password, String firstName, String lastName, String email, Role role) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getPasswordResetCount() {
        return passwordResetCount;
    }

    public void setPasswordResetCount(Integer passwordResetCount) {
        this.passwordResetCount = passwordResetCount;
    }

    public java.time.LocalDateTime getLastPasswordReset() {
        return lastPasswordReset;
    }

    public void setLastPasswordReset(java.time.LocalDateTime lastPasswordReset) {
        this.lastPasswordReset = lastPasswordReset;
    }

    public Boolean getIsDefaultPassword() {
        return isDefaultPassword;
    }

    public void setIsDefaultPassword(Boolean isDefaultPassword) {
        this.isDefaultPassword = isDefaultPassword;
    }

    public java.time.LocalDateTime getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(java.time.LocalDateTime passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }

    public Role getRole() { 
        return role; 
    }
    
    public void setRole(Role role) { 
        this.role = role; 
    }

    // Helper methods
    public boolean hasPermission(String permissionName) {
        if (role == null || role.getPermissions() == null) {
            return false;
        }
        return role.getPermissions().stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }

    public boolean hasRole(String roleName) {
        return role != null && role.getName().equals(roleName);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", role=" + (role != null ? role.getName() : "null") +
                '}';
    }
} 