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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
