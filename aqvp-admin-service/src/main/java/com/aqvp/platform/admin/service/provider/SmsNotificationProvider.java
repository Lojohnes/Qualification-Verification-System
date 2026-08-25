package com.aqvp.platform.admin.service.provider;

import com.aqvp.platform.admin.domain.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev & !test")
@Slf4j
public class SmsNotificationProvider implements NotificationProvider {

    @Override
    public boolean send(Notification notification) {
        notification.setProviderReference("sms-provider");
        notification.setProviderResponse("queued");
        log.info("SMS notification queued: recipient={}", notification.getRecipient());
        return true;
    }
}
