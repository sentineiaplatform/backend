package com.sentineia.complaint.complaint;

import com.sentineia.base.BaseService;
import com.sentineia.complaint.category.ComplaintCategory;
import com.sentineia.complaint.category.ComplaintCategoryRepository;
import com.sentineia.complaint.status.ComplaintStatus;
import com.sentineia.complaint.status.ComplaintStatusRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class ComplaintService extends BaseService<Complaint> {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final char[] PROTOCOL_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int PROTOCOL_LENGTH = 12;

    private final ComplaintRepository complaintRepository;
    private final ComplaintCategoryRepository complaintCategoryRepository;
    private final ComplaintStatusRepository complaintStatusRepository;

    public ComplaintService(
            ComplaintRepository repository,
            ComplaintCategoryRepository complaintCategoryRepository,
            ComplaintStatusRepository complaintStatusRepository) {
        super(repository);
        this.complaintRepository = repository;
        this.complaintCategoryRepository = complaintCategoryRepository;
        this.complaintStatusRepository = complaintStatusRepository;
    }

    /** Gera {@code DEN-XXXXXXXXXXXX} com {@value #PROTOCOL_LENGTH} caracteres alfanuméricos maiúsculos aleatórios, garantindo unicidade. Ex.: {@code DEN-4K9BZ2MR73QA}. */
    private String generateUniqueProtocol() {
        String protocol;
        do {
            StringBuilder sb = new StringBuilder(PROTOCOL_LENGTH);
            for (int i = 0; i < PROTOCOL_LENGTH; i++) {
                sb.append(PROTOCOL_CHARS[SECURE_RANDOM.nextInt(PROTOCOL_CHARS.length)]);
            }
            protocol = "DEN-" + sb;
        } while (complaintRepository.existsByProtocol(protocol));
        return protocol;
    }

    public Optional<Complaint> findByProtocol(String protocol) {
        return complaintRepository.findByProtocol(protocol);
    }

    @Override
    @Transactional
    public Complaint save(Complaint entity) {
        if (entity.getProtocol() == null || entity.getProtocol().isBlank()) {
            entity.setProtocol(generateUniqueProtocol());
        }
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
