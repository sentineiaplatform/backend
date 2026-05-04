package com.sentineia.complaint.link;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sentineia.base.BaseEntity;
import com.sentineia.complaint.complaint.Complaint;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "complaint_links",
    uniqueConstraints = @UniqueConstraint(columnNames = {"source_id", "target_id", "link_type"})
)
@Getter
@Setter
public class ComplaintLink extends BaseEntity {

    /** Denúncia de origem do vínculo. */
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Complaint source;

    /** Denúncia de destino do vínculo. */
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Complaint target;

    /** Tipo de vínculo: DUPLICATE, RELATED, FOLLOW_UP. */
    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String linkType = "RELATED";

    @Column(columnDefinition = "TEXT")
    private String note;
}
