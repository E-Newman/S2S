package com.example.pvv22.test3;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import android.widget.VideoView;


public class PhoneView extends Activity {
    private ServerSocket serverInit, serverImg;
    private Socket forInit, forImg;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private VideoView vv;
    private int curFile = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vv = (VideoView) findViewById(R.id.phoneImage);
        vv.setOnCompletionListener(onEnd);
        setContentView(R.layout.activity_phone_view);
        Server s = new Server();
        s.execute();
    }
    MediaPlayer.OnCompletionListener onEnd = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            vv.setVideoPath("record" + curFile + ".mp4");
            vv.start();
            File f = new File("record" + (curFile - 1) + ".mp4");
            f.delete();
            curFile++;
        }
    };
    private void goBack() {
        Intent i = new Intent(PhoneView.this, MainActivity.class);
        startActivity(i);
    }

    public void onBtnClick(View view) {
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        goBack();
    }

    /*class SendInterruptTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            byte[] mesg = "Interrupt".getBytes();
            try {
                DatagramSocket cli = new DatagramSocket();
                DatagramPacket dp = new DatagramPacket(mesg, mesg.length, ia, 11112);
                cli.send(dp);

            } catch (IOException e) {
                Log.e("emesg", "Ошибка при отправке пакета");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            goBack();
        }
    }*/

    @SuppressWarnings("unused")
    class Server extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            byte[] receiveData = new byte[8192];
            String mesg = null;
            try {
                serverInit = new ServerSocket(11110);
                serverImg = new ServerSocket(11112);
                forInit = serverInit.accept();
                dis = new DataInputStream(forInit.getInputStream());
                dos = new DataOutputStream(forInit.getOutputStream());
                int init = dis.readInt();
                int i = 1;
                if (init == 1){
                    dos.writeInt(10);
                    dis.close();
                    dos.close();
                    forInit.close();
                    forImg = serverImg.accept();
                    dis = new DataInputStream(forImg.getInputStream());
                }
                long fileSize;
                while (true) {
                    int res = 0;
                    File videoFile = new File("record" + i + ".mp4");
                    FileOutputStream fos = new FileOutputStream(videoFile);
                    fileSize = dis.readLong();
                    if (fileSize == -11){
                        dis.close();
                        forImg.close();
                        break;
                    }
                    while (fileSize > 0 && (res = dis.read(receiveData, 0, (int) Math.min(receiveData.length, fileSize))) != -1) {
                        fos.write(receiveData, 0, res);
                        fileSize -= res;
                    }
                    if (i == 2)
                        publishProgress();
                }
                if (fileSize == -11){
                    goBack();
                }
            } catch (IOException e) {
                Log.e("emesg", "Ошибка при получении пакета");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            vv.setVideoPath("record" + curFile + ".mp4");
            curFile++;
            vv.start();
        }
    }
}
