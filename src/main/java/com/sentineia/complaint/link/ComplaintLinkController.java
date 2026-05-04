package com.sentineia.complaint.link;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/complaints/{complaintId}/links")
public class ComplaintLinkController {

    private final ComplaintLinkService service;

    public ComplaintLinkController(ComplaintLinkService service) {
        this.service = service;
    }

    @GetMapping
    public List<ComplaintLink> list(@PathVariable UUID complaintId) {
        return service.findAllByComplaintId(complaintId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintLink create(@PathVariable UUID complaintId, @RequestBody CreateLinkRequest req) {
        UUID targetId = req.targetId() != null ? req.targetId() : resolveByProtocol(req.targetProtocol());
        return service.create(complaintId, targetId, req.linkType(), req.note());
    }

    @DeleteMapping("/{linkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID complaintId, @PathVariable UUID linkId) {
        service.delete(complaintId, linkId);
    }

    private UUID resolveByProtocol(String protocol) {
        if (protocol == null || protocol.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "targetId ou targetProtocol são obrigatórios.");
        }
        return service.findByProtocol(protocol);
    }

    public record CreateLinkRequest(UUID targetId, String targetProtocol, String linkType, String note) {}
}
