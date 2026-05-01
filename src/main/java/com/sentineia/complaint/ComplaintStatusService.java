package com.sentineia.complaint;

import java.util.List;

import com.sentineia.base.BaseService;

import org.springframework.stereotype.Service;

@Service
public class ComplaintStatusService extends BaseService<ComplaintStatus> {

    private final ComplaintStatusRepository complaintStatusRepository;

    public ComplaintStatusService(ComplaintStatusRepository repository) {
        super(repository);
        this.complaintStatusRepository = repository;
    }

    public List<ComplaintStatus> listOrdered() {
        return complaintStatusRepository.findAllByOrderBySortOrderAsc();
    }
}
