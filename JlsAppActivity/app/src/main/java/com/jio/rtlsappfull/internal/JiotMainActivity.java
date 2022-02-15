package com.jio.rtlsappfull.internal;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.ui.main.SectionsPagerAdapter;
import com.jio.rtlsappfull.utils.JiotUtils;
import com.jio.rtlsappfull.worker.JioLocateWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


@RequiresApi(api = Build.VERSION_CODES.R)
public class JiotMainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 110;
    public static final int REQUEST_CHECK_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleTask();
        turnOffDozeMode();
        if (!JiotUtils.jiotisLocationEnabled(this)) {
            showGPS();
        } else {
            checkLocationPermission();
        }
    }

    private void scheduleTask() {
        Constraints constraints = new Constraints.Builder().setRequiresDeviceIdle(false).setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(false).build();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(JioLocateWorker.class, 15, TimeUnit.MINUTES).setConstraints(constraints).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("JioLocate", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionAlreadyGrantedInMain())
            setupView();
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private LocationCallback locationCallback = new LocationCallback() {
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                JiotUtils.sLang = locationResult.getLastLocation().getLatitude();
                JiotUtils.slon = locationResult.getLastLocation().getLongitude();
                Intent intent = new Intent();
                intent.setAction("com.rtls.google_location");
                sendBroadcast(intent);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroyMain", "onDestroy");
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionAlreadyGrantedInMain() == false) {
                Log.d("ENTERPERM", "checkLocationPermission");
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public void turnOffDozeMode(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(packageName))
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
            startActivity(intent);
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

    PendingIntent m_workPendingIntent = null;
    AlarmManager m_alarmManager = null;

    public void startFetchAlarmMain() {
        Intent intent = new Intent(this, JiotFetchReceiver.class);
        m_workPendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT); //FLAG_CANCEL_CURRENT seems to be required to prevent a bug where the intent doesn't fire after app reinstall in KitKat
        m_alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        m_alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + JiotUtils.FLOAT_REFRESH_100MILI, m_workPendingIntent);
    }

    public void setupView() {
        if (JiotUtils.jiotisLocationEnabled(this) == true && JiotUtils.jiotisNetworkEnabled(this)) {
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabs = findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
            for (int i = 0; i < tabs.getTabCount(); i++) {
                if (i == 0) {
                    tabs.getTabAt(i).setIcon(R.drawable.cell_info);
                } else if (i == 1) {
                    tabs.getTabAt(i).setIcon(R.drawable.cell_map);
                } else if (i == 2) {
                    tabs.getTabAt(i).setIcon(R.drawable.cell_map);
                } else {
                    tabs.getTabAt(i).setIcon(R.drawable.cell_acc);
                }
            }
            Button m_refresh = findViewById(R.id.refresh_maps);
            m_refresh.setOnClickListener(v -> startFetchAlarmMain());
            FloatingActionButton m_refresh_float = findViewById(R.id.float_refresh);
            m_refresh_float.setOnClickListener(v -> {
                startFetchAlarmMain();
                JiotUtils.isRefreshed = true;
            });
        } else {
            showGPS();
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
                            resolvable.startResolutionForResult(JiotMainActivity.this, REQUEST_CHECK_SETTINGS);
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
}