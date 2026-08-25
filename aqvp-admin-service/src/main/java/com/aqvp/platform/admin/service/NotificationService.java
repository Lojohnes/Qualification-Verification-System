package com.aqvp.platform.admin.service;

import com.aqvp.platform.admin.domain.Notification;
import com.aqvp.platform.admin.domain.NotificationChannel;

public interface NotificationService {

    Notification sendNotification(String templateCode,
                                NotificationChannel channel,
                                String recipient,
                                String subject,
                                String message);
}
