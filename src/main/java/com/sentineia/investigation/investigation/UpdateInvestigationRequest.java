package com.sentineia.investigation.investigation;

import java.time.Instant;

public record UpdateInvestigationRequest(
        String triageDecision,
        String triageDecisionReason,
        Boolean restrictedAccess,
        String factsSummary,
        String legalBasis,
        String outcome,
        Boolean impactFinancial,
        Boolean impactReputational,
        Boolean impactRegulatory,
        String closureJustification,
        Instant closedAt,
        String leadInvestigatorName
) {}
