package edu.anadolu.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class ReceiverThread extends Thread {
    private DatagramSocket socket;
    private boolean stopped = false;

    public ReceiverThread(DatagramSocket socket) {
        this.socket = socket;
    }

    public void halt() {
        this.stopped = true;
    }

    public void run() {
        byte[] receiveData = new byte[1024];

        while (true) {
            if (stopped)
                return;

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                socket.receive(receivePacket);
                String serverReply =  new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println(serverReply);

                Thread.yield();
            }
            catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}