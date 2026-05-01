package com.sentineia.complaint;

import java.util.List;

import com.sentineia.base.BaseController;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/complaint-statuses")
public class ComplaintStatusController extends BaseController<ComplaintStatus, ComplaintStatusService> {

    public ComplaintStatusController(ComplaintStatusService service) {
        super(service);
    }

    @GetMapping("/ordered")
    public List<ComplaintStatus> listOrdered() {
        return service().listOrdered();
    }

    @PostMapping
    @Override
    public ResponseEntity<ComplaintStatus> create(@Valid @RequestBody ComplaintStatus entity) {
        return super.create(entity);
    }
}
