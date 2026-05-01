package com.sentineia.complaint.status;

import java.util.UUID;

import com.sentineia.base.BaseController;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/complaint-status")
public class ComplaintStatusController extends BaseController<ComplaintStatus, ComplaintStatusService> {

    public ComplaintStatusController(ComplaintStatusService service) {
        super(service);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintStatus> update(
            @PathVariable UUID id, @Valid @RequestBody ComplaintStatus body) {
        return service()
                .update(id, body)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Override
    public ResponseEntity<ComplaintStatus> create(@Valid @RequestBody ComplaintStatus entity) {
        return super.create(entity);
    }
}
