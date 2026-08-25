package com.aqvp.platform.admin.service.provider;

import com.aqvp.platform.admin.domain.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev & !test")
@Slf4j
public class EmailNotificationProvider implements NotificationProvider {

    @Override
    public boolean send(Notification notification) {
        notification.setProviderReference("smtp-provider");
        notification.setProviderResponse("queued");
        log.info("Email notification queued: recipient={}, subject={}", notification.getRecipient(), notification.getSubject());
        return true;
    }
}
