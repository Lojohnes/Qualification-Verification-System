package com.aqvp.platform.identity.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Placeholder listener for user lifecycle domain events.
 */
@Component
@Slf4j
public class UserEventListener {

    /**
     * Handles a {@link UserRegisteredEvent}.
     *
     * @param event the published event
     */
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("User registered event received for user '{}' ({})", event.username(), event.userId());
    }
}
