package com.sentineia.complaint;

import java.util.Optional;

import com.sentineia.base.BaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintCategoryRepository extends BaseRepository<ComplaintCategory> {

    Optional<ComplaintCategory> findByNameIgnoreCase(String name);
}
