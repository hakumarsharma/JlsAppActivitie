package com.jio.rtlsappfull.internal;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.config.Config;
import com.jio.rtlsappfull.log.JiotSdkFileLogger;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import static com.jio.rtlsappfull.config.Config.LOCATION_PREPROD_URL;
import static com.jio.rtlsappfull.config.Config.SERVER_PREPROD_API_KEY_URL;
import static com.jio.rtlsappfull.config.Config.SUBMIT_API_URL_PRE_PROD;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LocationFetchService extends Service {

    private static final String CHANNEL_ID = "111";
    private String m_api_key;
    public static JiotSdkFileLogger m_jiotSdkFileLoggerInstance = null;
    Handler handler = new Handler();
    private String id1;
    private String id2;
    private static ScheduledExecutorService scheduler;
    private static final int PERMISSION_REQUEST_CODE = 100;

    BroadcastReceiver mTokenRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.rtls.token_error")) {
                handler.post(() -> fetchRtlsKey(JiotUtils.getMobileNumber(context)));
            }
        }
    };

    private LocationCallback locationCallback = new LocationCallback() {
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                JiotUtils.sLang = latitude;
                JiotUtils.slon = longitude;
            }
        }
    };

    public JSONObject createV2UserIdObject(String name) {
        try {
            JSONObject jsonUserIdObj = new JSONObject();
            JSONObject jsonServiceId = new JSONObject();
            JSONObject jsonIDType = new JSONObject();
            id1 = Config.PREPROD_ID1;
            id2 = Config.PREPROD_ID2;

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
        if (JiotUtils.jiotisLocationEnabled(this) == true && JiotUtils.jiotisNetworkEnabled(this)) {
            JSONObject jsonMainBody = createV2UserIdObject(publickey);
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, SERVER_PREPROD_API_KEY_URL, jsonMainBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
//                        Toast.makeText(getApplicationContext(), "RTLS TOKEN FETCHED Successfully!!!! ", Toast.LENGTH_SHORT).show();
                        if (response.has("data")) {
                            JSONObject dataObject = response.getJSONObject("data");
                            if (dataObject.has("apikey")) {
                                m_api_key = dataObject.get("apikey").toString();
                                JiotUtils.jiotwriteRtlsToken(getApplicationContext(), m_api_key);
                                Log.d("RTLSTOKEN", m_api_key);
                                JiotUtils.isTokenExpired = false;
                                new JiotFetchCustomLatLng(getApplicationContext(), LOCATION_PREPROD_URL);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("RTLSTOKEN", "EXCEPTION");
                        e.printStackTrace();
                    }
                }
            }, error -> {
                String errorMsg = JiotUtils.getVolleyError(error);
                Log.e("RTLSTOKEN", "FAILURE " + errorMsg);
                JiotUtils.isTokenExpired = true;
                Toast.makeText(getApplicationContext(), "RTLS TOKEN FETCH Failed!!!! ", Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            queue.add(req);
        } else
            Toast.makeText(getApplicationContext(), "App requires location to be enabled and internet to be enabled", Toast.LENGTH_SHORT).show();
    }

    public LocationFetchService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            showNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mTokenRefreshReceiver);
        } catch (Exception e) {
            Log.e("LocationFetch Service", "Receiver error");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("JLS", "Location fetch service started");
        IntentFilter filter = new IntentFilter("com.rtls.token_error");
        registerReceiver(mTokenRefreshReceiver, filter);
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            executeTask();
        } else {
            scheduler.shutdownNow();
            try {
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
            scheduler = Executors.newSingleThreadScheduledExecutor();
            executeTask();
        }
        return START_STICKY;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        Intent notificationIntent = new Intent(this, JiotMainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        String NOTIFICATION_CHANNEL_ID = getApplicationContext().getPackageName();
        String channelName = getApplicationContext().getPackageName();
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("JioLocate is running")
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();
        makeLocationRequest();
        startForeground(2, notification);
    }

    private int getNotificationIcon() {
//        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return R.drawable.splash_icon;
    }

    private void showNotification() {
        Log.i("JLS", "Received Start Foreground Intent ");
        Intent notificationIntent = new Intent(getApplicationContext(), JiotMainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("JioLocate is running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        makeLocationRequest();
        startForeground(101, builder.build());
    }

    private void executeTask() {
        scheduler.scheduleAtFixedRate
                (() -> {
                    fetchServerLocation();
                }, 0, 5, TimeUnit.MINUTES);
    }

    public void fetchServerLocation() {
        m_jiotSdkFileLoggerInstance = JiotSdkFileLogger.JiotGetFileLoggerInstance(getApplicationContext());
        new JiotFetchCustomLatLng(getApplicationContext(), LOCATION_PREPROD_URL);
    }

    private void makeLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
}
