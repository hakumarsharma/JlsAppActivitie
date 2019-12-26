package com.jio.devicetracker.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.jio.devicetracker.database.pojo.AddedDeviceData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Util {
    private static Util mUtils;
    private static List<AddedDeviceData> list;
    static SharedPreferences sharedpreferences = null;
    public static final String MyPREFERENCES = "MyPrefs";

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
}
