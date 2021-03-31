/**
package edu.anadolu.tcp;

import edu.anadolu.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Deprecated
public class TCPServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String getWord() throws IOException {
        return in.readLine();
    }

    public boolean isConnected() {
        return clientSocket.isConnected();
    }

    public void informTurn(String lastWord) throws IOException {
        if (in.readLine().equals("is it my turn")) out.print(!Main.getPlayer());
        out.print(lastWord);
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
 */