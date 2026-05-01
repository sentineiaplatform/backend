package com.sentineia.complaint.complaint;

import com.sentineia.base.BaseService;
import com.sentineia.complaint.category.ComplaintCategory;
import com.sentineia.complaint.category.ComplaintCategoryRepository;
import com.sentineia.complaint.status.ComplaintStatus;
import com.sentineia.complaint.status.ComplaintStatusRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComplaintService extends BaseService<Complaint> {

    private final ComplaintCategoryRepository complaintCategoryRepository;
    private final ComplaintStatusRepository complaintStatusRepository;

    public ComplaintService(
            ComplaintRepository repository,
            ComplaintCategoryRepository complaintCategoryRepository,
            ComplaintStatusRepository complaintStatusRepository) {
        super(repository);
        this.complaintCategoryRepository = complaintCategoryRepository;
        this.complaintStatusRepository = complaintStatusRepository;
    }

    @Override
    @Transactional
    public Complaint save(Complaint entity) {
        if (entity.getCategory() == null || entity.getCategory().getId() == null) {
            throw new IllegalArgumentException("Complaint category is required.");
        }
        if (entity.getStatus() == null || entity.getStatus().getId() == null) {
            throw new IllegalArgumentException("Complaint status is required.");
        }
        ComplaintCategory managedCategory = complaintCategoryRepository
                .findById(entity.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown complaint category id."));
        ComplaintStatus managedStatus = complaintStatusRepository
                .findById(entity.getStatus().getId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown complaint status id."));
        entity.setCategory(managedCategory);
        entity.setStatus(managedStatus);
        return super.save(entity);
    }
}
