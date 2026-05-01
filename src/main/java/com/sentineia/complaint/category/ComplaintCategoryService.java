package com.sentineia.complaint.category;

import java.util.Optional;
import java.util.UUID;

import com.sentineia.base.BaseService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComplaintCategoryService extends BaseService<ComplaintCategory> {

    private final ComplaintCategoryRepository complaintCategoryRepository;

    public ComplaintCategoryService(ComplaintCategoryRepository repository) {
        super(repository);
        this.complaintCategoryRepository = repository;
    }

    @Transactional
    public Optional<ComplaintCategory> update(UUID id, ComplaintCategory incoming) {
        return complaintCategoryRepository.findById(id).map(existing -> {
            existing.setName(incoming.getName());
            existing.setDescription(incoming.getDescription());
            existing.setSlaDays(incoming.getSlaDays());
            existing.setActive(incoming.isActive());
            return complaintCategoryRepository.save(existing);
        });
    }
}
