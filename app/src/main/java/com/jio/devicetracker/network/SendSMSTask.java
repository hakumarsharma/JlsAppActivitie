// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.network;

import android.os.AsyncTask;
import android.telephony.SmsManager;


public class SendSMSTask extends AsyncTask<String, String, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(strings[0], null, strings[1], null, null);
        return null;
    }
}
