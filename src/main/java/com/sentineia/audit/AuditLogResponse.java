package com.sentineia.audit;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        Instant occurredAt,
        UUID actorUserId,
        String actorEmail,
        String category,
        String action,
        String detail) {

    static AuditLogResponse from(AuditLog e) {
        return new AuditLogResponse(
                e.getId(),
                e.getOccurredAt(),
                e.getActorUserId(),
                e.getActorEmail(),
                e.getCategory(),
                e.getAction(),
                e.getDetail());
    }
}
