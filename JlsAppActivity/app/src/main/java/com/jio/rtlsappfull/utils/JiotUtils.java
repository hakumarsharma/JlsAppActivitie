package com.jio.rtlsappfull.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import android.graphics.Typeface;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;


public class JiotUtils {

    public static final int SPLASH_TIME_OUT = 3000;
    public static final int FLOAT_REFRESH_100MILI = 100;

    public static boolean isGsmPrimary = false;
    public static boolean isCdmaPrimary = false;
    public static boolean isWcdmaPrimary = false;
    public static boolean isLtePrimary = false;

    static SharedPreferences preferences;
    static SharedPreferences.Editor prefEditor;
    public static final String RTLS_MYPREFERENCES = "RTLS_SERVER";
    public static final String RTLS_MYPREFERENCES_TOKEN = "RTLS_SERVER_TOKEN";
    public static final String RTLS_MYPREFERENCES_DID = "RTLS_SERVER_DID";
    public static final String RTLS_MYPREFERENCES_NONE = "NONE";

    //10 seconds
    public static final long RTLS_SCAN_PERIOD = 10000;
    public static final String RTLS_SERVICE_ID_MLS_MOZ = "test-100";
    public static final String RTLS_SERVICE_ID_MLS_JIO = "test-101";
    public static final String RTLS_SERVICE_ID_COMBAIN = "test-102";
    public static final String RTLS_SERVICE_ID_SKY = "test-103";
    public static final String RTLS_SERVICE_ID_TRI = "test-104";
    public static double sLang = 0;
    public static double slon = 0;
    public static boolean isRefreshed = false;
    public static boolean isTokenExpired = false;
    public static final String IMEI_NUMBER = "123456789012345";
    private static JiotUtils mUtils;
    private static String mobileNumber;

    public static String getVolleyError(VolleyError error) {
        String responseBody = "NODATA";
        try {
            if (error.networkResponse != null) {
                Log.d("VOLLEYERR", error.networkResponse.data.toString());
                responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                String message = data.optString("msg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    public static boolean jiotisNetworkEnabled(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean jiotisLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    public static void jiotwriteRtlsToken(Context ctx, String rtls_token) {
        preferences = ctx.getSharedPreferences(RTLS_MYPREFERENCES, MODE_PRIVATE);
        prefEditor = preferences.edit();
        prefEditor.putString(RTLS_MYPREFERENCES_TOKEN, rtls_token);
        Log.d("RTLSTOKEN", "RTLS TOKEN = " + rtls_token);
        prefEditor.commit();
    }

    public static String jiotgetRtlsToken(Context ctx) {
        preferences = ctx.getSharedPreferences(RTLS_MYPREFERENCES, MODE_PRIVATE);
        String rtls_token = preferences.getString(RTLS_MYPREFERENCES_TOKEN, RTLS_MYPREFERENCES_NONE);
        Log.d("RTLSTOKEN = ", rtls_token);
        return rtls_token;
    }

    public static void jiotwriteRtlsDid(Context ctx, String rtls_did) {
        preferences = ctx.getSharedPreferences(RTLS_MYPREFERENCES, MODE_PRIVATE);
        prefEditor = preferences.edit();
        prefEditor.putString(RTLS_MYPREFERENCES_DID, rtls_did);
        Log.d("RTLSTOKEN", "RTLS DID = " + rtls_did);
        prefEditor.commit();
    }

    public static String jiotgetRtlsDid(Context ctx) {
        preferences = ctx.getSharedPreferences(RTLS_MYPREFERENCES, MODE_PRIVATE);
        String rtls_token = preferences.getString(RTLS_MYPREFERENCES_DID, RTLS_MYPREFERENCES_NONE);
        Log.d("RTLSDID = ", rtls_token);
        return rtls_token;
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

    public static void showErrorToast(Context context, String errorMsg) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared_prefs", MODE_PRIVATE);
        String errorConsent = sharedPreferences.getString("error_consent", null);
        if (errorConsent.equalsIgnoreCase("Yes"))
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
    }

    /**
     * Singleton which returns single instance of a Util class
     *
     * @return
     */
    public synchronized static JiotUtils getInstance() {
        if (mUtils == null) {
            mUtils = new JiotUtils();
        }
        return mUtils;
    }

    /**
     * Returns Pojo class objects
     * @param response
     * @param pojo
     * @param <T>
     * @return
     */
    public <T> T getPojoObject(String response, Class<T> pojo) {
        Gson gson = new Gson();
        return gson.fromJson(response, pojo);
    }

    public static String getMobileNumber(Context context) {
        if(mobileNumber == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            mobileNumber = sharedPreferences.getString("mob", "1234567890");
        }
        return mobileNumber;
    }

    public static void saveMobileNumber(Context context, String mobileNumber) {
        SharedPreferences.Editor editor = context.getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
        editor.putString("mob", mobileNumber);
        editor.commit();
    }
}
