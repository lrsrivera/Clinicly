package com.icc.clinic.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_audit")
public class PasswordAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String action; // 'CHANGE', 'RESET', 'ADMIN_RESET'

    @Column(name = "reset_count")
    private Integer resetCount;

    @Column(nullable = false)
    private Boolean flagged = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public PasswordAudit() {}

    public PasswordAudit(User user, String action, Integer resetCount) {
        this.user = user;
        this.action = action;
        this.resetCount = resetCount;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getResetCount() {
        return resetCount;
    }

    public void setResetCount(Integer resetCount) {
        this.resetCount = resetCount;
    }

    public Boolean getFlagged() {
        return flagged;
    }

    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PasswordAudit{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", resetCount=" + resetCount +
                ", flagged=" + flagged +
                ", createdAt=" + createdAt +
                '}';
    }
}
