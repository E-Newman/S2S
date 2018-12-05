package com.example.pvv22.test3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class PhoneView extends Activity {
    private DatagramSocket server;
    private boolean interrupt = false;
    private ImageView iv;
    private Server serv;
    private InetAddress ia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iv = (ImageView) findViewById(R.id.phoneImage);
        setContentView(R.layout.activity_phone_view);
        ListenInterruptTask it = new ListenInterruptTask();
        it.execute();
        Server s = new Server();
        s.execute();
    }
    private void goBack(){
        Intent i = new Intent(PhoneView.this, MainActivity.class);
        startActivity(i);
    }
    public void onBtnClick(View view){
        SendInterruptTask sit = new SendInterruptTask();
        sit.execute();
    }
    class SendInterruptTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params){
            byte[] mesg = "Interrupt".getBytes();
            try{
                DatagramSocket cli = new DatagramSocket();
                DatagramPacket dp = new DatagramPacket(mesg, mesg.length, ia, 11112);
                cli.send(dp);

            }
            catch (IOException e){
                Log.e("emesg","Ошибка при отправке пакета");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void res){
            goBack();
        }
    }
    class ListenInterruptTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params){
            try {
                String t = "";
                DatagramSocket interruptTracker = new DatagramSocket(11113);
                DatagramPacket dp;
                while (t != "Interrupt"){
                    byte[] mesg = new byte[1024];
                    dp = new DatagramPacket(mesg, mesg.length);
                    interruptTracker.receive(dp);
                    ia = dp.getAddress();
                    t = dp.getData().toString();
                }
                interrupt = true;
                server.close();
                interruptTracker.close();
                goBack();
            }
            catch (IOException e){
                Log.e("emesg", "Проблема при получении пакета");
            }
            return null;
        }
    }

    class Server extends AsyncTask<Void, Bitmap, Void> {
        @Override
        protected Void doInBackground(Void... params){
            byte[] readBuf = new byte[32000];
            String mesg = null;
            try {
                server = new DatagramSocket(11111);
                Log.i("mesg", "Socket Created");
                while (!interrupt){
                    DatagramPacket recievePacket = new DatagramPacket(readBuf, readBuf.length);
                    server.receive(recievePacket);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(readBuf , 0, readBuf.length);
                    publishProgress(bitmap);
                    Arrays.fill(readBuf, (byte)0);
                }
            }
            catch (IOException e){
                Log.e("emesg", "Ошибка при получении пакета");
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Bitmap... values){
            iv.setImageBitmap(values[0]);
        }
    }
}
