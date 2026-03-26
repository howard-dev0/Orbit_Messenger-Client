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
    
    private String serverIp = "127.0.0.1"; // Default to localhost for testing
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
        if (isConnected) {
            System.out.println("⚠️ NetworkManager: Already connected. Ignoring connect() call.");
            return; 
        }
        
        try {
            System.out.println("🔄 NetworkManager: Attempting to connect to " + serverIp + ":" + port + "...");
            socket = new Socket(serverIp, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isConnected = true;
            
            System.out.println("✅ NetworkManager: Connection established!");

            if (myUsername != null && !myUsername.isEmpty()) {
                System.out.println("📡 NetworkManager: Sending JOIN for " + myUsername);
                out.println("JOIN|" + myUsername);
            }

            new Thread(() -> {
                try {
                    String incomingMessage;
                    while ((incomingMessage = in.readLine()) != null) {
                        broadcastToListeners(incomingMessage);
                    }
                    // If we reach this line, the Server cleanly closed the connection (EOF)
                    System.err.println("❌ NetworkManager: Server closed the connection (End of Stream).");
                } catch (Exception e) {
                    // If we reach this line, the connection was violently broken (Server crash or network drop)
                    System.err.println("❌ NetworkManager: Connection lost violently!");
                    e.printStackTrace();
                } finally {
                    isConnected = false;
                    System.out.println("🔌 NetworkManager: Socket closed and marked as disconnected.");
                }
            }).start();
        } catch (Exception e) {
            System.err.println("❌ NetworkManager: Could not connect to Orbit Server at " + serverIp);
            e.printStackTrace();
        }
    }

    public void send(String payload) {
        if (out != null && isConnected) {
            System.out.println("📤 NetworkManager Sending: " + payload);
            out.println(payload);
        } else {
            System.err.println("⛔ Tried to send message, but NOT CONNECTED! Payload: " + payload);
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