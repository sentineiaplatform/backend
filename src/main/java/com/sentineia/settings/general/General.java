package com.sentineia.settings.general;

import com.sentineia.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/** Configurações gerais da instância (registo único na tabela {@code general}). */
@Entity
@Table(name = "general")
@Getter
@Setter
public class General extends BaseEntity {

    @Column(name = "organization_name", nullable = false, length = 160)
    private String organizationName = "";

    @Column(nullable = false, length = 20)
    private String locale = "pt-BR";

    @Column(name = "date_format", nullable = false, length = 20)
    private String dateFormat = "dd/MM/yyyy";

    @Column(name = "default_timezone", nullable = false, length = 100)
    private String defaultTimezone = "America/Sao_Paulo";

    /** {@code light}, {@code dark} ou {@code system} (next-themes). */
    @Column(name = "theme", nullable = false, length = 20)
    private String theme = "system";

    @Column(name = "ui_zoom", nullable = false, length = 10)
    private String uiZoom = "100";
}
