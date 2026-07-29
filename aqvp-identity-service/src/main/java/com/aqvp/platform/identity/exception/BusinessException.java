package com.aqvp.platform.identity.exception;

/**
 * Base runtime exception for business-level errors in the identity module.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
