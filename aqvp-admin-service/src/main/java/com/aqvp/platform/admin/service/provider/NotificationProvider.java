package com.aqvp.platform.admin.service.provider;

import com.aqvp.platform.admin.domain.Notification;
import com.aqvp.platform.admin.domain.NotificationChannel;

public interface NotificationProvider {

    boolean supports(NotificationChannel channel);

    boolean send(Notification notification);
}
