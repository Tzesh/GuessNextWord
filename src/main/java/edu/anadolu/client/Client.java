package edu.anadolu.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private final String hostname;
    private final int port;
    private String userName;

    public Client(String hostname, int port) { // default constructor
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() { // we'll need 2 threads consequently working simultaneously
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to the server");

            new ReadThread(socket, this).start(); // one is read thread
            new WriteThread(socket, this).start(); // the other is write thread

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }

    }

    String getUserName() {
        return this.userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }
}