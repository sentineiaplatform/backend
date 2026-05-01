package com.sentineia.complaint;

import com.sentineia.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "complaint_categories")
@Getter
@Setter
public class ComplaintCategory extends BaseEntity {

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;
}
