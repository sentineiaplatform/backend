package com.sentineia.investigation.investigation;

import com.sentineia.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvestigationRepository extends BaseRepository<Investigation> {
    Optional<Investigation> findByComplaintId(UUID complaintId);
}
