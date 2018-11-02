package com.petrsu.se.s2s;

import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result){

    }
}