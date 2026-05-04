package com.sentineia.complaint.priority;

import java.util.Optional;
import java.util.UUID;

import com.sentineia.base.BaseService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComplaintPriorityService extends BaseService<ComplaintPriority> {

    private final ComplaintPriorityRepository priorityRepository;

    public ComplaintPriorityService(ComplaintPriorityRepository repository) {
        super(repository);
        this.priorityRepository = repository;
    }

    @Transactional
    public Optional<ComplaintPriority> update(UUID id, ComplaintPriority incoming) {
        return priorityRepository.findById(id).map(existing -> {
            existing.setCode(incoming.getCode().trim().toUpperCase());
            existing.setName(incoming.getName());
            existing.setDescription(incoming.getDescription());
            existing.setActive(incoming.isActive());
            return priorityRepository.save(existing);
        });
    }
}
