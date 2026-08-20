package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.domain.VerificationConfidence;
import com.aqvp.platform.verification.domain.VerificationOutcome;
import com.aqvp.platform.verification.dto.MatchDetailDto;
import java.util.List;

/**
 * Internal field matching decision.
 */
public record VerificationMatchResult(
    VerificationOutcome outcome,
    VerificationConfidence confidence,
    int matchScore,
    List<MatchDetailDto> matchDetails,
    String failureCode,
    String failureMessage
) {}
