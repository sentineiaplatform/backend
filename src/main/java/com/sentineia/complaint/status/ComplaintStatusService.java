package com.sentineia.complaint.status;

import java.util.Optional;
import java.util.UUID;

import com.sentineia.base.BaseService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComplaintStatusService extends BaseService<ComplaintStatus> {

    private final ComplaintStatusRepository complaintStatusRepository;

    public ComplaintStatusService(ComplaintStatusRepository repository) {
        super(repository);
        this.complaintStatusRepository = repository;
    }

    @Transactional
    public Optional<ComplaintStatus> update(UUID id, ComplaintStatus incoming) {
        return complaintStatusRepository.findById(id).map(existing -> {
            existing.setName(incoming.getName());
            existing.setDescription(incoming.getDescription());
            existing.setActive(incoming.isActive());
            return complaintStatusRepository.save(existing);
        });
    }
}
