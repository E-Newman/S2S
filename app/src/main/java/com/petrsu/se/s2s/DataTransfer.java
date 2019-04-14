package com.petrsu.se.s2s;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

class TVStatusChecker extends AsyncTask<String, Void, Integer> {
    public String tvStatus = "Undefined";

    @Override
    protected Integer doInBackground(String... args){
        Socket controlSock = null;
        String addria = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        for (String part : args) {
            addria += part;
        }

        try {
            controlSock = new Socket(addria,11110);
            dos = new DataOutputStream(controlSock.getOutputStream());
            dis = new DataInputStream(controlSock.getInputStream());
        } catch (Exception e) {
            Log.e("FATAL","Failed to create the socket");
            tvStatus = "Не удалось создать сокет";
            return -2;
        }

        try {
            dos.writeInt(1);
            controlSock.setSoTimeout(3000);
        } catch (Exception e) {
            Log.e("FATAL","Failed to send datagram");
            tvStatus = "Не удалось отправить запрос на подключение";
            try {
                if (!controlSock.isClosed()) {
                    controlSock.close();
                    tvStatus = "Отказано в соединении";
                    return -4;
                }
            } catch (Exception e1) {
                Log.e("FATAL","Socket wasn't closed");
                tvStatus = "Ошибка при закрытии сокета";
                return -5;
            }
        }

        try {

            int ansCode = dis.readInt();
            Log.i("FILELEN", Integer.toString(ansCode));
            if (ansCode == -10) {
                Log.i("NOPE", "Nope");
                try {
                    if (!controlSock.isClosed()) {
                        controlSock.close();
                        tvStatus = "Отказано в соединении";
                        return -4;
                    }
                } catch (Exception e) {
                    Log.e("FATAL","Socket wasn't closed");
                    tvStatus = "Ошибка при закрытии сокета";
                    return -5;
                }
            } else if(ansCode != 10) {
                Log.i("NOPE", "We got smth wrong");
                try {
                    if (!controlSock.isClosed()) {
                        controlSock.close();
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
            if (!controlSock.isClosed()) controlSock.close();
        } catch (Exception e) {
            Log.e("FATAL","Socket wasn't closed");
            tvStatus = "Ошибка при закрытии сокета";
            return -7;
        }

        tvStatus = "Соединение установлено";
        Log.i("FILELEN", "1" + tvStatus);
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
    public boolean working = true;

    public DataTransfer(ScreenRecorder screenRecorder) {
        this.screenRecorder = screenRecorder;
    }

    @Override
    protected Integer doInBackground(String... args){
        Socket sock = null, lsock = null;
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
            sock = new Socket(addria, 11112);
        } catch (Exception e) {
            Log.e("FATAL","Failed to create the socket");
            return -2;
        }

        /*try {
            lsock = new Socket(addria,11113);
        } catch (Exception e) {
            Log.e("FATAL", "Failed to create the listening socket");
        }*/

        Timer sendTimer = new Timer();
        TimerTask sendTask = new sendTask(sock, lsock, ia, sendTimer);

        stopPack = new byte[9];

        try {
            Thread.sleep(3000); // freeze to write some video
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendTimer.schedule(sendTask, 0, 3000); // TODO: find optimal vid length

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
        Socket sock, lsock;
        InetAddress ia;
        Timer sendTimer;
        int sentFifth = 0;
        byte[] lenByte = new byte[1], videoBytes;

        File sendFile = new File("/data/user/0/com.petrsu.se.s2s/record.mp4");

        public sendTask(Socket sock, Socket lsock, InetAddress ia, Timer sendTimer) {
            this.sock = sock;
            this.lsock = lsock;
            this.ia = ia;
            this.sendTimer = sendTimer;
            timerRunning = true;
        }

        @Override
        public void run() {
            long len = sendFile.length();
            DataOutputStream dos = null;
            try {
                dos = new DataOutputStream(sock.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (working) {
                if (len >= 600000) {
                    screenRecorder.stopRecord();
                    videoBytes = new byte[(int) len];
                    int n;
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(sendFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("RECORDED", "Yeee");
                    try {
                        dos.writeLong(len);
                        Log.i("FILELEN", "Длина файла " + len);
                        if (sendFile.exists()) {
                            n = fis.read(videoBytes);
                            //while (len > 0 && (n = fis.read(videoBytes)) != -1) {
                            //Log.i("FILELEN", "n: " + n);
                            //len -= n;
                            dos.write(videoBytes, 0, Math.min(videoBytes.length, (int) len));
                            //}
                        } else Log.e("FILE", "Not found");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (sentFifth != 5) { // debug
                        screenRecorder.startRecord();
                        sentFifth++;
                    } else {
                            /*try {
                                dos.writeInt(-1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
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
            } else {
                try {
                    dos.writeLong(-11);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendTimer.cancel();
                Log.i("FILELEN", "finish by user");
            }
        }
    }
}