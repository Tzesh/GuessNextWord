package edu.anadolu.udp.client;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {
    private final InetAddress hostname;
    private final int port;
    private String userName;

    public UDPClient(String hostname, int port) throws UnknownHostException { // default constructor
        this.hostname = InetAddress.getByName(hostname);
        this.port = port;
    }

    public void execute() throws SocketException, UnknownHostException { // we'll need 2 threads consequently working simultaneously

        System.out.println("Connected to the server");

        SenderThread sender = new SenderThread(hostname, port);
        sender.start();
        ReceiverThread receiver = new ReceiverThread(sender.getSocket());
        receiver.start();

    }

    String getUserName() {
        return this.userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }
}