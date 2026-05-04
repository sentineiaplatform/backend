package com.sentineia.complaint.link;

import com.sentineia.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComplaintLinkRepository extends BaseRepository<ComplaintLink> {
    List<ComplaintLink> findBySourceIdOrderByCreatedAtDesc(UUID sourceId);
    List<ComplaintLink> findByTargetIdOrderByCreatedAtDesc(UUID targetId);
    boolean existsBySourceIdAndTargetIdAndLinkType(UUID sourceId, UUID targetId, String linkType);
}
