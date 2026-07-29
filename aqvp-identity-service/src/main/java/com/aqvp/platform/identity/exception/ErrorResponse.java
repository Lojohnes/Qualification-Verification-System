package com.aqvp.platform.identity.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response payload returned by the global exception handler.
 */
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> fieldErrors
) {
    public ErrorResponse {
        fieldErrors = fieldErrors == null ? Map.of() : Map.copyOf(fieldErrors);
    }
}
