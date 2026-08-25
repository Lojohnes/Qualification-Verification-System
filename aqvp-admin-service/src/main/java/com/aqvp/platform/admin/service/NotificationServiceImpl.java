package com.aqvp.platform.admin.service;

import com.aqvp.platform.admin.domain.Notification;
import com.aqvp.platform.admin.domain.NotificationChannel;
import com.aqvp.platform.admin.domain.NotificationStatus;
import com.aqvp.platform.admin.repository.NotificationRepository;
import com.aqvp.platform.admin.service.provider.NotificationProvider;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final List<NotificationProvider> notificationProviders;

    @Override
    @Transactional
    public Notification sendNotification(String templateCode,
                                       NotificationChannel channel,
                                       String recipient,
                                       String subject,
                                       String message) {
        final Notification notification = Notification.builder()
            .templateCode(templateCode)
            .channel(channel)
            .recipient(recipient)
            .subject(subject)
            .message(message)
            .status(NotificationStatus.PENDING)
            .attempts(0)
            .build();

        final Notification saved = notificationRepository.save(notification);

        try {
            final NotificationProvider provider = notificationProviders.stream()
                .filter(candidate -> candidate.supports(channel))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No provider configured for channel " + channel));
            final boolean delivered = provider.send(saved);
            saved.setStatus(delivered ? NotificationStatus.SENT : NotificationStatus.FAILED);
            saved.setSentAt(LocalDateTime.now());
            saved.setAttempts(saved.getAttempts() == null ? 1 : saved.getAttempts() + 1);
            return notificationRepository.save(saved);
        } catch (Exception ex) {
            log.error("Failed to send notification template={} recipient={}", templateCode, recipient, ex);
            saved.setStatus(NotificationStatus.FAILED);
            saved.setErrorMessage(ex.getMessage());
            saved.setAttempts(saved.getAttempts() == null ? 1 : saved.getAttempts() + 1);
            return notificationRepository.save(saved);
        }
    }
}
