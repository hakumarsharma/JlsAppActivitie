package com.example.nutapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;

public class JioUtils {
    public static final String MYPREFERENCES = "SETTINGS_JIOTAGS";
    public static final String MYPREFERENCES_PHOTO_CPATURE = "SETTINGS_JIOTAGS_PHOTO_CPATURE";
    public static final String MYPREFERENCES_DISCONNECTION_ALERT = "SETTINGS_JIOTAGS_DISCONNECTION_ALERT";
    public static final int HOME_KEY=1111;
    public static final int LAUNCH_PHONE_ALERT_SETTING=1112;
    public static final int LAUNCH_DEVICE_ALERT_SETTING=1113;
    public static final int LAUNCH_FEEDBACK=222;
    public static final int LAUNCH_INFO=223;
    public static final int LAUNCH_HOW=224;
    public static final int BT_ENABLE_REQUEST=225;


    //Detailed Screen layout
    public static final String MYPREFERENCES_DEVICE_ALERTS = "SETTINGS_JIOTAGS_DEVICE_ALERTS";
    public static final String MYPREFERENCES_PHONE_ALERTS = "SETTINGS_JIOTAGS_PHONE_ALERTS";
    public static final String MYPREFERENCES_PHONE_ALERT_REPEAT = "SETTINGS_JIOTAGS_PHONE_ALERTS_REPEAT";
    public static final int REQUEST_CHECK_SETTINGS_MAIN=555;

    static SharedPreferences preferences;

    public static int getAlertDuration(Context ctx,String device_address,boolean isPhone) {
        Log.d("DEVADD", device_address);
        String phonealert_duration_switch;
        preferences = ctx.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        if(isPhone == true) {
            phonealert_duration_switch = preferences.getString(device_address + "PHONE_ALERT_DURATION", "5sec");
        }else{
            phonealert_duration_switch = preferences.getString(device_address + "DEVICE_ALERT_DURATION", "5sec");
        }

        Log.d("duration_switch", phonealert_duration_switch);
        int alert_duration = 5;
        switch (phonealert_duration_switch) {
            case "5sec":
                alert_duration = 5;
                break;
            case "10sec":
                alert_duration = 10;
                break;
            case "15sec":
                alert_duration = 15;
                break;
            default:
                alert_duration = 5;
                break;
        }
        return alert_duration;
    }


    public static boolean getPhoneAlertRepeat(Context ctx,String device_address) {
        preferences = ctx.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        boolean phonealert_repeat = Boolean.valueOf(preferences.getString(device_address + "PHONE_ALERT_REPEAT", false + ""));
        return phonealert_repeat;
    }

    public static boolean getDeviceAlertRepeat(Context ctx,String device_address) {
        preferences = ctx.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        boolean oldVal = Boolean.valueOf(preferences.getString(device_address + "DEVICE_ALERT_REPEAT", false + ""));
        return oldVal;
    }

    public static boolean getDeviceAlertReconnectionValue(Context ctx,String device_address){
        preferences = ctx.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        boolean devicealert_reconnection = Boolean.valueOf(preferences.getString(device_address + "DEVICE_ALERT_RECONNECTION", true + ""));
        return devicealert_reconnection;
    }

    public static boolean getPhoneAlertSetting(Context ctx, String deviceAddress) {
        preferences = ctx.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        boolean phoneAlertValue = Boolean.valueOf(preferences.getString(deviceAddress + "PHONEALERT", true + ""));
        Log.d("PHONEALERTPREF", deviceAddress + "::::" + phoneAlertValue);
        return phoneAlertValue;
    }

    public static boolean getDeviceAlertSetting(Context ctx, String deviceAddress) {
        preferences = ctx.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        boolean deviceAlertValue = Boolean.valueOf(preferences.getString(deviceAddress + "DEVICEALERT", true + ""));
        Log.d("DEVICEALERTPREF", deviceAddress + "::::" + deviceAlertValue);
        return deviceAlertValue;
    }

    public static boolean getDisconnectionAlertSetting(Context ctx) {
        preferences = ctx.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        boolean disconnectionAlert = preferences.getBoolean(JioUtils.MYPREFERENCES_DISCONNECTION_ALERT, false);
        return disconnectionAlert;
    }

    public static Typeface mTypeface(Context ctx, int typeface) {
        Typeface font1 = null;
        if (typeface == 1) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/JioType-Black.ttf");
        } else if (typeface == 2) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/JioType-Bold.ttf");
        } else if (typeface == 3) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/JioType-Light.ttf");
        } else if (typeface == 4) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/JioType-LightItalic.ttf");
        } else if (typeface == 5) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/JioType-Medium.ttf");
        } else if (typeface == 6) {
            font1 = Typeface.createFromAsset(ctx.getAssets(), "fonts/JioType-MediumItalic.ttf");
        }
        return font1;
    }
}
