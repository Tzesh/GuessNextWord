package edu.anadolu;

import edu.anadolu.game.GuessNextWord;
import edu.anadolu.tcp.TCPClient;
import edu.anadolu.tcp.TCPServer;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static GuessNextWord guessNextWord = new GuessNextWord();

    public static void main(String[] args) throws IOException {
        System.out.println("Menu" +
                "\na) Host game" +
                "\nb) Join game");
        String option = scanner.nextLine();
        if (option.equals("a")) {
            TCPServer server = new TCPServer();
            server.start(6666);
            System.out.println("Waiting for Player 2 to start");
            while (!server.isConnected()) { }
            System.out.println("Player 2 has joined!");
            guessNextWord.startGame();
            while (guessNextWord.isGameOver()) {
                if (guessNextWord.getPlayer()) {
                    String word = scanner.nextLine();
                    guessNextWord.guess(word);
                }
                else {
                    server.informTurn();
                    guessNextWord.guess(server.getWord());
                }
            }
        }
        if (option.equals("b")) {
            TCPClient client = new TCPClient();
            client.startConnection("127.0.0.1", 6666);
            while (guessNextWord.isGameOver()) {
                if (client.sendMessage("is it my turn").equals("yes")) {
                    String response = scanner.nextLine();
                    client.sendMessage(response);
                }
            }
        }
    }

    public static boolean getPlayer() {
        return guessNextWord.getPlayer();
    }
}
