package edu.anadolu.tcp.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UserThread extends Thread {
    private final Socket socket;
    private final Server server;
    private PrintWriter writer;
    private String username;

    public UserThread(Socket socket, Server server) { // default constructor
        this.socket = socket;
        this.server = server;
    }

    public void run() { // thread's all functionality
        try {
            BufferedReader reader = new BufferedReader((new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);

            String userName = reader.readLine();
            server.addUserName(userName);
            this.username = userName;

            printUsers();

            String serverMessage = "New user connected: " + userName;
            server.addUserName(userName);
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                if (clientMessage.isBlank()) {
                    sendMessage("Please type something");
                    continue;
                }
                if (clientMessage.contains("login")) {
                    String[] args = clientMessage.split(" ");
                    if (args.length == 1 || args.length > 2) {
                        sendMessage("Please re-type your password");
                        continue;
                    } else {
                        server.loginAsAdministrator(args[1], this);
                        continue;
                    }
                }
                if (clientMessage.equals("start the game TR")) {
                    server.startGame(this, new Locale("tr", "TR"));
                    continue;
                }
                if (clientMessage.equals("start the game EN")) {
                    server.startGame(this, new Locale("en", "US"));
                    continue;
                }
                serverMessage = "[" + userName + "]: " + clientMessage;
                if (server.isGameOn()) server.broadcast(clientMessage, this);
                else server.broadcast(serverMessage, this);
            } while (!clientMessage.equals("quit"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quited.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void printUsers() { // to print users
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    String getUsername() { // to get username
        return username;
    }

    void sendMessage(String message) { // to send message from server using this thread
        writer.println(message);
    }

    public Socket getSocket() { // to get socket
        return socket;
    }

    @Override
    public boolean equals(Object obj) { // to make comparisions
        if (obj instanceof UserThread) {
            return socket == ((UserThread) obj).getSocket();
        }
        return false;
    }
}