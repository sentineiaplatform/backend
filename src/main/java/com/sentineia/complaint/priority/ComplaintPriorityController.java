package com.sentineia.complaint.priority;

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
@RequestMapping("/api/complaint-priorities")
public class ComplaintPriorityController extends BaseController<ComplaintPriority, ComplaintPriorityService> {

    public ComplaintPriorityController(ComplaintPriorityService service) {
        super(service);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintPriority> update(
            @PathVariable UUID id, @Valid @RequestBody ComplaintPriority body) {
        return service()
                .update(id, body)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Override
    public ResponseEntity<ComplaintPriority> create(@Valid @RequestBody ComplaintPriority entity) {
        return super.create(entity);
    }
}
