package com.icc.clinic.service;

import com.icc.clinic.model.AdminActionLog;
import com.icc.clinic.model.User;
import com.icc.clinic.repository.AdminActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminActionLogService {

    @Autowired
    private AdminActionLogRepository repository;

    public void log(User admin, User target, String action, String details) {
        AdminActionLog log = new AdminActionLog();
        log.setAdminUser(admin);
        log.setTargetUser(target);
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        repository.save(log);
    }

    public List<AdminActionLog> getBetween(LocalDateTime start, LocalDateTime end) {
        return repository.findByTimestampBetween(start, end);
    }

    public List<AdminActionLog> getAll() {
        return repository.findAll();
    }
    
    // No code path to delete logs at runtime; logs are append-only
}


