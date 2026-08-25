package com.aqvp.platform.admin.service.provider;

import com.aqvp.platform.admin.domain.Notification;

public interface NotificationProvider {

    boolean send(Notification notification);
}
