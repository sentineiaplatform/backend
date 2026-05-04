package com.sentineia.complaint.department;

import java.util.Optional;

import com.sentineia.base.BaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintDepartmentRepository extends BaseRepository<ComplaintDepartment> {

    Optional<ComplaintDepartment> findByName(String name);
}
