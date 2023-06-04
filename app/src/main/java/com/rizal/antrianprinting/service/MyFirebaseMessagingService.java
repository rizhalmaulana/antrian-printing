package com.rizal.antrianprinting.service;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rizal.antrianprinting.MainActivity;
import com.rizal.antrianprinting.models.notification.NotificationModel;
import com.rizal.antrianprinting.utils.NotificationHelper;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    private NotificationHelper notificationHelper;

    @Override
    public void onCreate() {
        this.notificationHelper = new NotificationHelper(this);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if (message.getData().size() > 0) {
            Log.d(TAG, "Data Notification: " + message.getData());

            // Handle the data payload
            Map<String, String> data = message.getData();
            handleDataPayload(data);
        } else {
            Log.d(TAG, "Data Notification: " + message.getNotification());

            handleNotification(message.getNotification());
        }
    }

    private void handleNotification(RemoteMessage.Notification notification) {
        String title = notification.getTitle();
        String message = notification.getBody();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTitle(title);
        notificationModel.setMessage(message);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        notificationHelper = new NotificationHelper(getApplicationContext());
        notificationHelper.displayNotification(notificationModel, intent);
    }

    private void handleDataPayload(Map<String, String> data) {
        String title = data.get("title");
        String message = data.get("message");
        String image = data.get("image");
        String action = data.get("action");
        String actionDestination = data.get("action_destination");

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTitle(title);
        notificationModel.setMessage(message);
        notificationModel.setImage(image);
        notificationModel.setAction(action);
        notificationModel.setAction_destination(actionDestination);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        notificationHelper = new NotificationHelper(getApplicationContext());
        notificationHelper.displayNotification(notificationModel, intent);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "Refreshed token: " + token);
    }
}