// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.network;

public interface MessageListener {
    void messageReceived(String message, String phoneNum);
}