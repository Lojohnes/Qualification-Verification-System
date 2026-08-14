package com.aqvp.platform.qualification.exception;

/**
 * Base exception for business logic errors.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
