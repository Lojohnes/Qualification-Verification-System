package com.aqvp.platform.verification.exception;

/**
 * Base exception for domain rule violations.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
