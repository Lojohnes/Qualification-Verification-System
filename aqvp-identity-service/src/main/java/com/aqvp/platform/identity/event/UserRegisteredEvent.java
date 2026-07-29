package com.aqvp.platform.identity.event;

import java.util.UUID;

/**
 * Domain event published when a new user is registered.
 *
 * @param userId    the identifier of the newly registered user
 * @param username  the username of the newly registered user
 * @param email     the email address of the newly registered user
 * @param timestamp the event timestamp in epoch milliseconds
 */
public record UserRegisteredEvent(UUID userId, String username, String email, long timestamp) {
}
