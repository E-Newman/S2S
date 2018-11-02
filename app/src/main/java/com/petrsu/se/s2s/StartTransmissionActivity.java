package com.petrsu.se.s2s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.io.IOException;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class StartTransmissionActivity extends AppCompatActivity {
    public String addr;
    TextView tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_transmission);
        Intent intent = getIntent();
        addr = intent.getStringExtra("addr");
        tb = (TextView) findViewById(R.id.text1);
    }

    class DataTransfer extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... args){
            InetAddress ia;
            DatagramSocket sock = null;
            String message = "Hello from S2S";
            String addria = "";

            for (String part : args) {
                addria += part;
            }

            try {
                ia = InetAddress.getByName(addria);
            } catch (Exception e) {
                Log.e("FATAL","Failed to resolve IP");
                return -1;
            }

            try {
                sock = new DatagramSocket();
            } catch (Exception e) {
                Log.e("FATAL","Failed to create the socket");
                return -2;
            }

            DatagramPacket dp = new DatagramPacket(message.getBytes(), message.getBytes().length,  ia, 11111);
            try {
                sock.send(dp);
            } catch (Exception e) {
                Log.e("FATAL","Failed to send datagram");
                return -3;
            }

            try {
                if (!sock.isClosed()) sock.close();
            } catch (Exception e) {
                Log.e("FATAL","Socket wasn't closed");
                return -4;
            }

            /*
            try {
                sock = new DatagramSocket(3000);
                Log.i("Stage", "created socket");
            } catch (SocketException e) {
                return -2;
            }

            try {
                sock.send(new DatagramPacket(message.getBytes(), message.getBytes().length, ia, 3000));
                Log.i("Stage", "send");
            } catch (IOException e) {
                return -3;
            }

            try {
                byte[] buf = new byte[message.getBytes().length];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                sock.receive(dp);
                Log.i("Stage", "recieve");
            } catch (IOException e) {
                return -4;
            }*/
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result){
            tb.setText(result.toString());
        }
    }

    public void startTransmission(View view) {
        DataTransfer dt = new DataTransfer();
        dt.execute(addr);
        //int res = dt.connect();
        //final Button b = (Button) findViewById(R.id.buttonStartTransmission);
        //b.setText(Integer.toString(res));
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

