package com.sentineia.investigation.involved;

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
@Table(name = "investigation_involved_parties")
@Getter
@Setter
public class InvolvedParty extends BaseEntity {

    @JsonIgnore
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "investigation_id", nullable = false)
    private Investigation investigation;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String name;

    @Size(max = 100)
    @Column(length = 100)
    private String roleTitle;

    @Size(max = 100)
    @Column(length = 100)
    private String area;

    /** Tipo: ACCUSED, WITNESS, VICTIM. */
    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String partyType = "ACCUSED";
}
