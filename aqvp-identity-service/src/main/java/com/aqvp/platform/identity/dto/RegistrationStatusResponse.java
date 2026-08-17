package com.aqvp.platform.identity.dto;

/**
 * Indicates whether public self-registration is currently available.
 * It is only available before the first account has been created.
 */
public record RegistrationStatusResponse(boolean available) {
}
