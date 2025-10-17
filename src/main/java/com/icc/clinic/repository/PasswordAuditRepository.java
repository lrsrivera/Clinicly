package com.icc.clinic.repository;

import com.icc.clinic.model.PasswordAudit;
import com.icc.clinic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordAuditRepository extends JpaRepository<PasswordAudit, Long> {
    List<PasswordAudit> findByUserOrderByCreatedAtDesc(User user);
    List<PasswordAudit> findByFlaggedTrueOrderByCreatedAtDesc();
    List<PasswordAudit> findByActionOrderByCreatedAtDesc(String action);
}
