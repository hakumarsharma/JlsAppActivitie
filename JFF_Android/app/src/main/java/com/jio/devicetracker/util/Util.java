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

package com.jio.devicetracker.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AdminLoginData;

import java.text.DecimalFormat;
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
    public static String adminEmail;
    public static String userToken = "";
    public static String userName = "";
    public static String userId = "";
    public static String userNumber = "";
    public static String googleToken;
    private Date expiryTime;

    private Util() {

    }

    /**
     * Singleton which returns single instance of a Util class
     *
     * @return
     */
    public synchronized static Util getInstance() {
        if (mUtils == null) {
            mUtils = new Util();
        }
        return mUtils;
    }


    /**
     * Converts class to JSON object
     *
     * @param pojo
     * @return
     */
    public String toJSON(Object pojo) {
        Gson gson = new Gson();
        return gson.toJson(pojo);
    }

    /**
     * Returns Pojo class objects
     *
     * @param response
     * @param pojo
     * @param <T>
     * @return
     */
    public <T> T getPojoObject(String response, Class<T> pojo) {
        Gson gson = new Gson();
        return gson.fromJson(response, pojo);
    }

    /**
     * Method to check Mobile network
     *
     * @param mContext
     * @return
     */
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

    /**
     * Common alerts dialog box
     *
     * @param message
     * @param title
     * @param context
     */
    public static void alertDilogBox(String message, String title, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK",
                (dialog, which) -> {
                });
        builder.show();
    }

    /**
     * Returns IMEI number of the phone
     *
     * @param context
     * @return
     */
    public String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (context != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        if ("".equalsIgnoreCase(imeiNumber)) {
            imeiNumber = telephonyManager.getDeviceId();
        }
        return imeiNumber;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            System.out.println("READ_PHONE_STATE permission granted");
        }
    }

    /**
     * Email id validation through RegEx
     *
     * @param email
     * @return
     */
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

    /**
     * Password validation through RegEx
     *
     * @param pass
     * @return
     */
    public static boolean isValidPassword(String pass) {
        String passRegex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,16})";

        Pattern pat = Pattern.compile(passRegex);
        if (pass == null) {
            return false;
        }
        return pat.matcher(pass).matches();
    }

    /**
     * Convert current time to epoch time
     *
     * @return
     */
    public long convertTimeToEpochtime() {
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

    /**
     * Convert current time + given time to epoch time
     *
     * @param min
     * @return
     */
    public long getTimeEpochFormatAfterCertainTime(int min) {
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

    public long createSessionEndDate() {
        long epochTime = 0;
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(sdf.format(today)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 90);
        sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
        Date date = new Date(c.getTimeInMillis());
        epochTime = date.getTime() + 60 * 1000;
        return epochTime;
    }

    /**
     * Get Difference between two epoch time
     *
     * @param fromTime
     * @param toTime
     */
    public String getTrackingExpirirationDuration(long fromTime, long toTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
            Date fromDate = sdf.parse(sdf.format(new Date(fromTime)));
            Date toDate = sdf.parse(sdf.format(new Date(toTime)));
            long diff = toDate.getTime() - fromDate.getTime();
            DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");
            int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            int diffhours = (int) (diff / (60 * 60 * 1000));
            int diffmin = (int) (diff / (60 * 1000));
            int diffsec = (int) (diff / (1000));
            if (diffDays > 0) {
                return crunchifyFormatter.format(diffDays) + (diffDays > 1 ? " days" : "day");
            } else if (diffhours > 0) {
                return crunchifyFormatter.format(diffhours) + (diffhours > 1 ? " hours" : "hour");
            } else if (diffmin > 0) {
                return crunchifyFormatter.format(diffmin) + (diffmin > 1 ? " minutes" : "minute");
            }
            return crunchifyFormatter.format(diffsec) + (diffmin > 1 ? " seconds" : "second");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Constant.EMPTY_STRING;
    }

    /**
     * Common progress bar dialog with message
     *
     * @param context
     * @param message
     */
    public void showProgressBarDialog(Context context, String message) {
        progressDialog = ProgressDialog.show(context, Constant.EMPTY_STRING, message, true);
        progressDialog.setCancelable(true);
    }

    /**
     * To show common progress bar dialog
     *
     * @param context
     */
    public void showProgressBarDialog(Context context) {
        progressDialog = ProgressDialog.show(context, Constant.EMPTY_STRING, Constant.LOADING_DATA, true);
        progressDialog.setCancelable(true);
    }

    /**
     * To dismiss common progress bar dialog
     */
    public void dismissProgressBarDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Sets the terms and condition flag
     *
     * @param mContext
     * @param flag
     */
    public void setTermconditionFlag(Context mContext, boolean flag) {
        sharedpreferences = mContext.getSharedPreferences(Constant.TERM_CONDITION_FLAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("FLAG", flag);
        editor.commit();
    }

    /**
     * returns the terms and condition flag
     *
     * @param mContext
     * @return
     */
    public static boolean getTermconditionFlag(Context mContext) {
        sharedpreferences = mContext.getSharedPreferences(Constant.TERM_CONDITION_FLAG, Context.MODE_PRIVATE);
        if (sharedpreferences != null) {
            return sharedpreferences.getBoolean("FLAG", false);
        }
        return false;
    }

    /**
     * Sets the group Id
     *
     * @param mContext
     * @param groupId
     */
    public static void setGroupId(Context mContext, String groupId) {
        sharedpreferences = mContext.getSharedPreferences(Constant.GROUP_ID_VALUE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constant.GROUP_ID, groupId);
        editor.commit();
    }

    /**
     * returns the group_id
     *
     * @param mContext
     * @return
     */
    public static String getGroupId(Context mContext) {
        sharedpreferences = mContext.getSharedPreferences(Constant.GROUP_ID_VALUE, Context.MODE_PRIVATE);
        if (sharedpreferences != null) {
            return sharedpreferences.getString(Constant.GROUP_ID, "");
        }
        return "";
    }


    /**
     * Sets Login status
     *
     * @param mContext
     * @param flag
     */
    public void setAutologinStatus(Context mContext, boolean flag) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.AUTO_LOGIN_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedAutologin.edit();
        editor.putBoolean(Constant.AUTO_LOGIN, flag);
        editor.commit();
    }

    /**
     * Returns Login status
     *
     * @param mContext
     * @return
     */
    public boolean getAutologinStatus(Context mContext) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.AUTO_LOGIN_STATUS, Context.MODE_PRIVATE);
        return sharedAutologin.getBoolean(Constant.AUTO_LOGIN, false);
    }

    /**
     * Clear Login status
     *
     * @param mContext
     */
    public static void clearAutologinstatus(Context mContext) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.AUTO_LOGIN_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedAutologin.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Returns MQTT time format
     *
     * @return
     */
    public String getMQTTTimeFormat() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        return sdf.format(c.getTime());
    }

    /**
     * IMEI number validation through Regex
     *
     * @param imei
     * @return
     */
    public static boolean isValidIMEINumber(String imei) {
        String imeiNumber = "^\\d{15}$";
        Pattern pat = Pattern.compile(imeiNumber);
        return pat.matcher(imei).matches();
    }

    /**
     * Mobile Number validation through RegEx
     *
     * @param mobile
     * @return
     */
    public static boolean isValidMobileNumber(String mobile) {
        String mobileNumber = "^[6-9][0-9]{9}$";
        Pattern pat = Pattern.compile(mobileNumber);
        return pat.matcher(mobile).matches();
    }

    /**
     * Device Number validation through RegEx for pet
     *
     * @param mobile
     * @return
     */
    public static boolean isValidMobileNumberForPet(String mobile) {
        String mobileNumber = "^[0-9]{13}$";
        Pattern pat = Pattern.compile(mobileNumber);
        return pat.matcher(mobile).matches();
    }

    /**
     * To set location flag status in Shared Prefrences variable
     *
     * @param mContext
     * @param flag
     */
    public static void setLocationFlagStatus(Context mContext, boolean flag) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.LOCATION_FLAG_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedAutologin.edit();
        editor.putBoolean(Constant.AUTO_LOGIN, flag);
        editor.commit();
    }

    /**
     * Returns Login status
     *
     * @param mContext
     * @return
     */
    public static boolean getLocationFlagStatus(Context mContext) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.LOCATION_FLAG_STATUS, Context.MODE_PRIVATE);
        return sharedAutologin.getBoolean(Constant.AUTO_LOGIN, false);
    }

    /**
     * Clear Login status
     *
     * @param mContext
     */
    public static void clearLocationFlagstatus(Context mContext) {
        SharedPreferences sharedAutologin = mContext.getSharedPreferences(Constant.LOCATION_FLAG_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedAutologin.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Returns the loged in user token and his email id
     *
     * @param context
     */
    public static void getAdminDetail(Context context) {
        AdminLoginData adminLoginData = new DBManager(context).getAdminLoginDetail();
        if (adminLoginData != null) {
            userToken = adminLoginData.getUserToken();
            userName = adminLoginData.getName();
            userId = adminLoginData.getUserId();
            userNumber = adminLoginData.getPhoneNumber();
        }
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

    public void updateGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setExpiryTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
        expiryTime = calendar.getTime();
    }

    public boolean isGoogleTokenExpired() {
        return Calendar.getInstance().getTime().after(expiryTime);
    }
}
