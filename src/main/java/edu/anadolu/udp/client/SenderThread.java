package edu.anadolu.udp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

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
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            System.out.println("Please enter your username: ");
            this.username = inFromUser.readLine();

            while (true) {
                if (stopped)
                    return;

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

                byte[] sendData = new byte[1024];

                sendData = clientMessage.getBytes(StandardCharsets.UTF_8);

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, hostname, port);

                socket.send(sendPacket);

                Thread.yield();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}