package com.petrsu.se.s2s;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ScreenRecorder extends Service {
    private MediaProjection mediaProjection;
    //public DisplayMetrics metrics = new DisplayMetrics();
    private int dWidth;
    private int dHeight;
    private int dDensity;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay = null;
    private boolean running;

    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread serviceThread = new HandlerThread("service_thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        serviceThread.start();
        running = false;
        mediaRecorder = new MediaRecorder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }

    public boolean isRunning() {
        return running;
    }

    public void setConfig(int dWidth, int dHeight, int dDensity) {
        this.dWidth = dWidth;
        this.dHeight = dHeight;
        this.dDensity = dDensity;
    }

    public boolean startRecord() {
        if (mediaProjection == null || running) {
            return false;
        }

        initRecorder();
        if (virtualDisplay == null) createVirtualDisplay();
        mediaRecorder.start();
        running = true;
        return true;
    }

    public boolean stopRecord() {
        if (!running) {
            return false;
        }
        running = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        //virtualDisplay.release();
        mediaProjection.stop();

        return true;
    }

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", dWidth, dHeight, dDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        /*File outFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "record.mp4");

        Log.d("FILE", outFile.getAbsolutePath());
        if (!outFile.exists()) {
            try {
                if (outFile.createNewFile()) {
                    Log.d("RECORD", "Created in SR");
                } else Log.e("RECORD", "File create issues in SR");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        mediaRecorder.setOutputFile("/data/user/0/com.petrsu.se.s2s/record.mp4");
        mediaRecorder.setVideoSize(dWidth, dHeight);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(30);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public String getSaveDirectory() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.petrsu.se.s2s";

                File file = new File(rootDir);
                if (!file.exists()) {
                    if(!file.mkdirs()) {
                       Log.e("FILE FAIL", "Could not create " + rootDir);
                       return null;
                    }
                }

                Toast.makeText(getApplicationContext(), rootDir, Toast.LENGTH_SHORT).show();

                return rootDir;
            } else {
                return null;
            }
        }
        catch (Exception e) {
            Log.e("FILE FAIL", "Failed to open target file");
            return null;
        }
    }*/

    public class RecordBinder extends Binder {
        public ScreenRecorder getScreenRecorder() {
            return ScreenRecorder.this;
        }
    }


    /*public static Bitmap takeScreenshot(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap takeScreenshotOfRootView(View v) {
        return takeScreenshot(v.getRootView());
    }*/
}