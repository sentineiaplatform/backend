package com.sentineia.investigation.action;

import com.sentineia.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CorrectiveActionRepository extends BaseRepository<CorrectiveAction> {
    List<CorrectiveAction> findByInvestigationIdOrderByCreatedAtAsc(UUID investigationId);
}
