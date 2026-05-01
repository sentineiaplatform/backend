package com.sentineia.audit;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
        name = "audit_logs",
        indexes = {@Index(name = "idx_audit_occurred_at", columnList = "occurred_at")})
@Getter
@Setter
public class AuditLog {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Column(name = "actor_email", length = 320)
    private String actorEmail;

    @Column(nullable = false, length = 32)
    private String category;

    @Column(nullable = false, length = 500)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @PrePersist
    void prePersist() {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
