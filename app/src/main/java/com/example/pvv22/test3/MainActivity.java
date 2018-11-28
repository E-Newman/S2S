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
    public void onYes(){

    }
    public void toAuthorsActivity(View view) {
        startActivity(new Intent(MainActivity.this, AuthorsActivity.class));
    }
    class NetTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params){
            byte[] readBuf = new byte[1024];
            byte[] sendBuf;
            String mesg = null;
            DatagramPacket recievePacket = new DatagramPacket(readBuf, readBuf.length);
            DatagramSocket server = null;
            try {
                server = new DatagramSocket(11111);
                Log.i("mesg", "Socket Created");
                server.receive(recievePacket);
                mesg = new String(recievePacket.getData());
                if (mesg == "connect"){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Принять соединение?")
                            .setTitle("")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                }
                sendBuf = mesg.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, recievePacket.getAddress(), recievePacket.getPort());
                server.send(sendPacket);
                Log.i("mesg", "Message sent " + mesg);
            }
            catch (Exception e){
                Log.i("mesg", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa " + e);
            }
            return mesg;
        }
        @Override
        protected void onPostExecute(String result){

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
