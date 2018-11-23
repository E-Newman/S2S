package com.petrsu.se.s2s;

import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class TVStatusChecker extends AsyncTask<String, Void, Integer> {
    public int tvStatus = -10;

    @Override
    protected Integer doInBackground(String... args){
        InetAddress ia;
        DatagramSocket sock = null;
        String message = "Connect";
        String addria = "";

        for (String part : args) {
            addria += part;
        }

        try {
            ia = InetAddress.getByName(addria);
        } catch (Exception e) {
            Log.e("FATAL","Failed to resolve IP");
            tvStatus = -1;
            return -1;
        }

        try {
            sock = new DatagramSocket();
            sock.setSoTimeout(3000); // wait for 3 seconds
        } catch (Exception e) {
            Log.e("FATAL","Failed to create the socket");
            tvStatus = -2;
            return -2;
        }

        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.getBytes().length,  ia, 11111);
        try {
            sock.send(dp);
        } catch (Exception e) {
            Log.e("FATAL","Failed to send datagram");
            tvStatus = -3;
            return -3;
        }

        byte [] ans = new byte[1024];
        DatagramPacket ap = new DatagramPacket(ans, ans.length);
        try {
            Log.i("ZHDEM", "1");
            sock.receive(ap);
            Log.i("PRISHLO", "1");
            if (ap.getData().toString() == "No") {
                Log.i("NOPE", "Nope");
                try {
                    if (!sock.isClosed()) sock.close();
                } catch (Exception e) {
                    Log.e("FATAL","Socket wasn't closed");
                    tvStatus = 4;
                    return -4;
                } // process?
            }
        } catch (Exception e) {
            Log.e("FATAL","Failed to receive datagram");
            tvStatus = -5;
            return -5;
        }

        try {
            if (!sock.isClosed()) sock.close();
        } catch (Exception e) {
            Log.e("FATAL","Socket wasn't closed");
            tvStatus = -6;
            return -6;
        }

        tvStatus = 0;
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        tvStatus = result;
    }
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

        byte [] ans = new byte[1024];
        DatagramPacket ap = new DatagramPacket(ans, ans.length);
        try {
            Log.i("ZHDEM", "1");
            sock.receive(ap);
            Log.i("PRISHLO", "1");
            if (ap.getData().toString() == "No") {
                try {
                    Log.i("NOPE", "Nope");
                    if (!sock.isClosed()) sock.close();
                } catch (Exception e) {
                    Log.e("FATAL","Socket wasn't closed");
                    return -4;
                } // process?
            }
        } catch (Exception e) {
            Log.e("FATAL","Failed to recieve datagram");
            return -3;
        }

        try {
            if (!sock.isClosed()) sock.close();
        } catch (Exception e) {
            Log.e("FATAL","Socket wasn't closed");
            return -4;
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result){

    }
}