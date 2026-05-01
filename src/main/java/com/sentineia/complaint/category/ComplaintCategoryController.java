package com.sentineia.complaint.category;

import com.sentineia.base.BaseController;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/complaint-categories")
public class ComplaintCategoryController extends BaseController<ComplaintCategory, ComplaintCategoryService> {

    public ComplaintCategoryController(ComplaintCategoryService service) {
        super(service);
    }

    @PostMapping
    @Override
    public ResponseEntity<ComplaintCategory> create(@Valid @RequestBody ComplaintCategory entity) {
        return super.create(entity);
    }
}
