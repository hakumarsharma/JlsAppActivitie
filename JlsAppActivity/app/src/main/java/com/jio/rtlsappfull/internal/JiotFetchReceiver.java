package com.jio.rtlsappfull.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class JiotFetchReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("1SECALARM","onReceive");
        Intent sendFetchIntent = new Intent();
        sendFetchIntent.setAction("com.jio.fetchLocation");
        context.sendBroadcast(sendFetchIntent);
    }
}
