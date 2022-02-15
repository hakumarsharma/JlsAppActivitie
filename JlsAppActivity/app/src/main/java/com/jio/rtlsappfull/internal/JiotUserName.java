package com.jio.rtlsappfull.internal;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.config.Config;
import com.jio.rtlsappfull.log.JiotSdkFileLogger;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import static com.jio.rtlsappfull.config.Config.SERVER_PREPROD_API_KEY_URL;

public class JiotUserName extends AppCompatActivity {

    private EditText m_userid_edit_number;
    private Button m_user_id_submit;
    public static JiotSdkFileLogger m_jiotSdkFileLoggerInstance = null;
    public static final int REQUEST_CHECK_SETTINGS = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 110;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_id);
        if (!JiotUtils.jiotisLocationEnabled(this)) {
            showGPS();
        } else {
            checkLocationPermission();
        }
        m_jiotSdkFileLoggerInstance = JiotSdkFileLogger.JiotGetFileLoggerInstance(this);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(R.string.mobile_number);
        m_userid_edit_number = (EditText) findViewById(R.id.userid_edit_email_number);
        m_user_id_submit = (Button) findViewById(R.id.user_id_submit);
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
        int counter = sharedPreferences.getInt("counter", 2);
        if (counter != -1) {
            counter --;
            editor.putInt("counter", counter);
            editor.commit();
        }
        m_user_id_submit.setOnClickListener(v -> {
            if (JiotUtils.jiotisNetworkEnabled(this)) {
                Log.d("SUBMIT", "submit button clicked");
                String userNumber = m_userid_edit_number.getText().toString();
                Log.d("User phone", userNumber);
                if (userNumber == null || userNumber.isEmpty() || userNumber.length() != 10) {
                    Toast.makeText(this, getResources().getString(R.string.mobile_number_length), Toast.LENGTH_SHORT).show();
                } else {
                    JiotUtils.jiotwriteRtlsDid(JiotUserName.this, userNumber);
                    JiotUtils.saveMobileNumber(JiotUserName.this, userNumber);
                    fetchRtlsKey(userNumber);
                }
            } else {
                Toast.makeText(this, "Please enable your internet connection, before proceeding", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Following broadcast receiver is to listen the Location button toggle state in Android.
     */
    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (isGpsEnabled == false || isNetworkEnabled == false)
                    showGPS();
                else {
                    checkLocationPermission();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mGpsSwitchStateReceiver);
    }

    public JSONObject createV2UserIdObject(String name) {
        try {
            JSONObject jsonUserIdObj = new JSONObject();
            JSONObject jsonServiceId = new JSONObject();
            JSONObject jsonIDType = new JSONObject();
            String id1 = Config.PREPROD_ID1;
            String id2 = Config.PREPROD_ID2;
            String number = "00000" + name;
            char[] chars = new char[number.length()];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = toHex(
                        fromHex(number.charAt(i)) ^
                                fromHex(id1.charAt(i)));
            }
            String encode_msisdn = String.valueOf(chars);
            jsonIDType.put("value", id2 + encode_msisdn);
            jsonIDType.put("type", "100");
            jsonUserIdObj.put("id", jsonIDType);
            jsonServiceId.put("id", "101");
            jsonServiceId.put("name", "rtls");
            jsonUserIdObj.put("service", jsonServiceId);
            Log.d("USERIDJSON", jsonUserIdObj.toString());
            return jsonUserIdObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int fromHex(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        throw new IllegalArgumentException();
    }

    public static char toHex(int nybble) {
        if (nybble < 0 || nybble > 15) {
            throw new IllegalArgumentException();
        }
        return "0123456789abcdef".charAt(nybble);
    }


    public void fetchRtlsKey(String publickey) {
        JSONObject jsonMainBody = createV2UserIdObject(publickey);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, SERVER_PREPROD_API_KEY_URL, jsonMainBody, response -> {
            try {
                Toast.makeText(getApplicationContext(), "RTLS TOKEN FETCHED Successfully!!!! ", Toast.LENGTH_SHORT).show();
                if (response.has("data")) {
                    JSONObject dataObject = response.getJSONObject("data");
                    if (dataObject.has("apikey")) {
                        String m_api_key = dataObject.get("apikey").toString();
                        JiotUtils.jiotwriteRtlsToken(getApplicationContext(), m_api_key);
                        Log.d("RTLSTOKEN", m_api_key);
                        JiotUtils.isTokenExpired = false;
                        SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
                        editor.putString("fetch_rtls_key", "success");
                        editor.commit();
                        Intent i = new Intent(JiotUserName.this, JiotMainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        finish();
                    }
                }
            } catch (Exception e) {
                Log.d("RTLSTOKEN", "EXCEPTION");
                e.printStackTrace();
            }
        }, error -> {
            String errorMsg = JiotUtils.getVolleyError(error);
            Log.e("RTLSTOKEN", "FAILURE " + errorMsg);
            JiotUtils.isTokenExpired = true;
            Toast.makeText(getApplicationContext(), "RTLS TOKEN FETCH Failed!!!! ", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(req);
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionAlreadyGrantedInMain() == false) {
                Log.d("ENTERPERM", "checkLocationPermission");
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public boolean isPermissionAlreadyGrantedInMain() {
        Log.d("ENTERP", "isPermissionAlreadyGrantedInMain");
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int backgroundLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        if ((coarseLocation != PackageManager.PERMISSION_GRANTED || fineLocation != PackageManager.PERMISSION_GRANTED)
                && (backgroundLocation != PackageManager.PERMISSION_GRANTED)) {
            return false;
        } else {
            return true;
        }
    }

    public void showGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.create());
        builder.setNeedBle(true);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(JiotUserName.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                        int counter = sharedPreferences.getInt("counter", 2);
                        if (counter == 0) {
                            Toast.makeText(this, "You have consumed all the attempts, now you have to manually give permission.", Toast.LENGTH_LONG).show();
                            finishAffinity();
                        } else if (counter == 1) {
                            Toast.makeText(this, "Please accept the location permission, last attempt remaining", Toast.LENGTH_LONG).show();
                            finishAffinity();
                        } else if (counter == -1) {
                            showDialogue();
                        }
                        return;
                    } else if (result == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= 29) {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                Log.d("JiotMainActivity", "Location backgroung permission granted");
                            } else {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                                } else {
                                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                                }
                            }
                        }
                    }
                }
            }
            case BACKGROUND_LOCATION_ACCESS_REQUEST_CODE: {
                if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("JiotMainActivity", "Backgroung location permission granted");
                        /*stopService();
                        bindService(JiotUserName.this);*/
                    } else {
                        Log.d("JiotMainActivity", "Backgroung location permission not granted");
                    }
                }
            }
        }
    }

    /*public void stopService() {
        if (isMyServiceRunning(LocationFetchService.class, this)) {
            Intent myService = new Intent(this, LocationFetchService.class);
            stopService(myService);
        }
    }

    public static void bindService(Context context) {
        Log.d("JLS", "service status=" + isMyServiceRunning(LocationFetchService.class, context));
        if (!isMyServiceRunning(LocationFetchService.class, context)) {
            Log.d("JLS", "Service  started");
            Intent startIntent = new Intent(context, LocationFetchService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, startIntent);
            } else {
                context.startService(startIntent);
            }
        }
    }*/

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission alert");
        builder.setMessage("Please enable the location permission all the time from setting");
        builder.setPositiveButton("OK",
                (dialog, which) -> {
                    final Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                });
        builder.show();
    }

}
