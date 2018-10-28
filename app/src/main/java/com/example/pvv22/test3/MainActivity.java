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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.net.InetAddress;
import android.os.AsyncTask;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {

    private TextView tw1, tw2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tw1 = (TextView) findViewById(R.id.textView);
        tw2 = (TextView) findViewById(R.id.textView2);
        IpTask task1 = new IpTask();
        task1.execute();
    }

    public void netButtonClick(View view) throws Exception {
        NetTask task2 = new NetTask();
        task2.execute();
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
                server = new DatagramSocket(1228);
                server.receive(recievePacket);
                mesg = new String(recievePacket.getData());
                sendBuf = mesg.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, server.getInetAddress(), server.getPort());
                server.send(sendPacket);
            }
            catch (Exception e){}
            return mesg;
        }
        @Override
        protected void onPostExecute(String result){
            tw1.setText(result);
        }
    }
    class IpTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params){
            String ip = null;
            try {
                InetAddress addr = InetAddress.getLocalHost();
                ip = addr.getHostAddress();
            }
            catch (Exception e){}
            return ip;
        }
        @Override
        protected void onPostExecute(String arg){
            tw2.setText(arg);
        }
    }
}
