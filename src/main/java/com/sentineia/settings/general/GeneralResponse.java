package com.sentineia.settings.general;

import java.time.Instant;
import java.util.UUID;

public record GeneralResponse(
        UUID id,
        String organizationName,
        String locale,
        String dateFormat,
        String defaultTimezone,
        String theme,
        String uiZoom,
        Instant updatedAt) {}
