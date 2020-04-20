package com.example.nutapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nutspace.nut.api.BleDeviceConsumer;
import com.nutspace.nut.api.BleDeviceManager;
import com.nutspace.nut.api.callback.ScanResultCallback;
import com.nutspace.nut.api.model.BleDevice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    boolean m_isLocOn=false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case JioUtils.REQUEST_CHECK_SETTINGS_MAIN:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        m_isLocOn=true;
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        m_isLocOn=false;
                        break;
                    default:
                        m_isLocOn=false;
                        break;
                }
                displayLocationToast(m_isLocOn);

        }
    }

    public void displayLocationToast(boolean isLocOn){
        Intent intent2 = new Intent();
        intent2.setAction("com.nutapp.notifications_maps_home_key");
        sendBroadcast(intent2);
        if(isLocOn) {
            Toast.makeText(getApplicationContext(), "Location Turned On", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "Location Turned Off", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
/*        finishAffinity();
        finish();*/
    moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.nav_header_center);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
