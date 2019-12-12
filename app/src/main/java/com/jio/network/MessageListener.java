package com.jio.network;

public interface MessageListener {

    void messageReceived(String message, String phoneNum);
}