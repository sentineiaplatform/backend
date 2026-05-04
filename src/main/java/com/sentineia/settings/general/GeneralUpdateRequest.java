package com.sentineia.settings.general;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GeneralUpdateRequest(
        @NotBlank @Size(max = 160) String organizationName,
        @NotBlank @Pattern(regexp = "pt-BR|en-US") String locale,
        @NotBlank @Pattern(regexp = "dd/MM/yyyy|yyyy-MM-dd|MM/dd/yyyy") String dateFormat,
        @NotBlank @Size(max = 100) String defaultTimezone,
        @NotBlank @Pattern(regexp = "light|dark|system") String theme,
        @NotBlank @Pattern(regexp = "90|100|110|125") String uiZoom) {}
