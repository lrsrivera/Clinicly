package com.icc.clinic.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_action_logs")
public class AdminActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_user_id")
    private User adminUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @Column(nullable = false)
    private String action; // e.g., CREATE_USER, UPDATE_USER, DELETE_USER, RESET_PASSWORD

    @Column(length = 1000)
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public Long getId() { return id; }
    public User getAdminUser() { return adminUser; }
    public void setAdminUser(User adminUser) { this.adminUser = adminUser; }
    public User getTargetUser() { return targetUser; }
    public void setTargetUser(User targetUser) { this.targetUser = targetUser; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}



