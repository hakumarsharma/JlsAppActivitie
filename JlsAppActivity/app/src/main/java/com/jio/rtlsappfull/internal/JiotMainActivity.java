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
import com.google.android.gms.location.LocationRequest;
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

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.ui.main.SectionsPagerAdapter;
import com.jio.rtlsappfull.utils.JiotUtils;
import java.util.Calendar;


@RequiresApi(api = Build.VERSION_CODES.R)
public class JiotMainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 110;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1000;
    public static final int REQUEST_CHECK_SETTINGS = 1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroyMain", "onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!JiotUtils.jiotisLocationEnabled(this)) {
            showGPS();
        } else {
            checkLocationPermission();
            SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
            int counter = sharedPreferences.getInt("counter", 2);
            if (counter != -1) {
                counter--;
                editor.putInt("counter", counter);
                editor.commit();
            }
        }
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
                if (isGpsEnabled || isNetworkEnabled) {
                    checkLocationPermission();
                    SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
                    int counter = sharedPreferences.getInt("counter", 2);
                    if (counter != -1) {
                        counter--;
                        editor.putInt("counter", counter);
                        editor.commit();
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionAlreadyGrantedInMain())
            setupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.WiFiProfile:
                startActivity(new Intent(this, WifiProfileActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionAlreadyGrantedInMain() == false) {
                Log.d("ENTERPERM", "checkLocationPermission");
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
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
                        stopService();
                        bindService(this);
                    } else {
                        Log.d("JiotMainActivity", "Backgroung location permission not granted");
                    }
                }
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
    }

    public void stopService() {
        if (isMyServiceRunning(LocationFetchService.class, this)) {
            Intent myService = new Intent(this, LocationFetchService.class);
            stopService(myService);
        }
    }

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