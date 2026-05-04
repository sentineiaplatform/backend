package com.sentineia.investigation.investigation;

import com.sentineia.complaint.complaint.Complaint;
import com.sentineia.complaint.complaint.ComplaintRepository;
import com.sentineia.investigation.action.CorrectiveAction;
import com.sentineia.investigation.action.CorrectiveActionRepository;
import com.sentineia.investigation.approval.ApprovalDecision;
import com.sentineia.investigation.approval.ApprovalDecisionRepository;
import com.sentineia.investigation.comment.InternalComment;
import com.sentineia.investigation.comment.InternalCommentRepository;
import com.sentineia.investigation.involved.InvolvedParty;
import com.sentineia.investigation.involved.InvolvedPartyRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class InvestigationService {

    private final InvestigationRepository investigationRepo;
    private final InternalCommentRepository commentRepo;
    private final InvolvedPartyRepository involvedRepo;
    private final CorrectiveActionRepository actionRepo;
    private final ApprovalDecisionRepository approvalRepo;
    private final ComplaintRepository complaintRepo;

    public InvestigationService(
            InvestigationRepository investigationRepo,
            InternalCommentRepository commentRepo,
            InvolvedPartyRepository involvedRepo,
            CorrectiveActionRepository actionRepo,
            ApprovalDecisionRepository approvalRepo,
            ComplaintRepository complaintRepo) {
        this.investigationRepo = investigationRepo;
        this.commentRepo = commentRepo;
        this.involvedRepo = involvedRepo;
        this.actionRepo = actionRepo;
        this.approvalRepo = approvalRepo;
        this.complaintRepo = complaintRepo;
    }

    public Optional<Investigation> findById(UUID id) {
        return investigationRepo.findById(id);
    }

    @Transactional
    public Investigation findOrCreateByComplaintId(UUID complaintId) {
        return investigationRepo.findByComplaintId(complaintId).orElseGet(() -> {
            Complaint complaint = complaintRepo.findById(complaintId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found: " + complaintId));
            Investigation inv = new Investigation();
            inv.setComplaint(complaint);
            return investigationRepo.save(inv);
        });
    }

    @Transactional
    public Optional<Investigation> update(UUID id, UpdateInvestigationRequest req) {
        return investigationRepo.findById(id).map(inv -> {
            if (req.triageDecision() != null) inv.setTriageDecision(req.triageDecision());
            if (req.triageDecisionReason() != null) inv.setTriageDecisionReason(req.triageDecisionReason());
            if (req.restrictedAccess() != null) inv.setRestrictedAccess(req.restrictedAccess());
            if (req.factsSummary() != null) inv.setFactsSummary(req.factsSummary());
            if (req.legalBasis() != null) inv.setLegalBasis(req.legalBasis());
            if (req.outcome() != null) inv.setOutcome(req.outcome());
            if (req.impactFinancial() != null) inv.setImpactFinancial(req.impactFinancial());
            if (req.impactReputational() != null) inv.setImpactReputational(req.impactReputational());
            if (req.impactRegulatory() != null) inv.setImpactRegulatory(req.impactRegulatory());
            if (req.closureJustification() != null) inv.setClosureJustification(req.closureJustification());
            if (req.closedAt() != null) inv.setClosedAt(req.closedAt());
            if (req.leadInvestigatorName() != null) inv.setLeadInvestigatorName(req.leadInvestigatorName());
            return investigationRepo.save(inv);
        });
    }

    // ── comments ─────────────────────────────────────────────────────────────

    public List<InternalComment> listComments(UUID investigationId) {
        return commentRepo.findByInvestigationIdOrderByCreatedAtAsc(investigationId);
    }

    @Transactional
    public InternalComment addComment(UUID investigationId, String authorName, String body) {
        Investigation inv = requireInvestigation(investigationId);
        InternalComment c = new InternalComment();
        c.setInvestigation(inv);
        c.setAuthorName(authorName);
        c.setBody(body);
        return commentRepo.save(c);
    }

    @Transactional
    public void deleteComment(UUID investigationId, UUID commentId) {
        commentRepo.findById(commentId).ifPresent(c -> {
            if (c.getInvestigation().getId().equals(investigationId)) {
                commentRepo.delete(c);
            }
        });
    }

    // ── involved parties ──────────────────────────────────────────────────────

    public List<InvolvedParty> listInvolved(UUID investigationId) {
        return involvedRepo.findByInvestigationIdOrderByCreatedAtAsc(investigationId);
    }

    @Transactional
    public InvolvedParty addInvolved(UUID investigationId, String name, String roleTitle, String area, String partyType) {
        Investigation inv = requireInvestigation(investigationId);
        InvolvedParty p = new InvolvedParty();
        p.setInvestigation(inv);
        p.setName(name);
        p.setRoleTitle(roleTitle);
        p.setArea(area);
        p.setPartyType(partyType != null ? partyType : "ACCUSED");
        return involvedRepo.save(p);
    }

    @Transactional
    public Optional<InvolvedParty> updateInvolved(UUID investigationId, UUID partyId, String name, String roleTitle, String area, String partyType) {
        return involvedRepo.findById(partyId).map(p -> {
            if (!p.getInvestigation().getId().equals(investigationId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (name != null) p.setName(name);
            if (roleTitle != null) p.setRoleTitle(roleTitle);
            if (area != null) p.setArea(area);
            if (partyType != null) p.setPartyType(partyType);
            return involvedRepo.save(p);
        });
    }

    @Transactional
    public void deleteInvolved(UUID investigationId, UUID partyId) {
        involvedRepo.findById(partyId).ifPresent(p -> {
            if (p.getInvestigation().getId().equals(investigationId)) {
                involvedRepo.delete(p);
            }
        });
    }

    // ── corrective actions ────────────────────────────────────────────────────

    public List<CorrectiveAction> listActions(UUID investigationId) {
        return actionRepo.findByInvestigationIdOrderByCreatedAtAsc(investigationId);
    }

    @Transactional
    public CorrectiveAction addAction(UUID investigationId, String description, String responsible, LocalDate dueDate, String status) {
        Investigation inv = requireInvestigation(investigationId);
        CorrectiveAction a = new CorrectiveAction();
        a.setInvestigation(inv);
        a.setDescription(description);
        a.setResponsible(responsible);
        a.setDueDate(dueDate);
        a.setStatus(status != null ? status : "OPEN");
        return actionRepo.save(a);
    }

    @Transactional
    public Optional<CorrectiveAction> updateAction(UUID investigationId, UUID actionId, String description, String responsible, LocalDate dueDate, String status) {
        return actionRepo.findById(actionId).map(a -> {
            if (!a.getInvestigation().getId().equals(investigationId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (description != null) a.setDescription(description);
            if (responsible != null) a.setResponsible(responsible);
            if (dueDate != null) a.setDueDate(dueDate);
            if (status != null) a.setStatus(status);
            return actionRepo.save(a);
        });
    }

    @Transactional
    public void deleteAction(UUID investigationId, UUID actionId) {
        actionRepo.findById(actionId).ifPresent(a -> {
            if (a.getInvestigation().getId().equals(investigationId)) {
                actionRepo.delete(a);
            }
        });
    }

    // ── approval decisions ────────────────────────────────────────────────────

    public List<ApprovalDecision> listApprovals(UUID investigationId) {
        return approvalRepo.findByInvestigationIdOrderByLevelOrderAsc(investigationId);
    }

    @Transactional
    public ApprovalDecision addApproval(UUID investigationId, String level, int levelOrder, String decision, String justification, String decidedBy) {
        Investigation inv = requireInvestigation(investigationId);
        ApprovalDecision d = new ApprovalDecision();
        d.setInvestigation(inv);
        d.setLevel(level);
        d.setLevelOrder(levelOrder);
        d.setDecision(decision);
        d.setJustification(justification);
        d.setDecidedBy(decidedBy);
        if (decision != null) d.setDecidedAt(java.time.Instant.now());
        return approvalRepo.save(d);
    }

    @Transactional
    public Optional<ApprovalDecision> updateApproval(UUID investigationId, UUID decisionId, String level, int levelOrder, String decision, String justification, String decidedBy) {
        return approvalRepo.findById(decisionId).map(d -> {
            if (!d.getInvestigation().getId().equals(investigationId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (level != null) d.setLevel(level);
            d.setLevelOrder(levelOrder);
            if (decision != null) { d.setDecision(decision); d.setDecidedAt(java.time.Instant.now()); }
            if (justification != null) d.setJustification(justification);
            if (decidedBy != null) d.setDecidedBy(decidedBy);
            return approvalRepo.save(d);
        });
    }

    @Transactional
    public void deleteApproval(UUID investigationId, UUID decisionId) {
        approvalRepo.findById(decisionId).ifPresent(d -> {
            if (d.getInvestigation().getId().equals(investigationId)) {
                approvalRepo.delete(d);
            }
        });
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Investigation requireInvestigation(UUID id) {
        return investigationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investigation not found: " + id));
    }
}
