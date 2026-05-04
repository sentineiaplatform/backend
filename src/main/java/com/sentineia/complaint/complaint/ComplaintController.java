package com.sentineia.complaint.complaint;

import com.sentineia.base.BaseController;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController extends BaseController<Complaint, ComplaintService> {

    public ComplaintController(ComplaintService service) {
        super(service);
    }

    @PostMapping
    @Override
    public ResponseEntity<Complaint> create(@Valid @RequestBody Complaint entity) {
        return super.create(entity);
    }

    @GetMapping("/protocol/{protocol}")
    public ResponseEntity<Complaint> findByProtocol(@PathVariable String protocol) {
        return service().findByProtocol(protocol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
