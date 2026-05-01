package com.sentineia.audit;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private static final int MAX_ACTION = 500;
    private static final int MAX_DETAIL = 8000;

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Regista um evento de auditoria. O tráfego HTTP {@code /api/**} é coberto por
     * {@link com.sentineia.audit.AuditHttpLoggingInterceptor}; use este método só para
     * eventos que não passam pelo MVC (jobs, integrações) se necessário.
     */
    @Transactional
    public void record(UUID actorUserId, String actorEmail, String category, String action, String detail) {
        if (category == null || category.isBlank()) {
            category = "geral";
        }
        String cat = category.trim().toLowerCase();
        if (cat.length() > 32) {
            cat = cat.substring(0, 32);
        }
        String act = truncate(action == null ? "" : action.trim(), MAX_ACTION);
        String det = detail == null ? null : truncate(detail.trim(), MAX_DETAIL);
        String email = actorEmail == null || actorEmail.isBlank() ? null : actorEmail.trim().toLowerCase();

        AuditLog log = new AuditLog();
        log.setOccurredAt(Instant.now());
        log.setActorUserId(actorUserId);
        log.setActorEmail(email);
        log.setCategory(cat);
        log.setAction(act.isEmpty() ? "Evento" : act);
        log.setDetail(det);
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findPage(String categoryFilter, Pageable pageable) {
        String cat =
                categoryFilter == null || categoryFilter.isBlank() ? null : categoryFilter.trim().toLowerCase();
        if (cat == null) {
            return auditLogRepository.findAllByOrderByOccurredAtDesc(pageable);
        }
        return auditLogRepository.findAllByCategoryOrderByOccurredAtDesc(cat, pageable);
    }

    private static String truncate(String s, int max) {
        if (s.length() <= max) return s;
        return s.substring(0, max - 1) + "…";
    }
}
