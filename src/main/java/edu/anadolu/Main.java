package edu.anadolu;

import edu.anadolu.tcp.client.Client;
import edu.anadolu.tcp.server.Server;
import edu.anadolu.udp.client.UDPClient;
import edu.anadolu.udp.server.UDPServer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8)); // to avoid OS dependent outputs

        System.out.println("GuessNextWord | Menu" +
                "\na) Host game: You can host a game up to 2 clients to let them play" +
                "\nb) Join game: You can join a game to compete with another client");
        String option = scanner.nextLine();
        if (option.equals("a")) { // hosting game
            int port = getInteger("Please declaim a port to host the server: ");
            String password = getString("Please enter your administrator password: ", false);
            System.out.println("Starting the server on port '" + port + "' and administrator password '" + password + "' ");
            UDPServer server = new UDPServer(password, port);
            server.execute();
        }
        if (option.equals("b")) { // joining game
            String hostname = getString("Please type your hostname: ", true);
            int port = getInteger("Please enter your port to join to the server: ");
            UDPClient client = new UDPClient(hostname, port);
            client.execute();
        }
    }

    public static int getInteger(String message) { // to get integer values from scanner
        int integer = 0;
        System.out.print(message);
        try {
            while (true) {
                integer = scanner.nextInt();
                if (integer > 0) return integer;
            }
        } catch (InputMismatchException exception) {
            System.out.println("Returning default port '6666' since the value is not integer");
        }
        return 6666;
    }

    public static String getString(String message, boolean isHost) { // to get string values from scanner
        String string = "";
        System.out.print(message);
        try {
            while (true) {
                string = scanner.nextLine();
                if (!string.isEmpty()) return string;
            }
        } catch (InputMismatchException exception) {
            System.out.print("Please provide a suitable string.");
        }
        return isHost ? "localhost" : "12345"; // default
    }
}