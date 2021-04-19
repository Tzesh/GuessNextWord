package edu.anadolu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    // Server structures
    private int port;
    private ServerSocket serverSocket = new ServerSocket(port);
    private UserThread administrator = null;
    private String password = null;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    private LinkedList<UserThread> players = new LinkedList<>();
    // Game structures
    private boolean isGameOn = false;
    private Set<String> vocabulary = null;
    private String lastWord;
    private int interval;
    private Timer timer;
    private Locale locale = null;

    public Server(int port, String password) throws IOException {
        this.password = password;
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                if (isGameOn) {
                    System.out.println("There is a game currently running on in the server, please try again later.");
                    continue;
                }
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void broadcast(String message, UserThread excludeUser) {
        if (isGameOn) {
            if (players.peek() == excludeUser) guess(message.toLowerCase(locale), excludeUser);
            else excludeUser.sendMessage("It's not your turn, please wait your turn!");
            return;
        }
        for (UserThread aUser : userThreads) {
            if (excludeUser != null)
                if (aUser != excludeUser) {
                    aUser.sendMessage(message);
                }
        }
    }

    private void broadcastAll(String message) {
        for (UserThread aUser : userThreads) {
            aUser.sendMessage(message);
        }
    }

    boolean isGameOn() {
        return isGameOn;
    }

    void loginAsAdministrator(String password, UserThread user) {
        if (!password.equals(this.password)) {
            user.sendMessage("Password is wrong!");
            return;
        }
        if (administrator != null) {
            user.sendMessage("There can be only one administrator at a time!");
            return;
        }
        administrator = user;
        user.sendMessage("Welcome back, sir.");
        broadcast("Administrator " + user.getUsername() + " has logged in!", user);
    }

    void startGame(UserThread administrator, Locale locale) {
        if (this.administrator == null) {
            administrator.sendMessage("Seems like administrator has not logged in yet");
            return;
        }
        if (this.administrator != administrator) {
            administrator.sendMessage("You are not administrator!");
            return;
        }
        if (isGameOn) {
            administrator.sendMessage("There is a already game running on!");
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
                setInterval();
            }
        }, 1000, 1000);
        isGameOn = true;
        broadcastAll("The game has started!");
        players.addAll(userThreads);
        players.peek().sendMessage("It's your turn, just type a meaningful word between 30 seconds.");
    }

    public boolean guess(String word, UserThread user) {
        System.out.println(word);
        if (word.isBlank() || word.isEmpty() || word.contains("\\p{Punct}") || word.contains("\\s+")) {
            user.sendMessage("Wrong usage!");
            return false;
        }
        if (vocabulary.contains(word)) {
            user.sendMessage("This word has been used already");
            return false;
        }
        if (lastWord != null && !word.substring(0, 2).equals(lastWord.substring(lastWord.length() - 2))) {
            user.sendMessage("New word is not starting with the last 2 letters of the last one");
            return false;
        }
        vocabulary.add(word);
        lastWord = word;
        changeTurns(user);
        return true;
    }

    private void changeTurns(UserThread user) {
        players.poll();
        players.add(user);
        players.peek().sendMessage("It is your turn, last word is: " + lastWord);
        interval = 30;
    }

    private int setInterval() {
        if (interval == 15) players.peek().sendMessage("15 seconds left");
        if (interval < 10) players.peek().sendMessage(String.format("%s second(s) left!", interval));
        if (interval == 1) {
            players.poll();
            broadcastAll("Timed out!\n" + players.peek().getUsername() + " has won!");
            isGameOn = false;
            timer.cancel();
        }
        return --interval;
    }

    void addUserName(String userName) {
        userNames.add(userName);
    }


    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quited");
        }
    }


    Set<String> getUserNames() {
        return this.userNames;
    }

    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}