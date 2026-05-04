package com.sentineia.complaint.priority;

import com.sentineia.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/** Prioridade de atendimento de uma denúncia (ex.: P1 — urgente, P2 — média, P3 — rotina). */
@Entity
@Table(name = "complaint_priorities")
@Getter
@Setter
public class ComplaintPriority extends BaseEntity {

    /** Código curto exibido na interface — ex.: {@code P1}, {@code P2}, {@code P3}. */
    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, length = 10, unique = true)
    private String code;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
