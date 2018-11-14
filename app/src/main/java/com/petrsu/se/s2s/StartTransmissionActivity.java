package com.petrsu.se.s2s;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.graphics.Color;

public class StartTransmissionActivity extends AppCompatActivity {
    public String addr;
    private static final int NOTIFY_ID = 101;

    private class StopNotificationChannel extends ContextWrapper {
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
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(androidChannel);
        }

        private NotificationManager getManager() {
            if (mManager == null) {
                mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            return mManager;
        }

        public Notification.Builder getAndroidChannelNotification(String title, String body) {
            return new Notification.Builder(getApplicationContext(), "STOP_CHANNEL_ID")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setAutoCancel(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_transmission);
        Intent intent = getIntent();
        addr = intent.getStringExtra("addr");
    }

    public void startTransmission(View view) {
        StopNotificationChannel nc = new StopNotificationChannel(this);

        Notification.Builder nb = nc.
                getAndroidChannelNotification("S2S", "Dratuti");

        nc.getManager().notify(101, nb.build());

        /*background mode */
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

        DataTransfer dt = new DataTransfer();
        dt.execute(addr);
    }

    public void goBack(View view) { // TODO: check the mode and load the proper screen
        Intent intent = new Intent(StartTransmissionActivity.this, EnterIP_Activity.class);
        startActivity(intent);
    }

    public void goToMainMenu(View view) {
        Intent intent = new Intent(StartTransmissionActivity.this, MainMenu.class);
        startActivity(intent);
    }
}

