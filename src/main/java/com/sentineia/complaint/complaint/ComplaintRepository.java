package com.sentineia.complaint.complaint;

import com.sentineia.base.BaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends BaseRepository<Complaint> {

    boolean existsByProtocol(String protocol);
}
