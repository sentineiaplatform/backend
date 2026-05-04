package com.sentineia.complaint.department;

import com.sentineia.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/** Departamento responsável pelo tratamento de uma denúncia. */
@Entity
@Table(name = "complaint_departments")
@Getter
@Setter
public class ComplaintDepartment extends BaseEntity {

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
