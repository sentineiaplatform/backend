package com.sentineia.complaint;

import com.sentineia.base.BaseController;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
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
}
