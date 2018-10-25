package com.example.pvv22.test3;

import java.net.*;
import java.util.*;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class netcode {
    private AsyncTask<Void, Void, Void> async;
    public String s;
    private boolean test = true;
    String mesg;
    public netcode(){}
    public void connect(){
            async = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    byte[] readBuf = new byte[1024];
                    byte[] sendBuf = new byte[1024];

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
                    return null;
                }
            };
            async.execute();
    }
    public String getAddr(){
        async = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    InetAddress addr = InetAddress.getLocalHost();
                    s = addr.getHostAddress();

                    /*int i = 0;
                    Enumeration en = NetworkInterface.getNetworkInterfaces();
                    while(en.hasMoreElements()){
                        NetworkInterface ni=(NetworkInterface) en.nextElement();
                        Enumeration ee = ni.getInetAddresses();
                        while(ee.hasMoreElements()) {
                            InetAddress ia= (InetAddress) ee.nextElement();
                            Log.i("1", ia.getHostAddress());
                            if (i == 2) s = ia.getHostAddress();
                            i++;
                        }
                    } */
                }
                catch (Exception e) {
                    System.out.print(e);
                }
                test = false;
                return null;
            }
        };
        async.execute();
        while (test){}
        return s;
    }
}

