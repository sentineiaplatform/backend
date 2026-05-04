package com.sentineia.investigation.comment;

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

@Entity
@Table(name = "investigation_comments")
@Getter
@Setter
public class InternalComment extends BaseEntity {

    @JsonIgnore
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "investigation_id", nullable = false)
    private Investigation investigation;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String authorName;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;
}
