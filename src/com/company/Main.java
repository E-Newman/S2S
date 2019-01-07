package com.company;
import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) {
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(11110);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while(true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            String sentence = new String( receivePacket.getData());
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
            }
        }
    }
}
