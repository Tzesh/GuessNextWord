package edu.anadolu;

import edu.anadolu.game.GuessNextWord;
import edu.anadolu.tcp.TCPClient;
import edu.anadolu.tcp.TCPServer;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        GuessNextWord guessNextWord = new GuessNextWord();
        Scanner scanner = new Scanner(System.in);
        guessNextWord.startGame();
        while (true) {
            guessNextWord.guess(scanner.nextLine());
        }
        /*
        System.out.println("What do you want to set this for?");
        Scanner scanner = new Scanner(System.in);
        if (scanner.nextLine().equals("server")) {
            TCPServer server = new TCPServer();
            server.start(6666);
        } if (scanner.nextLine().equals("player")) {
            TCPClient client = new TCPClient();
            client.startConnection("127.0.0.1", 6666);
            while (true) {
                String response = client.sendMessage(scanner.nextLine());
                scanner.nextLine();

            }
        }
        //TCPServer server = new TCPServer();
        //server.start(6666);
        */
    }
}
