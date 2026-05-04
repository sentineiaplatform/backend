package com.sentineia.complaint.complaint;

import com.sentineia.base.BaseEntity;
import com.sentineia.complaint.category.ComplaintCategory;
import com.sentineia.complaint.department.ComplaintDepartment;
import com.sentineia.complaint.priority.ComplaintPriority;
import com.sentineia.complaint.status.ComplaintStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "complaints")
@Getter
@Setter
public class Complaint extends BaseEntity {

    /** Código de protocolo único, gerado automaticamente na criação — ex.: {@code DEN-4K9BZ2MR73QA}. */
    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30, unique = true)
    private String protocol;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String title;

    @NotBlank
    @Lob
    @Column(nullable = false)
    private String description;

    /** Canal de receção — ex.: {@code Canal web}, {@code Telefone}, {@code Presencial}, {@code E-mail}. */
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String channel;

    /** {@code true} = denunciante anónimo; {@code false} = denunciante identificado. */
    @Column(nullable = false)
    private boolean anonymous = true;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ComplaintCategory category;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ComplaintStatus status;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ComplaintPriority priority;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ComplaintDepartment department;
}
