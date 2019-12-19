// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class MessageReceiver extends BroadcastReceiver {

    private static MessageListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (Object o : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);
            String message = "Sender : " + smsMessage.getDisplayOriginatingAddress()
                    + "Email From: " + smsMessage.getEmailFrom()
                    + "Emal Body: " + smsMessage.getEmailBody()
                    + "Display message body: " + smsMessage.getDisplayMessageBody()
                    + "Time in millisecond: " + smsMessage.getTimestampMillis()
                    + "Message: " + smsMessage.getMessageBody();
            mListener.messageReceived(smsMessage.getDisplayMessageBody(), smsMessage.getDisplayOriginatingAddress());
            Log.d("Incoming message", message);

        }
    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }
}