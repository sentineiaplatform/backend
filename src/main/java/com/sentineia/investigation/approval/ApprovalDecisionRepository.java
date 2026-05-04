package com.sentineia.investigation.approval;

import com.sentineia.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApprovalDecisionRepository extends BaseRepository<ApprovalDecision> {
    List<ApprovalDecision> findByInvestigationIdOrderByLevelOrderAsc(UUID investigationId);
}
