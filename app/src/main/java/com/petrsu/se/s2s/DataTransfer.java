package com.petrsu.se.s2s;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.spec.ECField;
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
            sock = new DatagramSocket(11109);
        } catch (Exception e) {
            Log.e("FATAL","Failed to create the socket");
            tvStatus = "Не удалось создать сокет";
            return -2;
        }

        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.getBytes().length,  ia, 11110);
        try {
            sock.send(dp);
            sock.setSoTimeout(3000); // wait for 3 seconds
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
            if (ap.getData().toString().contains("No")) {
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
                }
            } else if(ap.getData().toString().contains("Yes")) {
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
    byte[] stopPack;
    public static boolean timerRunning = false;
    private ScreenRecorder screenRecorder;
    private InetAddress ia;

    public DataTransfer(ScreenRecorder screenRecorder) {
        this.screenRecorder = screenRecorder;
    }

    @Override
    protected Integer doInBackground(String... args){
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
            sock = new DatagramSocket(11112);
        } catch (Exception e) {
            Log.e("FATAL","Failed to create the socket");
            return -2;
        }

        try {
            lsock = new DatagramSocket(11113);
        } catch (Exception e) {
            Log.e("FATAL", "Failed to create the listening socket");
            return -2;
        }

        Timer sendTimer = new Timer();
        TimerTask sendTask = new sendTask(sock, lsock, ia, sendTimer);

        stopPack = new byte[9];

        try {
            Thread.sleep(5000); // freeze to write some video
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendTimer.schedule(sendTask, 0, 1000); // TODO: find optimal vid length

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
        Timer sendTimer;
        int sentFifth = 0;

        File sendFile = new File("/data/user/0/com.petrsu.se.s2s/record.mp4");

        public sendTask(DatagramSocket sock, DatagramSocket lsock, InetAddress ia, Timer sendTimer) {
            this.sock = sock;
            this.lsock = lsock;
            this.ia = ia;
            this.sendTimer = sendTimer;
            timerRunning = true;
        }

        @Override
        public void run() {
            long len = sendFile.length();
            Log.i("FILELEN", Long.toString(len));
            if (len >= 650000) {
                screenRecorder.stopRecord();
                FileInputStream fis = null;
                try
                {
                    fis = new FileInputStream(sendFile);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("RECORDED", "Yeee");
                try {
                    byte[] videoBytes = new byte[65000];
                    if (sendFile.exists()) {
                        int piecesNumber = (int)(sendFile.length() / 65000) + 1;
                        Log.i("FILELEN", Integer.toString(piecesNumber));
                        //byte[] byteNum = ByteBuffer.allocate(4).putInt(piecesNumber).array();
                        ByteArrayOutputStream numAos = new ByteArrayOutputStream();
                        DataOutputStream numDos = new DataOutputStream(numAos);
                        numDos.writeInt(piecesNumber);
                        numDos.close();
                        byte[] byteNum = numAos.toByteArray();
                        sock.send(new DatagramPacket(byteNum, byteNum.length, ia, 11111));
                        for (int i = 0; i < piecesNumber; i++) {
                            fis.read(videoBytes);
                            sock.send(new DatagramPacket(videoBytes, videoBytes.length, ia, 11111));
                        }
                        try {
                            fis.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("FILELEN", "Sent " + videoBytes.length + " bytes");
                    } else Log.e("FILE", "Not found");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sentFifth != 5) {
                    screenRecorder.startRecord();
                    sentFifth++;
                } else {
                    sendTimer.cancel();
                    Log.i("FILELEN", "finish");
                }

            }
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
        }
    }
}