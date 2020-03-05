/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

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
            if(smsMessage != null && mListener != null) {
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
    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }
}