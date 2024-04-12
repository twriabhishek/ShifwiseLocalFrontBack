package com.exato.usermodule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exato.usermodule.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
