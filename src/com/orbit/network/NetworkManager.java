package com.orbit.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {

    private static NetworkManager instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected = false;
    
    private String serverIp = "192.168.100.3"; // Update this on presentation day!
    private int port = 8080;

    private final List<NetworkListener> listeners = new ArrayList<>();

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        if (instance == null) instance = new NetworkManager();
        return instance;
    }

    public void setServerAddress(String ip) {
        this.serverIp = ip;
    }

    public void connect(String myUsername) {
        if (isConnected) return; 
        try {
            socket = new Socket(serverIp, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isConnected = true;

            if (myUsername != null) out.println("JOIN|" + myUsername);

            new Thread(() -> {
                try {
                    String incomingMessage;
                    while ((incomingMessage = in.readLine()) != null) {
                        broadcastToListeners(incomingMessage);
                    }
                } catch (Exception e) {
                    System.err.println("Lost connection to server.");
                    isConnected = false;
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Could not connect to Orbit Server at " + serverIp);
        }
    }

    public void send(String payload) {
        if (out != null && isConnected) {
            out.println(payload);
        } else {
            System.err.println("Tried to send message, but not connected! Payload: " + payload);
        }
    }

    public void addListener(NetworkListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    private void broadcastToListeners(String message) {
        List<NetworkListener> copy = new ArrayList<>(listeners);
        for (NetworkListener listener : copy) {
            listener.onMessageReceived(message);
        }
    }
}