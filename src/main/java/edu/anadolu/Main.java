package edu.anadolu;

import edu.anadolu.client.Client;
import edu.anadolu.server.Server;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8)); // to avoid OS dependent outputs

        System.out.println("GuessNextWord | Menu" +
                "\na) Host game: You can host a game up to 2 clients to let them play" +
                "\nb) Join game: You can join a game to compete with another client");
        String option = scanner.nextLine();
        if (option.equals("a")) {
            int port = getInteger("Please declaim a port to host the server: ");
            String password = getString("Please enter your administrator password: ");
            Server server = new Server(port, password);
            server.execute();
        }
        if (option.equals("b")) {
            String hostname = getString("Please type your hostname: ");
            int port = getInteger("Please enter your port to join to the server: ");
            Client client = new Client(hostname, port);
            client.execute();
        }
    }

    public static int getInteger(String message) {
        int integer = 0;
        System.out.print(message);
        try {
            while (true) {
                integer = scanner.nextInt();
                if (integer > 0) return integer;
            }
        }
        catch (InputMismatchException exception) {
            System.out.print("Please provide a suitable positive integer value");
        }
        return 6666;
    }

    public static String getString(String message) {
        String string = "";
        System.out.print(message);
        try {
            while (true) {
                string = scanner.nextLine();
                if (!string.isEmpty()) return string;
            }
        }
        catch (InputMismatchException exception) {
            System.out.print("Please provide a suitable string hostname");
        }
        return "localhost"; // default
    }
}
