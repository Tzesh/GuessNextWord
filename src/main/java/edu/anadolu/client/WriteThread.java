package edu.anadolu.client;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class WriteThread extends Thread {
	private PrintWriter writer;
	private Socket socket;
	private Client client;

	public WriteThread(Socket socket, Client client) {
		this.socket = socket;
		this.client = client;

		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true);
		} catch (IOException ex) {
			System.out.println("Error getting output stream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		System.out.println("\nEnter your nickname: ");
		String userName = scanner.nextLine();
		client.setUserName(userName);
		writer.println(userName);

		String text = null;

		do {
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