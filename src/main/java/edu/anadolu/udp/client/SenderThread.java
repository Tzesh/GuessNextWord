package edu.anadolu.udp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class SenderThread extends Thread {
    private final DatagramSocket socket;
    private final int port;
    private final InetAddress hostname;
    private boolean stopped = false;
    private String username = null;
    private boolean isUsernameSent = false;

    public SenderThread(InetAddress hostname, int port) throws SocketException, UnknownHostException { // default constructor
        this.hostname = hostname;
        this.port = port;

        this.socket = new DatagramSocket();
        this.socket.connect(hostname, port);
    }

    public DatagramSocket getSocket() {
        return this.socket;
    }

    public void halt() {
        this.stopped = true;
    }

    public void run() { // functionality of read thread
        try {
            //send blank message
            byte[] data = new byte[1024];
            data = "".getBytes();
            DatagramPacket blankPacket = new DatagramPacket(data, data.length, hostname, port);
            socket.send(blankPacket);

            // Create input stream
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please enter your username: ");
            this.username = inFromUser.readLine();

            while (true) {
                if (stopped)
                    return;

                // Message to send
                String clientMessage;
                if (isUsernameSent) {
                    clientMessage = inFromUser.readLine();
                }
                else {
                    clientMessage = "My username is, " + username;
                    isUsernameSent = true;
                }

                if (clientMessage.equals("quit"))
                    break;

                // Create byte buffer to hold the message to send
                byte[] sendData = new byte[1024];

                // Put this message into our empty buffer/array of bytes
                sendData = clientMessage.getBytes();

                // Create a DatagramPacket with the data, IP address and port number
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, hostname, port);

                // Send the UDP packet to server
                socket.send(sendPacket);

                Thread.yield();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}