package edu.anadolu.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UDPServer {
    // Server structures
    private DatagramSocket udpServerSocket;
    public HashSet<Integer> portSet = new HashSet<Integer>();
    public HashMap<Integer, String> usernames = new HashMap<>();
    private Integer administrator = null;
    private String password = null;
    private int serverPort;
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
        this.serverPort = port;
    }

    public void execute() throws IOException { // executing the server
        this.udpServerSocket = new DatagramSocket(serverPort);

        while (true) {
            byte[] receiveData = new byte[1024];

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            udpServerSocket.receive(receivePacket);

            String clientMessage = (new String(receivePacket.getData(), StandardCharsets.UTF_8)).trim();

            InetAddress clientIP = receivePacket.getAddress();

            int clientPort = receivePacket.getPort();
            portSet.add(clientPort);

            if (clientMessage.contains("My username is, ")) {
                String username = clientMessage.split(", ")[1];
                usernames.put(clientPort, username);
                continue;
            }

            if (clientMessage.contains("login")) {
                String[] args = clientMessage.split(" ");
                if (args.length == 1 || args.length > 2) {
                    sendMessage("Please re-type your password", clientIP, clientPort);
                    continue;
                } else {
                    loginAsAdministrator(args[1], clientPort, clientIP);
                    continue;
                }
            }

            if (clientMessage.equals("start the game TR")) {
                startGame(clientPort, new Locale("tr", "TR"), clientIP);
                continue;
            }

            if (clientMessage.equals("start the game EN")) {
                startGame(clientPort, new Locale("en", "US"), clientIP);
                continue;
            }

            clientMessage = "[" + usernames.get(clientPort) + "]: " + clientMessage;

            System.out.println(clientMessage);

            exclusiveBroadcast(clientMessage, clientIP, clientPort);
        }
    }

    void sendMessage(String message, InetAddress clientIP, Integer clientPort) throws IOException {
        byte[] sendData = new byte[1024];
        sendData = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, clientPort);
        udpServerSocket.send(sendPacket);
    }

    void broadcast(String message, InetAddress clientIP) throws IOException {
        for (Integer port : portSet) {
            sendMessage(message, clientIP, port);
        }
    }

    void exclusiveBroadcast(String message, InetAddress clientIP, Integer clientPort) throws IOException {
        if (isGameOn) {
            if (players.peek().equals(clientPort)) guess(message.replace("[" + usernames.get(clientPort) + "]: ", "").toLowerCase(locale), clientPort, clientIP);
            else sendMessage("It's not your turn, please wait your turn!", clientIP, clientPort);
            return;
        }

        for (Integer port : portSet) {
            if (!port.equals(clientPort)) {
                sendMessage(message, clientIP, port);
            }
        }
    }

    boolean isGameOn() { // to check is game on or off
        return isGameOn;
    }

    void loginAsAdministrator(String password, Integer clientPort, InetAddress clientIP) throws IOException { // to log in as administrator
        if (!password.equals(this.password)) {
            sendMessage("Password is wrong!", clientIP, clientPort);
            return;
        }
        if (administrator != null) {
            sendMessage("There can be only one administrator at a time!", clientIP, clientPort);
            return;
        }
        administrator = clientPort;
        sendMessage("Welcome back, sir.", clientIP, clientPort);
        broadcast("Administrator " + usernames.get(clientPort) + " has logged in!", clientIP);
    }

    void startGame(int administratorPort, Locale locale, InetAddress clientIP) throws IOException {
        if (this.administrator == null) {
            sendMessage("Seems like administrator has not logged in yet", clientIP, administratorPort);
            return;
        }
        if (this.administrator != administratorPort) {
            sendMessage("You are not administrator!", clientIP, administratorPort);
            return;
        }
        if (isGameOn) {
            sendMessage("There is a already game running on!", clientIP, administratorPort);
            return;
        }
        this.locale = locale;
        vocabulary = new HashSet<>();
        lastWord = null;
        timer = new Timer();
        interval = 30;
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                try {
                    setInterval(clientIP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 1000);
        isGameOn = true;
        broadcast("The game has started!", clientIP);
        System.out.println("The game has started!");
        players.addAll(portSet);
        sendMessage("It's your turn, just type a meaningful word between 30 seconds.", clientIP, players.peek());
    }

    public boolean guess(String word, Integer clientPort, InetAddress clientIP) throws IOException {
        if (word.isBlank() || word.isEmpty() || word.contains("\\p{Punct}") || word.contains("\\s+")) {
            sendMessage("Wrong usage!", clientIP, clientPort);
            return false;
        }
        if (vocabulary.contains(word)) {
            sendMessage("This word has been used already", clientIP, clientPort);
            return false;
        }
        if (lastWord != null && !word.substring(0, 2).equals(lastWord.substring(lastWord.length() - 2))) {
            sendMessage("New word is not starting with the last 2 letters of the last one", clientIP, clientPort);
            return false;
        }
        System.out.println("[" + usernames.get(clientPort) + "]: " + word);
        vocabulary.add(word);
        lastWord = word;
        changeTurns(clientPort, clientIP);
        return true;
    }

    private void changeTurns(Integer clientPort, InetAddress clientIP) throws IOException {
        players.poll();
        players.add(clientPort);
        sendMessage("It is your turn, last word is: " + lastWord, clientIP, players.peek());
        interval = 30;
    }

    private int setInterval(InetAddress clientIP) throws IOException {
        if (interval == 15) sendMessage("15 seconds left", clientIP, players.peek());
        if (interval < 10)
            sendMessage(String.format("%s second(s) left!", interval), clientIP, players.peek());
        if (interval == 1) {
            players.poll();
            broadcast("Timed out!\n" + usernames.get(players.peek()) + " has won!", clientIP);
            isGameOn = false;
            timer.cancel();
        }
        return --interval;
    }
}