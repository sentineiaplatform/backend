package com.sentineia.complaint.priority;

import java.util.Optional;

import com.sentineia.base.BaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintPriorityRepository extends BaseRepository<ComplaintPriority> {

    Optional<ComplaintPriority> findByCode(String code);
}
