package edu.anadolu.udp.server;

import edu.anadolu.tcp.server.UserThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class UDPServer {
    // Server structures
    private DatagramSocket udpServerSocket = null;
    public HashSet<Integer> portSet = new HashSet<Integer>();
    public HashMap<Integer, String> usernames = new HashMap<>();
    private Integer administrator = null;
    private String password = null;
    private int port;
    private LinkedList<Integer> players = new LinkedList<>();
    // Game structures
    private boolean isGameOn = false;
    private Set<String> vocabulary = null;
    private String lastWord;
    private int interval;
    private Timer timer;
    private Locale locale = null;

    public UDPServer(String password, int port) {
        this.password = password;
        this.port = port;
    }

    public void execute() throws IOException { // executing the server
        this.udpServerSocket = new DatagramSocket(port);

        System.out.println("Server started...\n");

        while (true) {
            // Create byte buffers to hold the messages to send and receive
            byte[] receiveData = new byte[1024];

            // Create an empty DatagramPacket packet
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // Block until there is a packet to receive, then receive it  (into our empty packet)
            udpServerSocket.receive(receivePacket);

            // Extract the message from the packet and make it into a string, then trim off any end characters
            String clientMessage = (new String(receivePacket.getData())).trim();
            int clientport = receivePacket.getPort();
            InetAddress clientIP = receivePacket.getAddress();

            if (clientMessage.contains("My username is, ")) {
                String username = clientMessage.split(", ")[1];
                usernames.put(clientport, username);
            }

            if (clientMessage.contains("login")) {
                String[] args = clientMessage.split(" ");
                if (args.length == 1 || args.length > 2) {
                    sendMessage("Please re-type your password", clientport);
                    continue;
                } else {
                    loginAsAdministrator(args[1], clientport);
                    continue;
                }
            }
            if (clientMessage.equals("start the game TR")) {
                startGame(clientport, new Locale("tr", "TR"));
                continue;
            }
            if (clientMessage.equals("start the game EN")) {
                startGame(clientport, new Locale("en", "US"));
                continue;
            }

            System.out.println("[" + usernames.get(clientport) + "]: " + clientMessage);

            broadcast(clientMessage, clientport, clientIP);
        }
    }

    void broadcast(String message, Integer clientPort, InetAddress clientIP) throws IOException { // to broadcast users with excluding one
        if (isGameOn) {
            if (players.peek() == port) guess(message.toLowerCase(locale), clientPort);
            else sendMessage("It's not your turn, please wait your turn!", clientPort);
            return;
        }

        byte[] sendData = new byte[1024];
        sendData = message.getBytes();
        for (Integer port : portSet) {
            if (port != clientPort) {
                // Create a DatagramPacket to send, using the buffer, the clients IP address, and the clients port
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, port);
                // Send the echoed message
                udpServerSocket.send(sendPacket);
            }
        }
    }

    void sendMessage(String message, Integer clientPort) throws IOException {
        byte[] sendData = new byte[1024];
        sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), clientPort);
        udpServerSocket.send(sendPacket);
    }

    private void broadcastAll(String message) throws IOException { // to broadcast users without excluding
        byte[] sendData = new byte[1024];
        sendData = message.getBytes();
        for (Integer port : portSet) {
            // Create a DatagramPacket to send, using the buffer, the clients IP address, and the clients port
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), port);
            // Send the echoed message
            udpServerSocket.send(sendPacket);
        }
    }

    boolean isGameOn() { // to check is game on or off
        return isGameOn;
    }

    void loginAsAdministrator(String password, int clientPort) throws IOException { // to log in as administrator
        if (!password.equals(this.password)) {
            sendMessage("Password is wrong!", clientPort);
            return;
        }
        if (administrator != null) {
            sendMessage("There can be only one administrator at a time!", clientPort);
            return;
        }
        administrator = clientPort;
        sendMessage("Welcome back, sir.", clientPort);
        broadcastAll("Administrator " + usernames.get(clientPort) + " has logged in!");
    }

    void startGame(int administrator, Locale locale) throws IOException {
        if (this.administrator == null) {
            sendMessage("Seems like administrator has not logged in yet", port);
            return;
        }
        if (this.administrator != administrator) {
            sendMessage("You are not administrator!", port);
            return;
        }
        if (isGameOn) {
            sendMessage("There is a already game running on!", port);
            return;
        }
        this.locale = locale;
        vocabulary = new HashSet<>();
        vocabulary = new HashSet<>();
        lastWord = null;
        timer = new Timer();
        interval = 30;
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                try {
                    setInterval();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 1000);
        isGameOn = true;
        broadcastAll("The game has started!");
        System.out.println("The game has started!");
        players.addAll(portSet);
        sendMessage("It's your turn, just type a meaningful word between 30 seconds.", players.peek());
    }

    public boolean guess(String word, Integer clientPort) throws IOException {
        if (word.isBlank() || word.isEmpty() || word.contains("\\p{Punct}") || word.contains("\\s+")) {
            sendMessage("Wrong usage!", clientPort);
            return false;
        }
        if (vocabulary.contains(word)) {
            sendMessage("This word has been used already", clientPort);
            return false;
        }
        if (lastWord != null && !word.substring(0, 2).equals(lastWord.substring(lastWord.length() - 2))) {
            sendMessage("New word is not starting with the last 2 letters of the last one", clientPort);
            return false;
        }
        System.out.println("[" + usernames.get(clientPort) + "]: " + word);
        vocabulary.add(word);
        lastWord = word;
        changeTurns(clientPort);
        return true;
    }

    private void changeTurns(Integer clientPort) throws IOException {
        players.poll();
        players.add(clientPort);
        sendMessage("It is your turn, last word is: " + lastWord, players.peek());
        interval = 30;
    }

    private int setInterval() throws IOException {
        if (interval == 15) sendMessage("15 seconds left", players.peek());
        if (interval < 10) sendMessage(String.format("%s second(s) left!", interval), players.peek());
        if (interval == 1) {
            players.poll();
            broadcastAll("Timed out!\n" + usernames.get(players.peek()) + " has won!");
            isGameOn = false;
            timer.cancel();
        }
        return --interval;
    }
}