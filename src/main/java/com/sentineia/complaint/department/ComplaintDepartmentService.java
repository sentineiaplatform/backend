package com.sentineia.complaint.department;

import java.util.Optional;
import java.util.UUID;

import com.sentineia.base.BaseService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComplaintDepartmentService extends BaseService<ComplaintDepartment> {

    private final ComplaintDepartmentRepository departmentRepository;

    public ComplaintDepartmentService(ComplaintDepartmentRepository repository) {
        super(repository);
        this.departmentRepository = repository;
    }

    @Transactional
    public Optional<ComplaintDepartment> update(UUID id, ComplaintDepartment incoming) {
        return departmentRepository.findById(id).map(existing -> {
            existing.setName(incoming.getName().trim());
            existing.setDescription(incoming.getDescription());
            existing.setActive(incoming.isActive());
            return departmentRepository.save(existing);
        });
    }
}
