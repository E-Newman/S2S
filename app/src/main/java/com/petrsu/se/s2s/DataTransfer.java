package com.petrsu.se.s2s;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

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

        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.getBytes().length,  ia, 11110);
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
            } else if(ap.getData().toString() != "Yes") {
                Log.i("NOPE", "We got smth wrong");
                try {
                    if (!sock.isClosed()) {
                        sock.close();
                        tvStatus = "Получено неверное сообщение";
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
    private Button findButton;
    byte[] stopPack;
    public static boolean timerRunning = false;

    public DataTransfer(Button findButton) {
        this.findButton = findButton;
    }

    @Override
    protected Integer doInBackground(String... args){
        InetAddress ia;
        DatagramSocket sock = null, lsock = null;
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

        try {
            lsock = new DatagramSocket(11112);
        } catch (Exception e) {
            Log.e("FATAL", "Failed to create the listening socket");
            return -2;
        }

        Timer sendTimer = new Timer();
        TimerTask sendTask = new sendTask(sock, lsock, ia, sendTimer);

        stopPack = new byte[9];

        sendTimer.schedule(sendTask, 0, 1000); // send once a second

        Log.i("START", "Data transfer start");
        while (timerRunning);
        Log.i("STOP", "Data transfer end");

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

    private class sendTask extends TimerTask {
        DatagramSocket sock, lsock;
        InetAddress ia;
        Bitmap screenToSend;
        Timer sendTimer;

        public sendTask(DatagramSocket sock, DatagramSocket lsock, InetAddress ia, Timer sendTimer) {
            this.sock = sock;
            this.lsock = lsock;
            this.ia = ia;
            this.sendTimer = sendTimer;
            timerRunning = true;
        }

        @Override
        public void run() {
            /*try {
                lsock.receive(new DatagramPacket(stopPack, stopPack.length));
                if (stopPack.toString() == "Interrupt") {
                    sendTimer.cancel();
                }
            }
            catch (Exception e) {
                Log.e("FATAL", "Failed to receive a stop datagram");
                sendTimer.cancel();
                return;
            }*/

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
                sendTimer.cancel();
                return;
            }
        }
    }
}