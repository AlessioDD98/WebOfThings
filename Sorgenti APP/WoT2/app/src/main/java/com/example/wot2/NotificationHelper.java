package com.example.wot2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper extends ContextWrapper {

    public static final String canale1ID = "Rilevazione";

    private NotificationManagerCompat NM;


    public NotificationHelper(Context base) {
        super(base);
        createChannel();
    }

    public void createChannel() {
        NotificationChannel canale1 = new NotificationChannel(canale1ID, canale1ID,
                NotificationManager.IMPORTANCE_DEFAULT);
        canale1.enableLights(true);
        canale1.enableVibration(true);
        canale1.setLightColor(R.color.colorPrimary);
        canale1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(canale1);
    }

    public NotificationManagerCompat getManager() {
        if (NM == null) {
            NM = NotificationManagerCompat.from(getApplicationContext());
        }
        return NM;
    }

    public NotificationCompat.Builder getCanale1NotificationMonitoriaggio(String titolo, String messaggio) {
        return new NotificationCompat.Builder(this,canale1ID)
                .setSmallIcon(R.drawable.ic_importante)
                .setContentTitle(titolo)
                .setContentText(messaggio)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messaggio));
    }
}
