package com.aqvp.platform.verification.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Standard API error response body.
 */
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> fieldErrors
) {}
