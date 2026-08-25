package com.aqvp.platform.admin.service.provider;

import com.aqvp.platform.admin.domain.Notification;
import com.aqvp.platform.admin.domain.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "aqvp.notifications.mock", havingValue = "true", matchIfMissing = true)
@Slf4j
public class MockNotificationProvider implements NotificationProvider {

    @Override
    public boolean supports(NotificationChannel channel) {
        return true;
    }

    public boolean send(Notification notification) {
        notification.setProviderReference("mock-provider-" + notification.getTemplateCode());
        notification.setProviderResponse("DISPATCHED");
        log.info("Mock notification dispatched: channel={}, recipient={}", notification.getChannel(), notification.getRecipient());
        return true;
    }
}
