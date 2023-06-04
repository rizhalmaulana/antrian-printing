package com.rizal.antrianprinting.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rizal.antrianprinting.utils.NotificationHelper;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    public static final String TYPE_NEWS = "NEWS";
    public static final String TYPE_OTHER = "OTHER";
    public static final String USER_UPDATED = "USER_UPDATED";

    private NotificationHelper notificationHelper;

    @Override
    public void onCreate() {
        this.notificationHelper = new NotificationHelper(this);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d(TAG, "data: " + message.getNotification());

        if (message.getNotification() != null) {

            Log.d(TAG, "data: " + message.getNotification());
            Log.d(TAG, "data: " + message.getData());

            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            // Handle the notification
            notificationHelper.createNotification(title, body);
        }

        if (message.getData().size() > 0) {
            // Handle the data payload
            Map<String, String> data = message.getData();
            handleDataPayload(data);
        }
    }

    private void handleDataPayload(Map<String, String> data) {
        String customData = data.get("custom_data");
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }
}