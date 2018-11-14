package com.petrsu.se.s2s;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.content.Intent;
import android.app.PendingIntent;

public class StopNotificationChannel extends ContextWrapper {
    private NotificationManager mManager;

    public StopNotificationChannel(Context base) {
        super(base);
        createChannel();
    }

    public void createChannel() {
        // create android channel
        NotificationChannel androidChannel = new NotificationChannel("STOP_CHANNEL_ID",
                "STOP_CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(false);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(false);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(androidChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public Notification.Builder getAndroidChannelNotification(String title, String body) {
        Intent myIntent = new Intent(this, StopNotificationChannel.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        return new Notification.Builder(getApplicationContext(), "STOP_CHANNEL_ID")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setAutoCancel(true)
                .setColor(Color.GREEN)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                .addAction(android.R.drawable.ic_menu_camera, "ЗАВЕРШИТЬ", pendingIntent); // TODO: set stop transmission

    }
}
