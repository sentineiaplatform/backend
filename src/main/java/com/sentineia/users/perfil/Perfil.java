package com.sentineia.users.perfil;

import com.sentineia.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "perfis")
@Getter
@Setter
public class Perfil extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 500)
    private String description;
}
