package com.petrsu.se.s2s;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class TVStatusChecker extends AsyncTask<String, Void, Integer> {
    public String tvStatus = "Undefined";

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
            tvStatus = "Не удалось получить IP-адрес";
            return -1;
        }

        try {
            sock = new DatagramSocket();
            sock.setSoTimeout(3000); // wait for 3 seconds
        } catch (Exception e) {
            Log.e("FATAL","Failed to create the socket");
            tvStatus = "Не удалось создать сокет";
            return -2;
        }

        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.getBytes().length,  ia, 11111);
        try {
            sock.send(dp);
        } catch (Exception e) {
            Log.e("FATAL","Failed to send datagram");
            tvStatus = "Не удалось отправить запрос на подключение";
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
                    if (!sock.isClosed()) {
                        sock.close();
                        tvStatus = "Отказано в соединении";
                        return -4;
                    }
                } catch (Exception e) {
                    Log.e("FATAL","Socket wasn't closed");
                    tvStatus = "Ошибка при закрытии сокета";
                    return -5;
                } // process?
            }
        } catch (Exception e) {
            Log.e("FATAL","Failed to receive datagram");
            tvStatus = "Не удалось получить ответ от телевизора";
            return -6;
        }

        /* image getting */

        try {
            if (!sock.isClosed()) sock.close();
        } catch (Exception e) {
            Log.e("FATAL","Socket wasn't closed");
            tvStatus = "Ошибка при закрытии сокета";
            return -7;
        }

        tvStatus = "Соединение установлено";
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
    }
}

class DataTransfer extends AsyncTask<String, Void, Integer> {
    public Boolean running = true;
    private Button findButton;

    public DataTransfer(Button findButton) {
        this.findButton = findButton;
    }

    @Override
    protected Integer doInBackground(String... args){
        InetAddress ia;
        DatagramSocket sock = null;
        Bitmap screenToSend;
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

        //while (running) { // send until we are stopped; test 1 time
            screenToSend = ScreenRecorder.takeScreenshotOfRootView(findButton); // take the ss

            /* convert & send the ss */
            ByteArrayOutputStream screenStream = new ByteArrayOutputStream();
            screenToSend.compress(Bitmap.CompressFormat.PNG, 50, screenStream);
            byte [] compScreen = screenStream.toByteArray();
            screenToSend.recycle();

            DatagramPacket dp = new DatagramPacket(compScreen, compScreen.length, ia, 11111);
            try {
                sock.send(dp);
            } catch (Exception e) {
                Log.e("FATAL", "Failed to send datagram");
                return -3;
            }
        //}

        try {
            if (!sock.isClosed()) sock.close();
        } catch (Exception e) {
            Log.e("FATAL", "Socket wasn't closed");
            return -4;
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result){

    }
}