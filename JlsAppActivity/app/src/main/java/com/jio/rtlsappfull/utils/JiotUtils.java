package com.jio.rtlsappfull.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.graphics.Typeface;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;


public class JiotUtils {

    public static final int SPLASH_TIME_OUT = 3000;
    public static final int ALARM_1_MINUTE = 60000;
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
    public static final long RTLS_WAIT_PERIOD = 15000;
    public static final String RTLS_SERVICE_ID_MLS = "serviceid=test-100";
    public static final String RTLS_SERVICE_ID_JIODB = "serviceid=test-101";
    public static final float RTLS_LOCATION_ACCURACY_THRESHOLD = 100f;

    public static final String RTLS_SERVICE_ID_MLS_MOZ = "test-100";
    public static final String RTLS_SERVICE_ID_MLS_JIO = "test-101";
    public static final String RTLS_SERVICE_ID_COMBAIN = "test-102";
    public static final String RTLS_SERVICE_ID_SKY = "test-103";
    public static final String RTLS_SERVICE_ID_TRI = "test-104";
    public static final String RTLS_SERVICE_ID_ALL = "test-200";
    public static double sLang = 0;
    public static double slon = 0;
    public static boolean isRefreshed = false;
    public static boolean isTokenExpired = false;
    public static final String IMEI_NUMBER = "123456789012345";
    private static JiotUtils mUtils;

    public static String getAddressFromLatLng(LatLng current, Context context) {
        String locationAddress = "NO ADDRESS";
        if (current != null) {
            Log.d("LOCATION:", current.latitude + " " + current.longitude);
            List<Address> addresses = null;
            Geocoder geocoder;
            Locale loc = Locale.getDefault();
            geocoder = new Geocoder(context, loc);

            try {
                addresses = geocoder.getFromLocation(
                        current.latitude,
                        current.longitude,
                        // In this sample, get just a single address.
                        1);
                Address address = addresses.get(0);
                Log.d("ADDRESS", address.getAddressLine(0));
                locationAddress = address.getAddressLine(0);
                Toast.makeText(context, "Current Location is: " + address.getAddressLine(0), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.d("LOCATIONEXCEPTION", "EXCEPTION");
                e.printStackTrace();
            }
        }
        return locationAddress;
    }


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


    public static String getDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        String datetime = dateformat.format(c.getTime());
        System.out.println(datetime);
        return datetime;
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
        preferences = ctx.getSharedPreferences(RTLS_MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        prefEditor.putString(RTLS_MYPREFERENCES_TOKEN, rtls_token);
        Log.d("RTLSTOKEN", "RTLS TOKEN = " + rtls_token);
        prefEditor.commit();
    }

    public static String jiotgetRtlsToken(Context ctx) {
        preferences = ctx.getSharedPreferences(RTLS_MYPREFERENCES, Context.MODE_PRIVATE);
        String rtls_token = preferences.getString(RTLS_MYPREFERENCES_TOKEN, RTLS_MYPREFERENCES_NONE);
        Log.d("RTLSTOKEN = ", rtls_token);
        return rtls_token;
    }

    public static void jiotwriteRtlsDid(Context ctx, String rtls_did) {
        preferences = ctx.getSharedPreferences(RTLS_MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        prefEditor.putString(RTLS_MYPREFERENCES_DID, rtls_did);
        Log.d("RTLSTOKEN", "RTLS DID = " + rtls_did);
        prefEditor.commit();
    }

    public static String jiotgetRtlsDid(Context ctx) {
        preferences = ctx.getSharedPreferences(RTLS_MYPREFERENCES, Context.MODE_PRIVATE);
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

    public static float jiotGetDeltaDistance(LatLng rtlsCell, LatLng googleGps) {
        float[] results = new float[1];
        Location.distanceBetween(rtlsCell.latitude, rtlsCell.longitude, googleGps.latitude, googleGps.longitude, results);
        float distanceInMeters = results[0];
        Log.d("DISTANCEM", distanceInMeters + "");
        //10KM=10,000 M
        return distanceInMeters;
    }

    public static void showErrorToast(Context context, String errorMsg) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String errorConsent = sharedPreferences.getString("error_consent", null);
        if (errorConsent.equalsIgnoreCase("Yes"))
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
    }

    /**
     * Singleton which returns single instance of a Util class
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

}
