package com.jio.devicetracker.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.jio.devicetracker.database.pojo.AddedDeviceData;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public class Util {
    private static Util mUtils;
    private static List<AddedDeviceData> list;
    static SharedPreferences sharedpreferences = null;
    public static final String MyPREFERENCES = "MyPrefs";
    private static String sessionID = null;
    ProgressDialog progressDialog = null;

    private Util() {

    }

    public static Util getInstance() {
        if (mUtils == null) {
            mUtils = new Util();
        }
        return mUtils;
    }

    public String toJSON(Object pojo) {
        Gson gson = new Gson();
        String json = gson.toJson(pojo);
        return json;
    }

    public <T> T getPojoObject(String response, Class<T> pojo) {
        T t = null;
        Gson gson = new Gson();
        t = gson.fromJson(response, pojo);
        return t;
    }

    public void setUserToken(Context mContext, String userToken) {
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("UserToken", userToken);
        editor.commit();
    }

    public static String getUserToken() {
        if (sharedpreferences != null) {
            String token = sharedpreferences.getString("UserToken", "");
            return token;
        }
        return "";
    }

    public static void setListvalue(List<AddedDeviceData> mList) {
        list = mList;

    }

    public static List<AddedDeviceData> getAddedDevicelist() {
        return list;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    // Method to check Mobile network
    public static boolean isMobileNetworkAvailable(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = connectivity.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getType() == ConnectivityManager.TYPE_MOBILE && info[i].isConnected()) {
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

    public String getFourDigitRandomNumber(){
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    public void showProgressBarDialog(Context context, String message) {
        progressDialog = ProgressDialog.show(context, "", message, true);
        progressDialog.setCancelable(true);
    }

    public void dismissProgressBarDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static long ConvertTimeToEpochtime()
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
