package com.aqvp.platform.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.admin.domain.Notification;
import com.aqvp.platform.admin.domain.NotificationChannel;
import com.aqvp.platform.admin.domain.NotificationStatus;
import com.aqvp.platform.admin.repository.NotificationRepository;
import com.aqvp.platform.admin.service.provider.NotificationProvider;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationProvider notificationProvider;

    @Mock
    private List<NotificationProvider> notificationProviders;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void shouldCreateAndSendNotification() {
        final Notification notification = Notification.builder()
            .id(UUID.randomUUID())
            .templateCode("qualification-issued")
            .channel(NotificationChannel.EMAIL)
            .recipient("student@example.com")
            .subject("Qualification issued")
            .message("Your qualification has been issued.")
            .status(NotificationStatus.PENDING)
            .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationProvider.supports(NotificationChannel.EMAIL)).thenReturn(true);
        when(notificationProvider.send(any(Notification.class))).thenReturn(true);
        when(notificationProviders.stream()).thenReturn(Stream.of(notificationProvider));

        final Notification result = notificationService.sendNotification(
            "qualification-issued",
            NotificationChannel.EMAIL,
            "student@example.com",
            "Qualification issued",
            "Your qualification has been issued.");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(result.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(notificationProvider, times(1)).send(any(Notification.class));
    }
}
