package com.aqvp.platform.verification.exception;

/**
 * Exception thrown for malformed or unsupported AQVP QR payloads.
 */
public class InvalidQrException extends BusinessException {

    public InvalidQrException(String message) {
        super(message);
    }
}
