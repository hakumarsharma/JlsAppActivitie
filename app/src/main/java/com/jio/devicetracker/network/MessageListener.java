// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.network;

public interface MessageListener {
    void messageReceived(String message, String phoneNum);
}