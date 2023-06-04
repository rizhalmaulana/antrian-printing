package com.rizal.antrianprinting.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.rizal.antrianprinting.LoginActivity;
import com.rizal.antrianprinting.MainActivity;
import com.rizal.antrianprinting.R;

public class NotificationHelper {
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    public NotificationHelper(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void createNotification(String title, String message) {
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message)
                .setBigContentTitle(title)
                .setSummaryText(message))
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultPendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(false);
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }

    public void cancelNotif() {
        mNotificationManager.cancel(0);
    }
}
