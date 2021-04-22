package com.example.alarmacasablanca;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class ExampleService extends Service {

    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        //Intent notificationIntent = new Intent(this, MainActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this,
        //      0, notificationIntent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.ic_launcher_foreground);
        String channelId = "alarma_id";
 /*  NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "notify_002");
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Bienvenido");
        bigText.setBigContentTitle("Procurar mantener APP abierta");

        mBuilder.setLargeIcon(largeIcon);
        mBuilder.setSmallIcon(R.mipmap.ic_casablanca_round);
        mBuilder.setContentTitle("Bienvenido");
        mBuilder.setContentText("Procurar mantener APP abierta");
        mBuilder.setPriority(Notification.PRIORITY_MIN);
        mBuilder.setLights(0xff0000ff, 300, 1000);
        mBuilder.setStyle(bigText);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "alarma",
                    NotificationManager.IMPORTANCE_HIGH);
                    channel.enableVibration(true);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
        mNotificationManager.notify(700, mBuilder.build());

*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            chan.setVibrationPattern((new long[]{0}));
            chan.enableVibration(true);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setOngoing(true)
                    .setContentTitle("Sistema FUNCIONANDO")
                    .setContentText("Mantener APP abierta")
                    .setSmallIcon(R.mipmap.ic_casablanca_round)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            startForeground(1, notification);
        }
        else{
            Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("Sistema FUNCIONANDO")
                    .setContentText("Mantener APP abierta")
                    .setVibrate((new long[]{0}))
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.mipmap.ic_casablanca_round)
                    //.setContentIntent(pendingIntent)
                    .setChannelId(channelId)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            startForeground(1, notification);
        }








        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}