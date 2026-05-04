package com.sentineia.investigation.investigation;

import com.sentineia.investigation.action.CorrectiveAction;
import com.sentineia.investigation.approval.ApprovalDecision;
import com.sentineia.investigation.comment.InternalComment;
import com.sentineia.investigation.involved.InvolvedParty;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/investigations")
public class InvestigationController {

    private final InvestigationService service;

    public InvestigationController(InvestigationService service) {
        this.service = service;
    }

    // ── investigation ─────────────────────────────────────────────────────────

    @GetMapping("/complaint/{complaintId}")
    public ResponseEntity<Investigation> findOrCreate(@PathVariable UUID complaintId) {
        return ResponseEntity.ok(service.findOrCreateByComplaintId(complaintId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investigation> getById(@PathVariable UUID id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Investigation> update(
            @PathVariable UUID id,
            @RequestBody UpdateInvestigationRequest req) {
        return service.update(id, req).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // ── comments ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}/comments")
    public List<InternalComment> listComments(@PathVariable UUID id) {
        return service.listComments(id);
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public InternalComment addComment(@PathVariable UUID id, @RequestBody AddCommentRequest req) {
        return service.addComment(id, req.authorName(), req.body());
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable UUID id, @PathVariable UUID commentId) {
        service.deleteComment(id, commentId);
    }

    // ── involved parties ──────────────────────────────────────────────────────

    @GetMapping("/{id}/involved")
    public List<InvolvedParty> listInvolved(@PathVariable UUID id) {
        return service.listInvolved(id);
    }

    @PostMapping("/{id}/involved")
    @ResponseStatus(HttpStatus.CREATED)
    public InvolvedParty addInvolved(@PathVariable UUID id, @RequestBody InvolvedPartyRequest req) {
        return service.addInvolved(id, req.name(), req.roleTitle(), req.area(), req.partyType());
    }

    @PutMapping("/{id}/involved/{partyId}")
    public ResponseEntity<InvolvedParty> updateInvolved(
            @PathVariable UUID id,
            @PathVariable UUID partyId,
            @RequestBody InvolvedPartyRequest req) {
        return service.updateInvolved(id, partyId, req.name(), req.roleTitle(), req.area(), req.partyType())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/involved/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvolved(@PathVariable UUID id, @PathVariable UUID partyId) {
        service.deleteInvolved(id, partyId);
    }

    // ── corrective actions ────────────────────────────────────────────────────

    @GetMapping("/{id}/actions")
    public List<CorrectiveAction> listActions(@PathVariable UUID id) {
        return service.listActions(id);
    }

    @PostMapping("/{id}/actions")
    @ResponseStatus(HttpStatus.CREATED)
    public CorrectiveAction addAction(@PathVariable UUID id, @RequestBody CorrectiveActionRequest req) {
        return service.addAction(id, req.description(), req.responsible(),
                req.dueDate() != null ? LocalDate.parse(req.dueDate()) : null, req.status());
    }

    @PutMapping("/{id}/actions/{actionId}")
    public ResponseEntity<CorrectiveAction> updateAction(
            @PathVariable UUID id,
            @PathVariable UUID actionId,
            @RequestBody CorrectiveActionRequest req) {
        return service.updateAction(id, actionId, req.description(), req.responsible(),
                req.dueDate() != null ? LocalDate.parse(req.dueDate()) : null, req.status())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/actions/{actionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAction(@PathVariable UUID id, @PathVariable UUID actionId) {
        service.deleteAction(id, actionId);
    }

    // ── approval decisions ────────────────────────────────────────────────────

    @GetMapping("/{id}/approvals")
    public List<ApprovalDecision> listApprovals(@PathVariable UUID id) {
        return service.listApprovals(id);
    }

    @PostMapping("/{id}/approvals")
    @ResponseStatus(HttpStatus.CREATED)
    public ApprovalDecision addApproval(@PathVariable UUID id, @RequestBody ApprovalDecisionRequest req) {
        return service.addApproval(id, req.level(), req.levelOrder(), req.decision(), req.justification(), req.decidedBy());
    }

    @PutMapping("/{id}/approvals/{decisionId}")
    public ResponseEntity<ApprovalDecision> updateApproval(
            @PathVariable UUID id,
            @PathVariable UUID decisionId,
            @RequestBody ApprovalDecisionRequest req) {
        return service.updateApproval(id, decisionId, req.level(), req.levelOrder(), req.decision(), req.justification(), req.decidedBy())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/approvals/{decisionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApproval(@PathVariable UUID id, @PathVariable UUID decisionId) {
        service.deleteApproval(id, decisionId);
    }

    // ── request records ───────────────────────────────────────────────────────

    public record AddCommentRequest(String authorName, String body) {}

    public record InvolvedPartyRequest(String name, String roleTitle, String area, String partyType) {}

    public record CorrectiveActionRequest(String description, String responsible, String dueDate, String status) {}

    public record ApprovalDecisionRequest(String level, int levelOrder, String decision, String justification, String decidedBy) {}
}
