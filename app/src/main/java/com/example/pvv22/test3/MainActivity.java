/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.pvv22.test3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {

    private String ip;
    private boolean check = false;
    private DatagramSocket server;
    private InetAddress ia;
    private int port;
    private TextView ipTextView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipTextView = (TextView) findViewById(R.id.ipTextView);
        IpTask task1 = new IpTask();
        task1.execute();
    }
    public void toManualActivity(View view) {
        startActivity(new Intent(MainActivity.this, ManualActivity.class));
    }
    public void toAuthorsActivity(View view) {
        startActivity(new Intent(MainActivity.this, AuthorsActivity.class));
    }
    class NetTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params){
            byte[] readBuf = new byte[1024];
            String mesg = null;
            DatagramPacket recievePacket = new DatagramPacket(readBuf, readBuf.length);
            try {
                server = new DatagramSocket(11110);
                Log.i("mesg", "Socket Created");
                server.receive(recievePacket);
                mesg = new String(recievePacket.getData());
                ia = recievePacket.getAddress();
                port = recievePacket.getPort();
            }
            catch (Exception e){
                Log.i("mesg", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa " + e);
            }
            return mesg;
        }
        @Override
        protected void onPostExecute(String result){
            if (result == "Сonnect"){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Принять соединение?")
                        .setTitle("")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                    Intent i = new Intent(MainActivity.this, PhoneView.class);
                                    byte[] sendBuf = "Да".getBytes();
                                    DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, ia, port);
                                    server.send(sendPacket);
                                    server.close();
                                    startActivity(i);
                                }
                                catch (IOException e){
                                    Log.e("emesg","Ошибка при отправлении");
                                }
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                    byte[] sendBuf = "Нет".getBytes();
                                    DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, ia, port);
                                    server.send(sendPacket);
                                    server.close();
                                }
                                catch (IOException e){
                                    Log.e("emesg","Ошибка при отправлении");
                                }

                            }
                        });
                builder.create();
            }
        }
    }
    class IpTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params){
                try{
                    final DatagramSocket socket = new DatagramSocket();
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    return socket.getLocalAddress().getHostAddress();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return null;
        }
        @Override
        protected void onPostExecute(String arg){
            ImageView iv = (ImageView) findViewById(R.id.qrImage);
            try {
                BitMatrix matrix = new MultiFormatWriter().encode(arg, BarcodeFormat.QR_CODE, 125, 125);
                Bitmap bmp = Bitmap.createBitmap(125, 125, Bitmap.Config.RGB_565);
                for (int x = 0; x < 125; x++){
                    for (int y = 0; y < 125; y++){
                        bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
                    }
                }
                iv.setImageBitmap(bmp);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            ipTextView.setText(arg);

        }
    }
}
