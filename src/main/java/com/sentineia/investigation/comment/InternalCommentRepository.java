package com.sentineia.investigation.comment;

import com.sentineia.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InternalCommentRepository extends BaseRepository<InternalComment> {
    List<InternalComment> findByInvestigationIdOrderByCreatedAtAsc(UUID investigationId);
}
