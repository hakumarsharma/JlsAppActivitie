// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Implementation of Utils class to write common methods .
 */
public final class Util extends AppCompatActivity {
    private static Util mUtils;
    public static ProgressDialog progressDialog = null;
    public static SharedPreferences sharedpreferences = null;
    public static String imeiNumber = "";

    private Util() {

    }

    // Singleton which returns single instance of a Util class
    public synchronized static Util getInstance() {
        if (mUtils == null) {
            mUtils = new Util();
        }
        return mUtils;
    }

    // Converts class to JSON object
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
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    // Common alerts dialog box
    public static void alertDilogBox(String message, String title, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK",
                (dialog, which) -> {
                });
        builder.show();
    }

    // Returns IMEI number of the phone
    public String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (context != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        if (imeiNumber.equals("")) {
            imeiNumber = telephonyManager.getDeviceId();
        }
        Log.d("IMEI Number --> ", imeiNumber);
        return imeiNumber;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Log.d("Permission", "READ_PHONE_STATE permission granted");
        }
    }


    // Email id validation through RegEx
    public static boolean isValidEmailId(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    // Password validation through RegEx
    public static boolean isValidPassword(String pass) {
        String passRegex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,16})";

        Pattern pat = Pattern.compile(passRegex);
        if (pass == null) {
            return false;
        }
        return pat.matcher(pass).matches();
    }

    // Convert current time to epoch time
    public static long convertTimeToEpochtime() {
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

    // Convert current time + given time to epoch time
    public static long getTimeEpochFormatAfterCertainTime(int min) {
        long epochTime = 0;
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
        String currentTime = simpleFormat.format(today);
        try {
            Date date = simpleFormat.parse(currentTime);
            epochTime = date.getTime() + min * 60 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return epochTime;
    }

    // Common progress bar dialog with message
    public void showProgressBarDialog(Context context, String message) {
        progressDialog = ProgressDialog.show(context, "", message, true);
        progressDialog.setCancelable(true);
    }

    // Common progress bar dialog
    public void showProgressBarDialog(Context context) {
        progressDialog = ProgressDialog.show(context, "", Constant.LOADING_DATA, true);
        progressDialog.setCancelable(true);
    }

    public void dismissProgressBarDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    // Sets the terms and condition flag
    public static void setTermconditionFlag(Context mContext, boolean flag) {
        sharedpreferences = mContext.getSharedPreferences(Constant.TERM_CONDITION_FLAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("FLAG", flag);
        editor.commit();
    }

    // Returns the terms and condition flag
    public static boolean getTermconditionFlag(Context mContext) {
        sharedpreferences = mContext.getSharedPreferences(Constant.TERM_CONDITION_FLAG, Context.MODE_PRIVATE);
        if (sharedpreferences != null) {

            return sharedpreferences.getBoolean("FLAG", false);
        }
        return false;
    }

    // Sets Login status
    public static void setAutologinStatus(Context mContext, boolean flag) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.AUTO_LOGIN_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedAutologin.edit();
        editor.putBoolean(Constant.AUTO_LOGIN, flag);
        editor.commit();
    }

    // Returns Login status
    public static boolean getAutologinStatus(Context mContext) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.AUTO_LOGIN_STATUS, Context.MODE_PRIVATE);
        return sharedAutologin.getBoolean(Constant.AUTO_LOGIN, false);
    }

    // Clear Login status
    public static void clearAutologinstatus(Context mContext) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.AUTO_LOGIN_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedAutologin.edit();
        editor.clear();
        editor.commit();
    }

    // Returns MQTT time format
    public String getMQTTTimeFormat() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        return sdf.format(c.getTime());
    }

    // IMEI number validation through Regex
    public static boolean isValidIMEINumber(String imei) {
        String imeiNumber = "^\\d{15}$";
        Pattern pat = Pattern.compile(imeiNumber);
        return pat.matcher(imei).matches();
    }

    // Mobile Number validation through RegEx
    public static boolean isValidMobileNumber(String mobile) {
        String mobileNumber = "^[6-9][0-9]{9}$";
        Pattern pat = Pattern.compile(mobileNumber);
        return pat.matcher(mobile).matches();
    }

    public static void setLocationFlagStatus(Context mContext, boolean flag) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.LOCATION_FLAG_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedAutologin.edit();
        editor.putBoolean(Constant.AUTO_LOGIN, flag);
        editor.commit();
    }

    // Returns Login status
    public static boolean getLocationFlagStatus(Context mContext) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.LOCATION_FLAG_STATUS, Context.MODE_PRIVATE);
        return sharedAutologin.getBoolean(Constant.AUTO_LOGIN, false);
    }

    // Clear Login status
    public static void clearLocationFlagstatus(Context mContext) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.LOCATION_FLAG_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedAutologin.edit();
        editor.clear();
        editor.commit();
    }


}
