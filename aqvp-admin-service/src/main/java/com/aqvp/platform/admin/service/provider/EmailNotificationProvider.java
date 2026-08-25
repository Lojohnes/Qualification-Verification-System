package com.aqvp.platform.admin.service.provider;

import com.aqvp.platform.admin.domain.Notification;
import com.aqvp.platform.admin.domain.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "aqvp.notifications.mock", havingValue = "false")
@RequiredArgsConstructor
public class EmailNotificationProvider implements NotificationProvider {

    private final JavaMailSender mailSender;

    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL;
    }

    public boolean send(Notification notification) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getSubject() == null ? notification.getTemplateCode() : notification.getSubject());
        message.setText(notification.getMessage());
        mailSender.send(message);
        notification.setProviderReference("smtp");
        notification.setProviderResponse("accepted by SMTP server");
        return true;
    }
}
