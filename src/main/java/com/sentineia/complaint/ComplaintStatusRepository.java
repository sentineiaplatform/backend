package com.sentineia.complaint;

import java.util.List;

import com.sentineia.base.BaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintStatusRepository extends BaseRepository<ComplaintStatus> {

    List<ComplaintStatus> findAllByOrderBySortOrderAsc();
}
