package com.petrsu.se.s2s;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.app.Notification;
import android.widget.TextView;
import android.widget.Button;

import java.io.File;

public class StartTransmissionActivity extends AppCompatActivity {
    public String addr;
    private static final int NOTIFY_ID = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int RECORD_REQUEST_CODE = 103;
    private boolean working = false;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private ScreenRecorder screenRecorder;
    private DataTransfer dt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_transmission);
        Intent intent = getIntent();
        addr = intent.getStringExtra("addr");
        Button buttonStartTransmission = (Button) findViewById(R.id.buttonStartTransmission);
        buttonStartTransmission.setText("Начать трансляцию на " + addr);

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        if (ContextCompat.checkSelfPermission(StartTransmissionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }

        PackageManager p = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo i = p.getPackageInfo(s, 0);
            Log.d("PACKDIR", i.applicationInfo.dataDir);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PACKDIR", "Error Package name not found ");
        }

        File outFile = new File("/data/user/0/com.petrsu.se.s2s/record.mp4"); // TODO: shift for any devices

        Log.d("FILE", outFile.getAbsolutePath());
        if (outFile.exists()) {
            if (outFile.delete()) {
                Log.d("RECORD", "Deleted in STA");
            }  else Log.e("RECORD", "File delete issues in STA");
        }
        if (!outFile.exists()) {
            try {
                if (outFile.createNewFile()) {
                    Log.d("RECORD", "Created in STA");
                } else Log.e("RECORD", "File create issues in STA");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
        Intent recIntent = new Intent(this, ScreenRecorder.class);
        bindService(recIntent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                screenRecorder.setMediaProject(mediaProjection);
                //screenRecorder.startRecord(); // TODO: перенести на кнопку?
            } else {
                Log.e("RESULT CODE", Integer.toString(resultCode));
            }
        }
    }

    public void startTransmission(View view) {
        Button buttonStartTransmission = (Button) findViewById(R.id.buttonStartTransmission);
        TextView textStatus = (TextView) findViewById(R.id.textStatus);

        if (!working) {
            TVStatusChecker tvc = new TVStatusChecker();
            tvc.execute(addr);

            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (tvc.tvStatus.contains("Соединение установлено"))
                Log.i("FILELEN", "Ret: " + tvc.tvStatus);

        /*try {
            tvc.get(3500, TimeUnit.MILLISECONDS);
        } // wait for timeout
        catch (Exception e) {
            e.printStackTrace();
            textStatus.setText(tvc.tvStatus);
        }*/

            if (tvc.tvStatus.contains("Соединение установлено")) { // раскомментить, когда будем перекидываться сообщениями
                Log.i("FILELEN", "1");
                textStatus.setVisibility(View.INVISIBLE);
                //StopNotificationChannel nc = new StopNotificationChannel(this, addr);

                //Notification.Builder nb = nc.
                //        getAndroidChannelNotification("S2S", "Идёт трансляция экрана. Нажмите, чтобы остановить");

                //nc.getManager().notify(NOTIFY_ID, nb.build());

                Log.i("FILELEN", "2");

                buttonStartTransmission.setText("Остановить трансляцию на " + addr);
                working = true;
                //buttonStartTransmission.setEnabled(false); // prohibit to start again or return before stop
                Button buttonGoBackST = (Button) findViewById(R.id.buttonGoBackST);
                buttonGoBackST.setEnabled(false);
                Button buttonBackToMainST = (Button) findViewById(R.id.buttonBackToMainST);
                buttonBackToMainST.setEnabled(false);

                Log.i("FILELEN", "3");

                /*background mode; doesn't fit <8.0 */
            /*Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);*/

                /* start transmission */
            /*if(screenRecorder.isRunning()) {
                screenRecorder.stopRecord();
            } else {
                Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
            }*/

                //screenRecorder.stopRecord(); // TODO: чисто для теста
                screenRecorder.startRecord();
                dt = new DataTransfer(screenRecorder);
                dt.execute(addr);
            } else {
                Log.i("FILELEN", tvc.tvStatus);
                textStatus.setText(tvc.tvStatus);
            }
        } else {
            dt.working = false;
            buttonStartTransmission.setText("Начать трансляцию на " + addr);
            working = false;
        }
    }

    public void goBack(View view) { // TODO: check the mode and load the proper screen
        Intent intent = new Intent(StartTransmissionActivity.this, EnterIP_Activity.class);
        startActivity(intent);
    }

    public void goToMainMenu(View view) {
        Intent intent = new Intent(StartTransmissionActivity.this, MainMenu.class);
        startActivity(intent);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            ScreenRecorder.RecordBinder binder = (ScreenRecorder.RecordBinder) service;
            screenRecorder = binder.getScreenRecorder();
            screenRecorder.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            screenRecorder.setMediaProject(mediaProjection);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };
}

