package com.sentineia.settings.general;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeneralService {

    private final GeneralRepository generalRepository;

    public GeneralService(GeneralRepository generalRepository) {
        this.generalRepository = generalRepository;
    }

    @Transactional(readOnly = true)
    public GeneralResponse getOrCreate() {
        return toResponse(
                generalRepository.findTopByOrderByCreatedAtAsc().orElseGet(this::createDefaults));
    }

    @Transactional
    public GeneralResponse update(GeneralUpdateRequest request) {
        General entity =
                generalRepository.findTopByOrderByCreatedAtAsc().orElseGet(this::createDefaults);
        entity.setOrganizationName(request.organizationName().trim());
        entity.setLocale(request.locale());
        entity.setDateFormat(request.dateFormat());
        entity.setDefaultTimezone(request.defaultTimezone().trim());
        entity.setTheme(request.theme());
        entity.setUiZoom(request.uiZoom());
        return toResponse(generalRepository.save(entity));
    }

    private General createDefaults() {
        General g = new General();
        return generalRepository.save(g);
    }

    private static GeneralResponse toResponse(General g) {
        return new GeneralResponse(
                g.getId(),
                g.getOrganizationName(),
                g.getLocale(),
                g.getDateFormat(),
                g.getDefaultTimezone(),
                themeOrDefault(g.getTheme()),
                g.getUiZoom(),
                g.getUpdatedAt());
    }

    private static String themeOrDefault(String raw) {
        if (raw == null || raw.isBlank()) {
            return "system";
        }
        return raw;
    }
}
