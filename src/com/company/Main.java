package com.company;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Main {

    public static void main(String[] args) {
        Boolean gotConnect = false;
        DatagramSocket serverSocket = null, imgSocket = null;
        try {
            serverSocket = new DatagramSocket(11110);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            imgSocket = new DatagramSocket(11111);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        byte[] receiveData = new byte[65000];
        byte[] sendData = new byte[1024];
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            if (!gotConnect) {
                try {
                    serverSocket.receive(receivePacket);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                String sentence = new String(receivePacket.getData());
                System.out.println("RECEIVED: " + sentence);
                if (sentence.contains("Connect")) {
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    String response = "Yes";
                    sendData = response.getBytes();
                    DatagramPacket sendPacket =
                            new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    try {
                        serverSocket.send(sendPacket);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    gotConnect = true;
                }
            } else {
                System.out.println("Ready for img");
                try {
                    imgSocket.receive(receivePacket);
                    System.out.println(receivePacket.getData().toString());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                //ByteBuffer numBuf = ByteBuffer.wrap(receivePacket.getData());
                int piecesNumber = -1;
                ByteArrayInputStream numIs = new ByteArrayInputStream(receivePacket.getData());
                DataInputStream numDs = new DataInputStream(numIs);
                try {
                    piecesNumber = numDs.readInt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                File videoFile = null;
                try {
                    videoFile = new File("C:/Users/EP/record.mp4");
                    System.out.println("Video");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(videoFile);
                    System.out.println("Video write");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                System.out.println(piecesNumber);
                for (int i = 0; i < piecesNumber; i++) {
                    try {
                        imgSocket.receive(receivePacket);
                        //byte[] videoBytes = new byte[65000];
                        byte[] videoBytes = receivePacket.getData();
                        long checksum = 0;
                        for (int j = 0; j < videoBytes.length; j++) {
                            checksum += videoBytes[j];
                        }
                        System.out.println(Long.toString(checksum));
                        fos.write(receivePacket.getData());
                    }
                        catch (Exception e) {
                    System.out.println(e.getMessage());
                    }
                }
            }
        }
    }
}
