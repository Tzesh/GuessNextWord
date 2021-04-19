package edu.anadolu.client;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ReadThread extends Thread {
	private BufferedReader reader;
	private Socket socket;
	private Client client;

	public ReadThread(Socket socket, Client client) {
		this.socket = socket;
		this.client = client;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
		} catch (IOException ex) {
			System.out.println("Error getting input stream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				String response = reader.readLine();
				System.out.println("\n" + response);

				if (client.getUserName() != null) {
					System.out.print("[" + client.getUserName() + "]: ");
				}
			} catch (IOException ex) {
				System.out.println("Error reading from server: " + ex.getMessage());
				ex.printStackTrace();
				break;
			}
		}
	}
}