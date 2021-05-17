package edu.anadolu.tcp.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class WriteThread extends Thread {
    private final Socket socket;
    private final Client client;
    private PrintWriter writer;

    public WriteThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() { // all the functionality of write thread
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.print("\nEnter your nickname: ");
        String userName = scanner.nextLine();
        client.setUserName(userName);
        writer.println(userName);

        String text = null;

        do {
            System.out.print("[" + userName + "]: ");
            text = scanner.nextLine();
            if (text.isEmpty()) continue;
            writer.println(text);

        } while (!text.equals("quit"));

        try {
            socket.close();
        } catch (IOException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}