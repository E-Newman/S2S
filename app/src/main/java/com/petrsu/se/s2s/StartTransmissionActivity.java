package com.petrsu.se.s2s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.app.Notification;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class StartTransmissionActivity extends AppCompatActivity {
    public String addr;
    private static final int NOTIFY_ID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_transmission);
        Intent intent = getIntent();
        addr = intent.getStringExtra("addr");
    }

    public void startTransmission(View view) {
        TVStatusChecker tvc = new TVStatusChecker();
        tvc.execute(addr);

        try {
            tvc.get(3500, TimeUnit.MILLISECONDS);
        } // wait for timeout; #TODO progressbar
        catch (Exception e) {
            Toast.makeText(this, "При попытке подключения произошла ошибка", Toast.LENGTH_SHORT).show();
        }

        if (tvc.tvStatus == 0) {
            StopNotificationChannel nc = new StopNotificationChannel(this);

            Notification.Builder nb = nc.
                    getAndroidChannelNotification("S2S", "Идёт трансляция экрана. Нажмите, чтобы остановить");


            nc.getManager().notify(NOTIFY_ID, nb.build());

            /*background mode */
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        } else Toast.makeText(this, "Ошибка: " + tvc.tvStatus, Toast.LENGTH_SHORT).show();
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

