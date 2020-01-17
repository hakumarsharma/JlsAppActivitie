package com.jio.devicetracker.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public final class Util {
    private static Util mUtils;
    private static String sessionID = null;

    private Util() {

    }

    public synchronized static Util getInstance() {
        if (mUtils == null) {
            mUtils = new Util();
        }
        return mUtils;
    }

    public String toJSON(Object pojo) {
        Gson gson = new Gson();
        return gson.toJson(pojo);
    }

    public <T> T getPojoObject(String response, Class<T> pojo) {
        Gson gson = new Gson();
        return gson.fromJson(response, pojo);
    }

    // Method to check Mobile network
    public static boolean isMobileNetworkAvailable(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = connectivity.getAllNetworkInfo();
        if (info != null) {
            for (NetworkInfo networkInfo : info ) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void alertDilogBox(String message, String title, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    public String getSessionId() {
        if(sessionID == null) {
            sessionID = UUID.randomUUID().toString();
            Log.d("Session Id --> ", sessionID);
            return sessionID;
        }
        Log.d("Session Id --> ", sessionID);
        return sessionID;
    }

    public String getIMEI(Context context) {
        if(context != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return telephonyManager.getDeviceId();
            }
        }
        return "IMEI permission is not granted!";
    }

    private int checkSelfPermission(String readPhoneState) {
        Log.d("TAG", readPhoneState);
        return 1;
    }

    public static boolean isValidEmailId(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isValidPassword(String pass) {
        String passRegex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,16})";

        Pattern pat = Pattern.compile(passRegex);
        if (pass == null) {
            return false;
        }
        return pat.matcher(pass).matches();
    }

    public static long convertTimeToEpochtime()
    {
        long epochTime = 0;
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
        String currentTime = simpleFormat.format(today);
        try {
            Date date = simpleFormat.parse(currentTime);
            epochTime = date.getTime();


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return epochTime;
    }
}
