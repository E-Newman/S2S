package com.company;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

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
        //byte[] receiveData = new byte[8192];
        //byte[] sendData = new byte[1024];
        int initCode = 0;
        int i = 1;
        DataInputStream dis1 = null;
        DataOutputStream dos1 = null;
        try {
            Socket servS = serverSocket.accept();
            dis1 = new DataInputStream(servS.getInputStream());
            dos1 = new DataOutputStream(servS.getOutputStream());
            System.out.println("here");
            initCode = dis1.readInt();
            System.out.println("here1");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("RECEIVED: " + initCode);
        if (initCode == 1) {
            //String response = "Yes";
            //sendData = response.getBytes();
            try {
                dos1.writeInt(10);
                dis1.close();
                dos1.close();
                Socket sockIs = imgSocket.accept();
                DataInputStream dis =  new DataInputStream(sockIs.getInputStream());
                while(true) {
                    //DataOutputStream dos = null;
                    System.out.println("Ready for img");
                    File videoFile = null;
                    try {
                        videoFile = new File("C:/Users/EP/record" + i +".mp4");
                        i++;
                        System.out.println(i + " " + LocalDateTime.now().toString());
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
                        System.out.println("Accepted");
                        long filesize = dis.readLong();
                        if (filesize != -11) {
                            byte[] receiveData = new byte[(int)filesize];
                            System.out.println("Размер файла " + filesize);
                            while (filesize > 0 && (res = dis.read(receiveData, 0, (int) Math.min(receiveData.length, filesize))) != -1) {
                                fos.write(receiveData, 0, res);
                                //System.out.println(res);
                                filesize -= res;
                                //System.out.println(filesize);
                            }
                        } else {
                            System.out.println("Session finished");
                            System.exit(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
