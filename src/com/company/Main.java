package com.company;

import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) {
        Boolean gotConnect = false;
        ServerSocket imgSocket = null, serverSocket = null;
        try {
            serverSocket = new ServerSocket(11110);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            imgSocket = new ServerSocket(11112);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        byte[] receiveData = new byte[1000000];
        byte[] sendData = new byte[1024];
        InputStream ssIs = null;
        OutputStream ssOs = null;
        while(true) {
            if (!gotConnect) {
                try {
                    Socket servS = serverSocket.accept();
                    ssIs = servS.getInputStream();
                    ssOs = servS.getOutputStream();
                    ssIs.read(receiveData);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                String sentence = new String(receiveData);
                System.out.println("RECEIVED: " + sentence);
                if (sentence.contains("Connect")) {
                    String response = "Yes";
                    sendData = response.getBytes();
                    try {
                        ssOs.write(sendData);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    gotConnect = true;
                }
            } else {
                System.out.println("Ready for img");
                File videoFile = null;
                try {
                    videoFile = new File("C:/Users/EP/record.mp4");
                    if (videoFile.exists()) {
                        videoFile.delete();
                        videoFile.createNewFile();
                    }
                    //System.out.println("Video");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(videoFile);
                    //System.out.println("Video write");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                //byte[] videoBytes = null;
               try {
                   int res = 0;
                   InputStream sockIs = imgSocket.accept().getInputStream();
                   System.out.println("Accepted");
                   while ((res = sockIs.read(receiveData)) != -1) {
                       fos.write(receiveData, 0, res);
                       System.out.println(res);
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
