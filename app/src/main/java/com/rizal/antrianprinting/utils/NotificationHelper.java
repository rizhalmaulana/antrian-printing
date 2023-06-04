package com.rizal.antrianprinting.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Html;

import androidx.core.app.NotificationCompat;

import com.rizal.antrianprinting.MainActivity;
import com.rizal.antrianprinting.R;
import com.rizal.antrianprinting.models.notification.NotificationModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import retrofit2.http.HTTP;

public class NotificationHelper {
    private static final String TYPE_NEWS = "NEWS";
    private static final String TYPE_OTHER = "OTHER";
    private static final String USER_UPDATED = "USER_UPDATED";

    private static final String CHANNEL_ID = "antrian-printing";
    private static final String CHANNEL_NAME = "antrian-printing";

    private Map<String, Class> map = new HashMap<>();
    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;

        map.put("mainAcitivity", MainActivity.class);
    }

    public void displayNotification(NotificationModel notificationModel, Intent resultIntent) {
        String title = notificationModel.getTitle();
        String message = notificationModel.getMessage();
        String icon = notificationModel.getImage();
        String action = notificationModel.getAction();
        String actionDest = notificationModel.getAction_destination();

        Bitmap bitmap = null;
        if (icon != null) {
            bitmap = getBitmapFromUrl(icon);
        }

        PendingIntent resultPendingIntent;
        if (action != null && action.equals("url")) {
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(actionDest));
            resultPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        } else if (action != null && action.equals("activity") && map.containsKey(actionDest)) {
            resultIntent = new Intent(context, map.get(actionDest));
            resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        Notification notification;

        if (bitmap != null) {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(message);

            notification = builder.setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(message)
                    .build();
        } else {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());

            notification = builder.setTicker(title).setWhen(0)
                    .setAutoCancel(false)
                    .setContentText(title)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(bigPictureStyle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(message)
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    private Bitmap getBitmapFromUrl(String icon) {
        try {
            URL url = new URL(icon);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);

            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);

        } catch (IOException ioException) {
            return null;
        }
    }

}
