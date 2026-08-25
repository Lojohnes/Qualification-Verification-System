package com.aqvp.platform.admin.service.provider;

import com.aqvp.platform.admin.domain.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
@Slf4j
public class MockNotificationProvider implements NotificationProvider {

    @Override
    public boolean send(Notification notification) {
        notification.setProviderReference("mock-provider-" + notification.getTemplateCode());
        notification.setProviderResponse("DISPATCHED");
        log.info("Mock notification dispatched: channel={}, recipient={}", notification.getChannel(), notification.getRecipient());
        return true;
    }
}
