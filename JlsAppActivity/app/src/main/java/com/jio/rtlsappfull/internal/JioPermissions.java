package com.jio.rtlsappfull.internal;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.utils.JiotUtils;

@RequiresApi(api = Build.VERSION_CODES.M)
public class JioPermissions extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public BluetoothAdapter m_bluetoothAdpater;
    public static final int REQUEST_CHECK_SETTINGS = 1;
    private Activity m_currentAct;
    private LocationRequest mLocationRequestHighAccuracy;
    public MaterialAlertDialogBuilder m_alertDialog = null;
    private String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private AlertDialog settingAlertDialog;
    private EditText numberMarkerEdittext;

    public void createAlertDialog() {
        m_alertDialog = new MaterialAlertDialogBuilder(JioPermissions.this, R.style.AlertDialogTheme)
                .setTitle(getResources().getString(R.string.jiousername_net_alert))
                .setMessage(getResources().getString(R.string.jiopermissions_main_block))
                .setPositiveButton(getResources().getString(R.string.jiousername_ok), (dialog, which) -> dialog.dismiss());
    }

    public void showAlertDialog() {
        m_alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (m_bluetoothReceiver != null) {
                Log.d("PERMISSION", "UNREGISTER BLUETOOTH");
                unregisterReceiver(m_bluetoothReceiver);
            }
        } catch (Exception e) {
            Log.d("PERMISSION", "NOT REGISTRED BLUETOOTH");
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
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                if (!JiotUtils.jiotisLocationEnabled(this)) {
                    ShowGPS();
                }
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])) {
                openGrantDialog();
            } else {
                openSettingsDialog();
//                Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openSettingsDialog() {
        final AlertDialog.Builder alertDialog = new
                AlertDialog.Builder(this);
        alertDialog.setTitle("Permission Needed");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Please enable Location permission");
        alertDialog.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
//                sentToSettings = true;
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            }
        });
        alertDialog.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.cancel();
            finish();
        });
        settingAlertDialog = alertDialog.show();
    }

    private void openGrantDialog() {
        //Show Information about why you need the permission
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Needs Permission");
        builder.setMessage("Please enable Location Permission");
        builder.setCancelable(false);
        builder.setPositiveButton("GRANT", (dialog, which) -> {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(JioPermissions.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                }
        );
        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.cancel();
            finish();
        });
        builder.show();
    }

    public void enableBluetooth() {
        if (m_bluetoothAdpater.isEnabled() == false) {
            Intent btIntent = new Intent(m_bluetoothAdpater.ACTION_REQUEST_ENABLE);
            startActivity(btIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already enabled", Toast.LENGTH_LONG).show();
        }
    }


    public void ShowGPS() {
        Log.d("SHOWGPS", "GPS");
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
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(
                                        m_currentAct,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            } catch (ClassCastException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "Location Turned On", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "Location Turned Off", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
            case REQUEST_PERMISSION_SETTING:
                if (checkSelfPermission(permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    if (settingAlertDialog != null)
                        settingAlertDialog.dismiss();
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jio_permissions);
        m_bluetoothAdpater = BluetoothAdapter.getDefaultAdapter();
        mLocationRequestHighAccuracy = LocationRequest.create();
        IntentFilter intFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        this.registerReceiver(m_bluetoothReceiver, intFilter);
        Spinner spinner = findViewById(R.id.server_spinner);
        Spinner errorDetailSpinner = findViewById(R.id.error_detail_spinner);
        Spinner fetchTimeIntervalSpinner = findViewById(R.id.fetch_time_spinner);
        spinner.setOnItemSelectedListener(this);
        errorDetailSpinner.setOnItemSelectedListener(this);
        fetchTimeIntervalSpinner.setOnItemSelectedListener(this);
        Spinner enableDisableSpinner = findViewById(R.id.enable_disable_spinner);
        ArrayAdapter<CharSequence> enableDisableAdapter = ArrayAdapter.createFromResource(this,
                R.array.error_details_concent, android.R.layout.simple_spinner_item);
        enableDisableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        enableDisableSpinner.setAdapter(enableDisableAdapter);
        enableDisableSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.server_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        ArrayAdapter<CharSequence> errorDetailAadapter = ArrayAdapter.createFromResource(this,
                R.array.error_details_concent, android.R.layout.simple_spinner_item);
        errorDetailAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        errorDetailSpinner.setAdapter(errorDetailAadapter);
        ArrayAdapter<CharSequence> fetchTimeAadapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        fetchTimeAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fetchTimeIntervalSpinner.setAdapter(fetchTimeAadapter);
        TextView jiopermissions_app_header_text = findViewById(R.id.jiopermissions_app_header_text);
        TextView jiopermissions_before_activate = findViewById(R.id.jiopermissions_before_activate);
        TextView jiopermissions_turnon_bt = findViewById(R.id.jiopermissions_turnon_bt);
        TextView jiopermissions_turnon_bt_details = findViewById(R.id.jiopermissions_turnon_bt_details);
        TextView jiopermissions_turnon_bt_details_enable = findViewById(R.id.jiopermissions_turnon_bt_details_enable);
        TextView jiopermissions_turnon_location = findViewById(R.id.jiopermissions_turnon_location);
        TextView jiopermissions_turnon_location_details = findViewById(R.id.jiopermissions_turnon_location_details);
        TextView jiopermissions_turnon_location_details_enable = findViewById(R.id.jiopermissions_turnon_location_details_enable);
        TextView jiopermissions_turnon_network_details = findViewById(R.id.jiopermissions_turnon_network_details);
        TextView jiopermissions_turnon_network = findViewById(R.id.jiopermissions_turnon_network);
        numberMarkerEdittext = findViewById(R.id.numberMarkerEdittext);

        Button btn_next = findViewById(R.id.jiopermissions_next);
        btn_next.setOnClickListener(v -> {
            if ((JiotUtils.jiotisLocationEnabled(v.getContext()) == false)
                    || (JiotUtils.jiotisNetworkEnabled(v.getContext()) == false)) {
                showAlertDialog();
            } else {
                String api_key = JiotUtils.jiotgetRtlsToken(getApplicationContext());
                Intent intent;
                if (api_key.equalsIgnoreCase("none")) {
                    intent = new Intent(getApplicationContext(), JiotUserName.class);
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
                    if (!numberMarkerEdittext.getText().toString().equalsIgnoreCase("")) {
                        int markerno = Integer.parseInt(numberMarkerEdittext.getText().toString());
                        if (markerno >= 1 && markerno <= 500) {
                            editor.putInt("marker_number", markerno);
                            editor.commit();
                        } else {
                            Toast.makeText(this, "Please enter marker number between 1 to 500", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    intent = new Intent(getApplicationContext(), JiotMainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
        btn_next.setTypeface(JiotUtils.mTypeface(this, 5));
        jiopermissions_app_header_text.setTypeface(JiotUtils.mTypeface(this, 5));
        jiopermissions_before_activate.setTypeface(JiotUtils.mTypeface(this, 5));
        jiopermissions_turnon_bt.setTypeface(JiotUtils.mTypeface(this, 5));
        jiopermissions_turnon_bt_details.setTypeface(JiotUtils.mTypeface(this, 3));
        jiopermissions_turnon_bt_details_enable.setTypeface(JiotUtils.mTypeface(this, 2));
        jiopermissions_turnon_location.setTypeface(JiotUtils.mTypeface(this, 5));
        jiopermissions_turnon_location_details.setTypeface(JiotUtils.mTypeface(this, 3));
        jiopermissions_turnon_location_details_enable.setTypeface(JiotUtils.mTypeface(this, 2));
        jiopermissions_turnon_network_details.setTypeface(JiotUtils.mTypeface(this, 3));
        jiopermissions_turnon_network.setTypeface(JiotUtils.mTypeface(this, 5));
        TextView serverDetail = findViewById(R.id.server_detail);
        serverDetail.setTypeface(JiotUtils.mTypeface(this, 5));
        TextView errorDetailLogs = findViewById(R.id.error_detail_logs);
        errorDetailLogs.setTypeface(JiotUtils.mTypeface(this, 5));
        jiopermissions_turnon_bt_details_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBluetooth();
            }
        });
        jiopermissions_turnon_location_details_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    /*Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);*/
                    //ShowGPS();
                } else {
                    if (JiotUtils.jiotisLocationEnabled(JioPermissions.this)) {
                        Toast.makeText(getApplicationContext(), "Location is already enabled", Toast.LENGTH_LONG).show();
                    } else {
                        ShowGPS();
                    }
                }
            }
        });
        createAlertDialog();
        m_currentAct = this;
        SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
        editor.putString("server_name", "Prod");
        editor.putString("error_consent", "No");
        editor.putString("fetch_interval", "5 min");
        editor.putInt("marker_number", 1);
        editor.putString("enable_disable_submit","No");
        editor.commit();
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            boolean hasForegroundPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (hasForegroundPermission) {
                boolean hasBackgroundPermission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        //Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
                if (hasBackgroundPermission) {
                    return true;
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_CALLBACK_CONSTANT);
                    //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CALLBACK_CONSTANT);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_CALLBACK_CONSTANT);
                    //new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CALLBACK_CONSTANT);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CALLBACK_CONSTANT);
                }
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.server_spinner:
                SharedPreferences.Editor serverEditor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
                serverEditor.putString("server_name", parent.getItemAtPosition(position).toString());
                serverEditor.commit();
                break;
            case R.id.error_detail_spinner:
                SharedPreferences.Editor errorEditor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
                errorEditor.putString("error_consent", parent.getItemAtPosition(position).toString());
                errorEditor.commit();
                break;
            case R.id.fetch_time_spinner:
                SharedPreferences.Editor fetchTimeEditor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
                fetchTimeEditor.putString("fetch_interval", parent.getItemAtPosition(position).toString());
                fetchTimeEditor.commit();
                break;
            case R.id.enable_disable_spinner:
                SharedPreferences.Editor fetchEnableDisable = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
                fetchEnableDisable.putString("enable_disable_submit", parent.getItemAtPosition(position).toString());
                fetchEnableDisable.commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Todo
    }
}
