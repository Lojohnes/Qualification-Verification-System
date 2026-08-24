package com.aqvp.platform.verification.dto;

/**
 * Field-level comparison detail for a verification result.
 */
public record MatchDetailDto(
    String field,
    String submitted,
    boolean matched
) {}
