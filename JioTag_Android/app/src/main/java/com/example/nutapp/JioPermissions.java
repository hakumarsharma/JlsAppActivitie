package com.example.nutapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class JioPermissions extends AppCompatActivity {

    public BluetoothAdapter m_bluetoothAdpater;
    public static final int REQUEST_CHECK_SETTINGS=1;
    Activity m_currentAct;
    LocationRequest mLocationRequestHighAccuracy;

    public static boolean isLocationEnabled(Context context) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (m_bluetoothReceiver != null) {
                Log.d("PERMISSION", "UNREGISTER BLUETOOTH");
                unregisterReceiver(m_bluetoothReceiver);
            }
        }catch(Exception e){
            Log.d("PERMISSION","NOT REGISTRED BLUETOOTH");
        }
    }

    public BroadcastReceiver m_bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BLUETOOTHBLE", intent.getAction().toString());
            if (intent.getAction().equalsIgnoreCase("android.bluetooth.adapter.action.STATE_CHANGED")) {
                Log.d("BLUETOOTHBLE", "Bluetooth status changed");
                switch (m_bluetoothAdpater.getState()) {
                    case BluetoothAdapter.STATE_ON:
                        Log.d("BTCONNECTIONCALLBACK", "BT CONNECTED");
                        Toast.makeText(getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("BTCONNECTIONSTATUS", "BT DISCONNECTED");
                        Toast.makeText(getApplicationContext(), "Bluetooth is disabled", Toast.LENGTH_LONG).show();
                        break;

                    default:
                        break;
                }
            }
        }
    };


    public void enableBluetooth() {
        if (m_bluetoothAdpater.isEnabled() == false) {
            Intent btIntent = new Intent(m_bluetoothAdpater.ACTION_REQUEST_ENABLE);
            startActivity(btIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already enabled", Toast.LENGTH_LONG).show();
        }
    }


    public void showGPS(){
        Log.d("SHOWGPS","GPS");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy);
        builder.setNeedBle(true);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Log.d("JioPermission","response value=="+response);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        m_currentAct,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                Log.d("JioPermission","Error value=="+e);
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                Log.d("JioPermission","Error value=="+e);
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;

                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Toast.makeText(getApplicationContext(),"Location Turned On",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(getApplicationContext(),"Location Turned Off",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;

            default :
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jio_permissions);

        m_bluetoothAdpater = BluetoothAdapter.getDefaultAdapter();
        mLocationRequestHighAccuracy=LocationRequest.create();
        IntentFilter intFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        this.registerReceiver(m_bluetoothReceiver, intFilter);

        TextView jiopermissionsAppHeaderText = (TextView) findViewById(R.id.jiopermissions_app_header_text);
        TextView jiopermissionsBeforeActivate = (TextView) findViewById(R.id.jiopermissions_before_activate);
        TextView jiopermissionsTurnonBt = (TextView) findViewById(R.id.jiopermissions_turnon_bt);
        TextView jiopermissionsTurnonBtDetails = (TextView) findViewById(R.id.jiopermissions_turnon_bt_details);
        TextView jiopermissionsTurnonBtDetailsEnable = (TextView) findViewById(R.id.jiopermissions_turnon_bt_details_enable);
        TextView jiopermissionsTurnonLocation = (TextView) findViewById(R.id.jiopermissions_turnon_location);
        TextView jiopermissionsTurnonLocationDetails = (TextView) findViewById(R.id.jiopermissions_turnon_location_details);
        TextView jiopermissionsTurnonLocationDetailsEnable = (TextView) findViewById(R.id.jiopermissions_turnon_location_details_enable);


        Button btnNext = (Button) findViewById(R.id.jiopermissions_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocationEnabled(v.getContext()) == false || m_bluetoothAdpater.isEnabled() == false){
                    Toast.makeText(getApplicationContext(), "Location/Bluetooth is not enabled pls click enable to proceed!!", Toast.LENGTH_LONG).show();
                }else {
                    Intent startMain = new Intent(getApplicationContext(), JioAttachFinder.class);
                    startActivity(startMain);
                }
            }
        });
        btnNext.setTypeface(JioUtils.mTypeface(this, 5));
        jiopermissionsAppHeaderText.setTypeface(JioUtils.mTypeface(this, 5));
        jiopermissionsBeforeActivate.setTypeface(JioUtils.mTypeface(this, 5));
        jiopermissionsTurnonBt.setTypeface(JioUtils.mTypeface(this, 5));
        jiopermissionsTurnonBtDetails.setTypeface(JioUtils.mTypeface(this, 3));
        jiopermissionsTurnonBtDetailsEnable.setTypeface(JioUtils.mTypeface(this, 2));
        jiopermissionsTurnonLocation.setTypeface(JioUtils.mTypeface(this, 5));
        jiopermissionsTurnonLocationDetails.setTypeface(JioUtils.mTypeface(this, 3));
        jiopermissionsTurnonLocationDetailsEnable.setTypeface(JioUtils.mTypeface(this, 2));

        jiopermissionsTurnonBtDetailsEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBluetooth();
            }
        });
        jiopermissionsTurnonLocationDetailsEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocationEnabled(v.getContext()) == false) {
                    /*Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);*/
                    showGPS();
                }else{
                    Toast.makeText(getApplicationContext(), "Location is already enabled", Toast.LENGTH_LONG).show();
                }
            }
        });
        m_currentAct=this;
    }
}
