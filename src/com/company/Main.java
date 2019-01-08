package com.company;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

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
        byte[] receiveData = new byte[65536];
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
                ByteArrayInputStream imgStream = new ByteArrayInputStream(receivePacket.getData());
                BufferedImage img = null;
                try {
                    img = ImageIO.read(imgStream);
                    System.out.println("Image");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                File imgFile = new File("C:/ourimg.jpg");
                try {
                    ImageIO.write(img, "jpg", imgFile);
                    System.out.println("Image write");
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
