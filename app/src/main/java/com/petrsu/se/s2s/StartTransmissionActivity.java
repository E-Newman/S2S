package com.petrsu.se.s2s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.app.Notification;
import android.widget.TextView;
import android.widget.Button;

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
        Button buttonStartTransmission = (Button) findViewById(R.id.buttonStartTransmission);
        buttonStartTransmission.setText("Начать трансляцию на " + addr);
    }

    public void startTransmission(View view) {
        Button buttonStartTransmission = (Button) findViewById(R.id.buttonStartTransmission);
        TextView textStatus = (TextView) findViewById(R.id.textStatus);

        TVStatusChecker tvc = new TVStatusChecker();
        tvc.execute(addr);

        try {
            tvc.get(3500, TimeUnit.MILLISECONDS);
        } // wait for timeout
        catch (Exception e) {
            textStatus.setText(tvc.tvStatus);
        }

        //if (tvc.tvStatus == "Соединение установлено") { // раскомментить, когда будем перекидываться сообщениями
            textStatus.setVisibility(View.INVISIBLE);
            StopNotificationChannel nc = new StopNotificationChannel(this, addr);

            Notification.Builder nb = nc.
                    getAndroidChannelNotification("S2S", "Идёт трансляция экрана. Нажмите, чтобы остановить");

            nc.getManager().notify(NOTIFY_ID, nb.build());

            buttonStartTransmission.setText("Идёт трансляция на " + addr);
            buttonStartTransmission.setEnabled(false); // prohibit to start again or return before stop
            Button buttonGoBackST = (Button) findViewById(R.id.buttonGoBackST);
            buttonGoBackST.setEnabled(false);
            Button buttonBackToMainST = (Button) findViewById(R.id.buttonBackToMainST);
            buttonBackToMainST.setEnabled(false);

            /*background mode; doesn't fit <8.0 */
            /*Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);*/

            /* start transmission */
            DataTransfer dt = new DataTransfer((Button) findViewById(R.id.buttonStartTransmission));
            dt.execute(addr);
        } //else textStatus.setText(tvc.tvStatus);
    //}

    public void goBack(View view) { // TODO: check the mode and load the proper screen
        Intent intent = new Intent(StartTransmissionActivity.this, EnterIP_Activity.class);
        startActivity(intent);
    }

    public void goToMainMenu(View view) {
        Intent intent = new Intent(StartTransmissionActivity.this, MainMenu.class);
        startActivity(intent);
    }
}

