package com.example.wot2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class RilevazioneReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String tipo=intent.getStringExtra("Rilevazione");
        if(tipo.equals("Acceleration")){
            Log.v("notifica","ACCELERATION");
            int notificaID=5;
            NotificationHelper NH = new NotificationHelper(context);
            NotificationCompat.Builder nc = NH.getCanale1NotificationMonitoriaggio("Soglia "+tipo+" superata!",
                    "Attenzione! Hai superato la soglia impostata!");
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificaID, nc.build());
        }
        else if(tipo.equals("AccelerationSamples")){
            int notificaID=6;
            NotificationHelper NH = new NotificationHelper(context);
            NotificationCompat.Builder nc = NH.getCanale1NotificationMonitoriaggio("Soglia "+tipo+" superata!", "Attenzione! Hai superato la soglia impostata!");
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificaID, nc.build());
        }
        else if(tipo.equals("Gyroscope")){
            int notificaID=7;
            NotificationHelper NH = new NotificationHelper(context);
            NotificationCompat.Builder nc = NH.getCanale1NotificationMonitoriaggio("Soglia "+tipo+" superata!", "Attenzione! Hai superato la soglia impostata!");
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificaID, nc.build());
        }
        else if(tipo.equals("GyroscopeSamples")){
            int notificaID=8;
            NotificationHelper NH = new NotificationHelper(context);
            NotificationCompat.Builder nc = NH.getCanale1NotificationMonitoriaggio("Soglia "+tipo+" superata!", "Attenzione! Hai superato la soglia impostata!");
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificaID, nc.build());
        }
    }
}
