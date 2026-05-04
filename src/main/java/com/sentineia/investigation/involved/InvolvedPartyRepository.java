package com.sentineia.investigation.involved;

import com.sentineia.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvolvedPartyRepository extends BaseRepository<InvolvedParty> {
    List<InvolvedParty> findByInvestigationIdOrderByCreatedAtAsc(UUID investigationId);
}
