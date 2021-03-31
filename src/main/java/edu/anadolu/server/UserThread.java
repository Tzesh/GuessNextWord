package edu.anadolu.server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UserThread extends Thread {
	private Socket socket;
	private Server server;
	private PrintWriter writer;

	public UserThread(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);

			printUsers();

			String userName = reader.readLine();
			server.addUserName(userName);

			String serverMessage = "New user connected: " + userName;
			server.broadcast(serverMessage, this);

			String clientMessage;

			do {
				clientMessage = reader.readLine();
				if (clientMessage.contains("login")) {
					String[] args = clientMessage.split(" ");
					if (args.length == 1 || args.length > 2) {
						sendMessage("Please re-type your password");
						continue;
					}
					else server.loginAsAdministrator(args[1], this);
					continue;
				}
				if (clientMessage.equals("start the game TR")) {
					server.startGame(this, new Locale("tr-TR"));
					continue;
				}
				if (clientMessage.equals("start the game EN")) {
					server.startGame(this, new Locale("en-US"));
					continue;
				}
				serverMessage = "[" + userName + "]: " + clientMessage;
				server.broadcast(serverMessage, this);
			} while (!clientMessage.equals("quit"));

			server.removeUser(userName, this);
			socket.close();

			serverMessage = userName + " has quitted.";
			server.broadcast(serverMessage, this);

		} catch (IOException ex) {
			System.out.println("Error in UserThread: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	void printUsers() {
		if (server.hasUsers()) {
			writer.println("Connected users: " + server.getUserNames());
		} else {
			writer.println("No other users connected");
		}
	}

	void sendMessage(String message) {
		writer.println(message);
	}

	public Socket getSocket() {
		return socket;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserThread) {
			return socket == ((UserThread) obj).getSocket();
		}
		return false;
	}
}