package com.sferadev.geekthetime.companion;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import static com.sferadev.geekthetime.companion.Utils.*;

public class NotificationService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification mNotification = sbn.getNotification();
        if (mNotification != null) {
            //Do Stuff
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}