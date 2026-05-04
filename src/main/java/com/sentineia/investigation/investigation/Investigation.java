package com.sentineia.investigation.investigation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sentineia.base.BaseEntity;
import com.sentineia.complaint.complaint.Complaint;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "investigations")
@Getter
@Setter
public class Investigation extends BaseEntity {

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Complaint complaint;

    /** Decisão de triagem: FORMAL, CORRECTIVE, COMMITTEE, ARCHIVED. */
    @Column(length = 30)
    private String triageDecision;

    @Column(columnDefinition = "TEXT")
    private String triageDecisionReason;

    @Column(nullable = false)
    private boolean restrictedAccess = false;

    @Column(columnDefinition = "TEXT")
    private String factsSummary;

    @Column(columnDefinition = "TEXT")
    private String legalBasis;

    /** Resultado final: PROCEDENTE, IMPROCEDENTE, PARCIAL. */
    @Column(length = 30)
    private String outcome;

    @Column(nullable = false)
    private boolean impactFinancial = false;

    @Column(nullable = false)
    private boolean impactReputational = false;

    @Column(nullable = false)
    private boolean impactRegulatory = false;

    @Column(columnDefinition = "TEXT")
    private String closureJustification;

    private Instant closedAt;

    @Column(length = 200)
    private String leadInvestigatorName;
}
