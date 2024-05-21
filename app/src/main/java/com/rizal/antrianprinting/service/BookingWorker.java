package com.rizal.antrianprinting.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rizal.antrianprinting.R;
import com.rizal.antrianprinting.activity.RiwayatBookingActivity;
import com.rizal.antrianprinting.models.antrian.AntrianFlagging;
import com.rizal.antrianprinting.utils.Preferences;

import java.util.concurrent.TimeUnit;

public class BookingWorker extends Worker {
    private static final String CHANNEL_ID = "booking_channel";

    public BookingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        sendNotification("Antrian kamu sedang berjalan, konfirmasi antrianmu disini!");
        scheduleCancellation();
        return Result.success();
    }

    private void scheduleCancellation() {
        AntrianFlagging prefAntrianFlagging = Preferences.getAntrianFlagging(getApplicationContext());
        int antrianId = prefAntrianFlagging.getId_antrian();

        WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag("cancelAntrian");

        Data itemAntrianId = new Data.Builder()
                .putInt("antrianId", antrianId)
                .build();

        OneTimeWorkRequest cancelWorkRequest = new OneTimeWorkRequest.Builder(CancelAntrianWorker.class)
                .setInitialDelay(20, TimeUnit.MINUTES)
                .setInputData(itemAntrianId)
                .addTag("cancelAntrian")
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(cancelWorkRequest);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(getApplicationContext(), RiwayatBookingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_information) // Replace with your app's notification icon
                .setContentTitle("Booking Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Booking Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

}
