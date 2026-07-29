package com.aqvp.platform.identity.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global REST exception handler for the identity module.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
        ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null));
    }

    @ExceptionHandler({DuplicateResourceException.class, UserAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
        BusinessException ex, HttpServletRequest request) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null));
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
        RuntimeException ex, HttpServletRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials", request, null));
    }

    @ExceptionHandler({InvalidTokenException.class, TokenRefreshException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
        BusinessException ex, HttpServletRequest request) {
        log.warn("Token error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null));
    }

    @ExceptionHandler(WeakPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleWeakPassword(
        WeakPasswordException ex, HttpServletRequest request) {
        log.warn("Weak password: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null));
    }

    @ExceptionHandler(ApiClientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleApiClientAuthentication(
        ApiClientAuthenticationException ex, HttpServletRequest request) {
        log.warn("API client authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(buildErrorResponse(HttpStatus.FORBIDDEN, "Access denied", request, null));
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleDisabled(
        DisabledException ex, HttpServletRequest request) {
        log.warn("Disabled account: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(buildErrorResponse(HttpStatus.FORBIDDEN, "Account is disabled", request, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex, HttpServletRequest request) {
        final Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation failed for {}: {}", request.getRequestURI(), fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException ex, HttpServletRequest request) {
        final Map<String, String> fieldErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
            fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        log.warn("Constraint violation for {}: {}", request.getRequestURI(), fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors));
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
        AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication required", request, null));
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBusinessException(
        BusinessException ex, HttpServletRequest request) {
        log.warn("Business error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, null));
    }

    private ErrorResponse buildErrorResponse(
        HttpStatus status, String message, HttpServletRequest request, Map<String, String> fieldErrors) {
        return new ErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI(),
            fieldErrors
        );
    }
}
