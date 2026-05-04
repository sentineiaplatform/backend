package com.sentineia.investigation.action;

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

import java.time.LocalDate;

@Entity
@Table(name = "investigation_corrective_actions")
@Getter
@Setter
public class CorrectiveAction extends BaseEntity {

    @JsonIgnore
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "investigation_id", nullable = false)
    private Investigation investigation;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Size(max = 200)
    @Column(length = 200)
    private String responsible;

    private LocalDate dueDate;

    /** Status: OPEN, IN_PROGRESS, DONE. */
    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String status = "OPEN";
}
