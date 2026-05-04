package com.sentineia.complaint.department;

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
@RequestMapping("/api/complaint-departments")
public class ComplaintDepartmentController extends BaseController<ComplaintDepartment, ComplaintDepartmentService> {

    public ComplaintDepartmentController(ComplaintDepartmentService service) {
        super(service);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintDepartment> update(
            @PathVariable UUID id, @Valid @RequestBody ComplaintDepartment body) {
        return service()
                .update(id, body)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Override
    public ResponseEntity<ComplaintDepartment> create(@Valid @RequestBody ComplaintDepartment entity) {
        return super.create(entity);
    }
}
