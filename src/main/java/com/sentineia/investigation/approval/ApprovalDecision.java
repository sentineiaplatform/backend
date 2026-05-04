package com.sentineia.investigation.approval;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sentineia.base.BaseEntity;
import com.sentineia.investigation.investigation.Investigation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "investigation_approval_decisions")
@Getter
@Setter
public class ApprovalDecision extends BaseEntity {

    @JsonIgnore
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "investigation_id", nullable = false)
    private Investigation investigation;

    /** Nível de aprovação: COMPLIANCE, LEGAL, BOARD. */
    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String level;

    @Column(nullable = false)
    private int levelOrder = 0;

    /** Decisão: APPROVED, REJECTED, REVIEW. */
    @Size(max = 20)
    @Column(length = 20)
    private String decision;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Size(max = 200)
    @Column(length = 200)
    private String decidedBy;

    private Instant decidedAt;
}
