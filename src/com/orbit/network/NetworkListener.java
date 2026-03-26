package com.orbit.network;

public interface NetworkListener {
    // This method gets triggered automatically whenever the server sends a message
    void onMessageReceived(String message);
}