package com.example.nutapp.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraDevice;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutapp.BleDetails;
import com.example.nutapp.BleDetailsAdapter;
import com.example.nutapp.CameraActivity;
import com.example.nutapp.CardDetails;
import com.example.nutapp.JioFinderNotFound;
import com.example.nutapp.JioUtils;
import com.example.nutapp.MainActivity;
import com.example.nutapp.MapsActivity;
import com.example.nutapp.R;
import com.example.nutapp.SettingsActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nutspace.nut.api.BleDeviceConsumer;
import com.nutspace.nut.api.BleDeviceManager;
import com.nutspace.nut.api.callback.ConnectStateChangedCallback;
import com.nutspace.nut.api.callback.EventCallback;
import com.nutspace.nut.api.callback.ScanResultCallback;
import com.nutspace.nut.api.model.BleDevice;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

//Nut related Imports

public class HomeFragment extends Fragment implements BleDeviceConsumer, ScanResultCallback, ConnectStateChangedCallback, EventCallback {

    View root;
    public static boolean m_addr_pending = false;
    public static String ret_addr = "Location Not Known";
    AppCompatDialog m_animationWaitDialog;
    boolean m_serviceBound = false;
    public ProgressDialog dialogp;
    boolean m_waitingForScan = true;
    BleDeviceConsumer m_devCon;
    ScanResultCallback m_ScanCon;
    ConnectStateChangedCallback m_connStateCall;
    EventCallback m_EventCallBack;


    private String mCurrentPhotoPath;
    boolean m_isReceiverRegistered = false;

    public BluetoothAdapter m_bluetoothAdpater = BluetoothAdapter.getDefaultAdapter();
    public BluetoothLeScanner m_leScanner;


    public ScanSettings m_scanSettings;
    public List<ScanFilter> m_sacnFilter = new ArrayList<ScanFilter>();

    public ArrayList<String> m_deviceAddress = new ArrayList<String>();
    public ArrayList<String> m_btDeviceNames = new ArrayList<String>();
    public ArrayList<String> m_btDeviceRssi = new ArrayList<String>();
    public ArrayList<Double> m_btDeviceDistance = new ArrayList<Double>();
    public List<String> m_deviceTags = new ArrayList<String>();

    List<BleDetails> m_bleDetails = new ArrayList<BleDetails>();
    BleDetailsAdapter m_bleDetailsAdapter;
    int m_itemPos = 0;
    boolean m_gattServiceDiscovered = false;
    BluetoothGatt m_globalGatt;
    int CAMERA_REQUEST_CODE = 1;
    RecyclerView recyclerView;
    Handler m_handler;
    boolean m_sacanPending = false;
    boolean m_onBackKey = false;
    private Bitmap mImageBitmap;
    AlertDialog.Builder builder;
    AlertDialog m_disconnectAlert;


    //Maps
    FusedLocationProviderClient m_fusedLocationProviderClient;

    //NUT related Callbacks
    BleDeviceManager mManager;
    ArrayList<BleDevice> mBleDeviceList = new ArrayList<>();

    //ALert
    UUID ITAG_ALARM_SERVICE_UUID = convertFromInteger(0x1802);
    UUID ITAG_BEEP_UUID = convertFromInteger(0x2A06);


    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Context m_sharedPrefContext;
    public Set<String> m_auto_reconnect_list= new HashSet<String>();
    LocationRequest mLocationRequestHighAccuracy;

    public void ShowGPS_Status(){
        Log.d("SHOWGPS","GPS");
        //Toast.makeText(getApplicationContext(),"LOCATION IS TURNED OFF PLEASE TURN ON",Toast.LENGTH_SHORT).show();
        mLocationRequestHighAccuracy= LocationRequest.create();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy);
        builder.setNeedBle(true);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());
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
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        getActivity(),
                                        JioUtils.REQUEST_CHECK_SETTINGS_MAIN);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
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



    private BroadcastReceiver m_locationStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_location_turn_on")) {
                    ShowGPS_Status();
                    return;
                }

            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGpsEnabled || isNetworkEnabled) {
                    // Handle Location turned ON
                    Log.d("CONNECTIONSTATUS","LOCATION ON");
                } else {
                    // Handle Location turned OFF
                    Log.d("CONNECTIONSTATUS", "LOCATION OFF");
                    Intent i = new Intent(context, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    Intent btIntent = new Intent();
                    btIntent.setAction("com.nutapp.notifications_location_turn_on");
                    getContext().sendBroadcast(btIntent);
                }
            }
        }
    };


    public boolean isPermissionAlreadyGrantedInMain() {
        Log.d("ENTERP", "isPermissionAlreadyGrantedInMain");
        int send_sms = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);
        int fine_location = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int write_external_storage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_external_storage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int coarse_location = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int read_phone_state = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE);
        int read_phone_numbers = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_NUMBERS);
        int read_sms = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);
        int receive_sms = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS);
        if ((send_sms != PackageManager.PERMISSION_GRANTED) || (fine_location != PackageManager.PERMISSION_GRANTED) || (camera != PackageManager.PERMISSION_GRANTED) ||
                (write_external_storage != PackageManager.PERMISSION_GRANTED) || (read_external_storage != PackageManager.PERMISSION_GRANTED) ||
                (coarse_location != PackageManager.PERMISSION_GRANTED) || (read_phone_state != PackageManager.PERMISSION_GRANTED) ||
                (read_phone_numbers != PackageManager.PERMISSION_GRANTED) || (read_sms != PackageManager.PERMISSION_GRANTED) ||
                (receive_sms != PackageManager.PERMISSION_GRANTED)) {
            return false;
        } else {
            return true;
        }

    }


    public void createSharedPreferences() {
        m_sharedPrefContext = getContext();
        preferences = getContext().getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        Log.d("PHOTO_CPATURE_PREF", "FIRST TIME SETTING TRUE");
        if (preferences.contains(JioUtils.MYPREFERENCES_PHOTO_CPATURE) != true) {
            prefEditor.putBoolean(JioUtils.MYPREFERENCES_PHOTO_CPATURE, true);
            prefEditor.commit();
        }
        if (preferences.contains(JioUtils.MYPREFERENCES_DISCONNECTION_ALERT) != true) {
            prefEditor.putBoolean(JioUtils.MYPREFERENCES_DISCONNECTION_ALERT, true);
            prefEditor.commit();
        }
        if (preferences.contains(JioUtils.MYPREFERENCES_DEVICE_ALERTS) != true) {
            Log.d("DEVICE_ALERTS", "first time true");
            prefEditor.putBoolean(JioUtils.MYPREFERENCES_DEVICE_ALERTS, true);
            prefEditor.commit();
        }
        if (preferences.contains(JioUtils.MYPREFERENCES_PHONE_ALERTS) != true) {
            Log.d("PHONE_ALERTS", "first time true");
            prefEditor.putBoolean(JioUtils.MYPREFERENCES_PHONE_ALERTS, true);
            prefEditor.commit();
        }
        Log.d("CAPTURE", preferences.getBoolean(JioUtils.MYPREFERENCES_DISCONNECTION_ALERT, false) + "");
        Log.d("CAPTURE", preferences.getBoolean(JioUtils.MYPREFERENCES_PHOTO_CPATURE, false) + "");
    }


    ///////////////////////////////////////////////////////////

    public BroadcastReceiver m_nutAppNotificationReceiver = new BroadcastReceiver() {
        int position_nut = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("NUTRECEIVER", intent.getAction().toString());
            // Toast.makeText(getContext(), "NUTRECEIVER AT POS: " + intent.getAction().toString(), Toast.LENGTH_SHORT).show();
            if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_alarm_start")) {
                Log.d("NUTRECEIVER", "Start Alarm");
                position_nut = intent.getIntExtra("POSITION_NUT", 0);
                String DEVICE_ADDRESS = intent.getStringExtra("DEVICE_ADDRESS");
                startAlarm(position_nut, DEVICE_ADDRESS);
            } else if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_disconnect")) {
                Log.d("NUTRECEIVER", "DISCONNECT");
                position_nut = intent.getIntExtra("POSITION_NUT", 0);
                startDisconnecting(position_nut);
            } else if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_alarm_stop")) {
                Log.d("NUTRECEIVER", "ALARM STOP");
                position_nut = intent.getIntExtra("POSITION_NUT", 0);
                String DEVICE_ADDRESS = intent.getStringExtra("DEVICE_ADDRESS");
                stopAlarm(position_nut, DEVICE_ADDRESS);
            } else if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_connect")) {
                Log.d("NUTRECEIVER", "CONNECT");
                position_nut = intent.getIntExtra("POSITION_NUT", 0);
                String devaddress = intent.getStringExtra("DEV_ADDRESS");
                startConnecting(position_nut, devaddress);
            } else if (intent.getAction().equalsIgnoreCase(("com.nutapp.notifications_showlocation"))) {
                Log.d("NUTRECEIVER", "SHOWLOCATION");
                readLocationDetails();
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DESTROY","ondestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("DETACH", "ONDETACH");
        try {
            if (m_btReceiver != null) {
                Log.d("UNREG", "UNREGISTER BTRECV");
                getContext().unregisterReceiver(m_btReceiver);
            }
        }catch(Exception e){
            Log.d("BTRECV","NOT REGISTRED");
        }

        try {
            if (m_nutAppNotificationReceiver != null) {
                Log.d("UNREG", "UNREGISTER NUTRECV");
                getContext().unregisterReceiver(m_nutAppNotificationReceiver);
            }
        }catch(Exception e){
            Log.d("CONNRECVR","CONNECTTTT NOT REGISTRED");
        }
        try{
            //startDisconnecting()
            mManager.removeScanResultCallback(this);
            mManager.removeEventCallback(this);
            mManager.removeConnectStateChangedCallback(this);
            mManager.unbind(this);
        }catch(Exception e){
            Log.d("NUTUNBIND","NUT UNBIND");
        }
        try{
            Log.d("UNREGLOC", "UNREGISTER LOCATION RCVR");
            getContext().unregisterReceiver(m_locationStateReceiver);
        }catch(Exception e){
            Log.d("UNREGLOC","EXCEPTION IN UNREGLOC");
        }

    }

    public void onBackKeyEntered() {
        Log.d("BACKKEY", "onBackKeyEntered REMOVE CALLBACKS");
        if (m_waitingForScan == true) {
            m_waitingForScan = false;
            mManager.stopScan();
        }
    }

    @Override
    public void onDeviceRingStateChangedEvent(BleDevice bleDevice, int i, int i1) {
        Log.d("RINGCHANGED", "RING CHANGED");
    }

    @Override
    public void onBatteryChangedEvent(BleDevice bleDevice, int i) {
        Log.d("BATTERYCHANGE", "onBatteryChangedEvent");
    }

    @Override
    public void onRssiChangedEvent(BleDevice bleDevice, int rssi) {
        Log.d("RSSICHANGE", "onRssiChangedEvent");
        int deviceListIndex = findDeviceListIndex(bleDevice.address, mBleDeviceList);
        if (deviceListIndex != -1) {
            Log.d("FOUNDDEV", bleDevice.address + " index:" + deviceListIndex + "RSSI:" + rssi);
            m_bleDetailsAdapter.setProgressBarForItem(bleDevice.address, 100 + (rssi));
            double dist = getDistanceFromRSSI(rssi);
            if (dist > 5) {
                m_bleDetailsAdapter.setDistanceOfNut(bleDevice, "Far");
            } else {
                m_bleDetailsAdapter.setDistanceOfNut(bleDevice, "Nearby");
            }
        }
    }

    public static ResolveInfo getCameraPackageName(Context context, PackageManager pm) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        ResolveInfo cameraInfo = null;

        List<ResolveInfo> pkgList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (pkgList != null && pkgList.size() > 0) {
            cameraInfo = pkgList.get(0);
        }
        return (cameraInfo);
    }

    @Override
    public void onClickEvent(BleDevice bleDevice, int action) {
        Log.d("NUTEVENT", "NUT SENT YOU AN EVENT");
        switch (action) {
            case BleDevice.ACTION_SINGLE_CLICK:
                Log.d("NUTSINGLE", "SINGLE CLICK");
                //startCameraClick();
                boolean phoneAlertSetting = JioUtils.getPhoneAlertSetting(m_sharedPrefContext, bleDevice.address);
                if (phoneAlertSetting) {
                    showJioPushNotification(bleDevice.address);
                } else {
                    Toast.makeText(m_sharedPrefContext, "PHONE ALERT SETTING DISABLED: PLEASE ENABLE IT. ", Toast.LENGTH_SHORT).show();
                }
                break;
            case BleDevice.ACTION_DOUBLECLICK:
                Log.d("NUTDOUBLE", "DOUBLE CLICK");
                //readLocationDetails();
                boolean isPhotoCaptureOn = preferences.getBoolean(JioUtils.MYPREFERENCES_PHOTO_CPATURE, false);
                if (isPhotoCaptureOn == true) {
                  /*  ResolveInfo info=getCameraPackageName(getActivity(),getActivity().getPackageManager());
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String name = takePictureIntent.resolveActivity(getActivity().getPackageManager()).getPackageName();
                    Log.d("CAMERAAA",name);*/
                    //startCamera();
                    Log.d("CAMMENU", "take photo");
                    Intent startCameraIntent = new Intent(getContext(), CameraActivity.class);
                    startActivity(startCameraIntent);
                    // Intent mailAccessabilityIntent = new Intent(getContext(), CameraAccessibilityService.class);
                    //getContext().startService(mailAccessabilityIntent);
                    //bindService(mailAccessabilityIntent, serviceConnection, Context.BIND_AUTO_CREATE);

                } else {
                    Toast.makeText(getContext(), "JioTag Device Double Clicked Turn on Photo Capture setting to take photo ", Toast.LENGTH_SHORT).show();
                }
                break;
            case BleDevice.ACTION_LONGCLICK:
                Log.d("NUTLONG", "LONG CLICK");
                break;
            default:
                break;
        }
    }

    public void showAlertOnReconnectionFromMain(String device_address) {
        Log.d("JIO", "RECONNECT ALERT");
        String m_assetName = preferences.getString(device_address + "CUSTOMNAME", " ");
        String receivedData = m_assetName + "ReConnect success Alert";
        Context context = getContext();
        Intent intent = new Intent(context, CardDetails.class);
        // Send data to NotificationView Class
        intent.putExtra("title", m_assetName + " ReConnected,successfully");
        intent.putExtra("text", receivedData);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final int notificationId = 3;
        String channelId = "channel-03";
        String channelName = "Connection";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(m_assetName + "  ReConnected successfully")
                .setSmallIcon(R.drawable.ic_action_settings_remote)
                .setAutoCancel(false)
                .setColor(0x00b359)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            nm.createNotificationChannel(mChannel);
        }
        // NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        Notification m_notif = nbuilder.build();
        nm.notify(notificationId, m_notif);
    }


    public void startDisconnecting(int pos) {
        if (mBleDeviceList.size() > pos) {
            Log.d("DISCONN", mBleDeviceList.get(pos).address);
            if(m_auto_reconnect_list.contains(mBleDeviceList.get(pos).address)) {
                m_auto_reconnect_list.remove(mBleDeviceList.get(pos).address);
                Log.d("REMOVE",mBleDeviceList.get(pos).address);
            }else{
                Log.d("NOREMOVE",mBleDeviceList.get(pos).address);
            }
            mManager.disconnect(getContext(), mBleDeviceList.get(pos));
        } else {
            Log.d("startDisconnecting", mBleDeviceList.size() + "");
        }
    }

    public void startConnecting(int pos, String devAddr) {
        if (mBleDeviceList.size() > pos) {
            int i = 0;
            for (BleDevice dev : mBleDeviceList) {
                if (devAddr.equals(dev.address)) {
                    mManager.connect(getContext(), dev, true);
                }
                i++;
            }
            //mManager.connect(getContext(), mBleDeviceList.get(pos), false);
        } else {
            Log.d("startConnecting", mBleDeviceList.size() + "");
        }
    }

    @Override
    public void onConnect(BleDevice bleDevice) {
        Log.d("CONNECTION", "onConnect DEVIIIIII:" + bleDevice.address);
        if(m_auto_reconnect_list.contains(bleDevice.address)){
            //Show Reconnected successfully Alert
            Log.d("SHOW",bleDevice.address);
            showAlertOnReconnectionFromMain(bleDevice.address);
        }else{
            m_auto_reconnect_list.add(bleDevice.address);
            Log.d("ADDING",bleDevice.address);
        }
        m_bleDetailsAdapter.setDeviceStatus(true, m_itemPos, bleDevice);
        mManager.enableAntiLost(bleDevice,true);
        //Toast.makeText(getContext(),"DEVICEIDDD"+bleDevice.id,Toast.LENGTH_SHORT).show();
        Intent inte = new Intent();
        inte.setAction("com.nutapp.notifications_connected_device");
        inte.putExtra("DEV_ADDRESS", bleDevice.address);
        getContext().sendBroadcast(inte);

    }

    @Override
    public void onDisconnect(BleDevice bleDevice, int i) {
        Log.d("CONNECTION", "onDisconnect" + bleDevice.address);
        m_bleDetailsAdapter.setDeviceStatus(false, m_itemPos, bleDevice);
        //createDisconnectDialog(bleDevice);
        Intent inte = new Intent();
        inte.setAction("com.nutapp.notifications_disconnected_device");
        inte.putExtra("DEV_ADDRESS", bleDevice.address);
        getContext().sendBroadcast(inte);
    }

    public static boolean m_alertDialogActive = false;

    public void createDisconnectDialog(BleDevice bleDevice) {
        boolean disconnectAlertVal = JioUtils.getDisconnectionAlertSetting(m_sharedPrefContext);
        if (disconnectAlertVal) {
            if (m_alertDialogActive == true) {
                m_alertDialogActive = false;
                m_disconnectAlert.dismiss();
            }
            m_alertDialogActive = true;
            builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Jio Tag" + " is disconnected from your phone").setTitle("Jio Tag")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            m_alertDialogActive = false;
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("LOCATION", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_alertDialogActive = false;
                            dialog.dismiss();
                            readLocationDetails();
                        }
                    }).setIcon(R.drawable.ic_action_settings_remote);

            m_disconnectAlert = builder.create();
            //m_disconnectAlert.setTitle(bleDevice.name + " Disconnected");
            m_disconnectAlert.setTitle("Alert");
            m_disconnectAlert.show();
            Button b_negative = m_disconnectAlert.getButton(DialogInterface.BUTTON_NEGATIVE);
            Button b_positive = m_disconnectAlert.getButton(DialogInterface.BUTTON_POSITIVE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    200,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 0, 0, 0);

            if (b_negative != null) {
                b_negative.setLayoutParams(params);
                //b_negative.setWidth(200);
                b_negative.setTextColor(Color.WHITE);
                b_negative.setBackgroundDrawable(getResources().getDrawable(R.drawable.square_blue_frame));
            }
            if (b_positive != null) {
                //b_positive.setWidth(200);
                b_positive.setLayoutParams(params);
                b_positive.setTextColor(Color.WHITE);
                b_positive.setBackgroundDrawable(getResources().getDrawable(R.drawable.square_blue_frame));
            }
        }

    }

    public int findDevice(
            String address, List<BleDetails> devices) {
        int i = 0;
        for (BleDetails device : devices) {
            if (device.getDeviceAddress().equals(address)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public int findDeviceListIndex(
            String address, List<BleDevice> devices) {
        int i = 0;
        for (BleDevice device : devices) {
            if (device.address.equals(address)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public void onBleDeviceScanned(BleDevice bleDevice) {
        Log.d("SCANRESULT", "onBleDeviceScanned");
        m_deviceAddress.add(bleDevice.address);
        m_btDeviceNames.add(bleDevice.name);
        m_btDeviceRssi.add(bleDevice.rssi + "");
        double dist = getDistanceFromRSSI(bleDevice.rssi);
        m_btDeviceDistance.add(dist);
        int deviceExists = findDevice(bleDevice.address, m_bleDetails);
        if (deviceExists != -1) {
            Log.d("REMOVE", bleDevice.address + " index:" + deviceExists);
            m_bleDetails.remove(deviceExists);
        }
        BleDetails ble = new BleDetails(bleDevice.name, bleDevice.address, bleDevice.rssi + "", R.mipmap.ic_action_settings_remote, R.mipmap.ic_action_settings_remote, dist);
        m_bleDetails.add(ble);
        int deviceListIndex = findDeviceListIndex(bleDevice.address, mBleDeviceList);
        if (deviceListIndex != -1) {
            Log.d("REMOVE", bleDevice.address + " index:" + deviceListIndex);
            mBleDeviceList.remove(deviceListIndex);
        }
        mBleDeviceList.add(bleDevice);
        m_bleDetailsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServiceBound() {
        Log.d("SERVICEBOUND", "onServiceBound");
        if (m_sacanPending == true) {
            m_sacanPending = false;
            Runnable startScanr = new Runnable() {
                public void run() {
                    mManager.startScan();
                    stopProgressDialog();
                }
            };
            startProgressDialog();
            m_handler.postDelayed(startScanr, 20000);
        }
        m_serviceBound = true;
    }

    @Override
    public Context getApplicationContext() {
        return getContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        Log.d("UNBIND", "SERVICE UNBOUND");
        getActivity().unbindService(serviceConnection);
        m_serviceBound = false;
    }


    public void stopAnimationWaitDialog() {
        m_animationWaitDialog.dismiss();
    }

    public void startAnimationWaitDialog() {

        m_animationWaitDialog.setCancelable(false);
        m_animationWaitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        m_animationWaitDialog.setContentView(R.layout.jio_please_wait);
        m_animationWaitDialog.show();
        final ImageView img_loading_frame = (ImageView) m_animationWaitDialog.findViewById(R.id.jiopleasewait_img_location);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                Log.d("START", "ANIMATION");
                frameAnimation.start();
            }
        });
    }

    public void startProgressDialog() {
        if (true) {
            startAnimationWaitDialog();
        } else {
            dialogp.setMessage("Scanning for devices…please wait");
            dialogp.setCancelable(false);
            //dialogp.setContentView(R.layout.jio_please_wait);
            dialogp.show();
        }
    }

    public void stopProgressDialog() {
        if (true) {
            Runnable rAnim = new Runnable() {
                public void run() {
                    m_addr_pending = true;
                    final String addr = getLocationDetails();
                    Log.d("ADRDD", addr);
                    stopAnimationWaitDialog();
                    mManager.stopScan();
                    checkAndLaunchNodeviceFound();
                }
            };
            m_handler.postDelayed(rAnim, 20000);
        } else {
            Runnable r = new Runnable() {
                public void run() {
                    dialogp.dismiss();
                }
            };
            m_handler.postDelayed(r, 20000);
        }
    }

    public void checkAndLaunchNodeviceFound() {
        Log.d("SCANNEDNO", "checkAndLaunchNodeviceFound" + " : " + m_bleDetails.size());
        if (m_bleDetails.size() == 0) {
            Intent startNotFound = new Intent(getContext(), JioFinderNotFound.class);
            startActivity(startNotFound);
            getActivity().getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        Log.d("BINDSERVICE", "BINDSERVICE");
        getActivity().bindService(intent,
                serviceConnection, Context.BIND_AUTO_CREATE);
        return true;
    }

    ///////////////////////////////////////////////////////////
    public void updateJioPushNotification(String device_address) {
        Log.d("JIO", "NOTIFICATION PRINTED");
        String receivedData = "Jio Tag: Device Notification";
        Context context = getContext();
        Intent intent = new Intent(context, MainActivity.class);
        // Send data to NotificationView Class
        intent.putExtra("title", "Jio Tag: Device Notification");
        intent.putExtra("text", receivedData);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Jio Tag: Device Notification")
                .setSmallIcon(R.drawable.ic_action_settings_remote)
                .setAutoCancel(false)
                .setColor(0x00b359)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText("Jio Tag: Device Notification,Please dismiss this notification to stop repeated sound alert");
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            nm.createNotificationChannel(mChannel);
        }

        Notification m_notif = nbuilder.build();
        nm.notify(notificationId, m_notif);
    }

    public void showJioPushNotification(String device_address) {
        Log.d("JIO", "NOTIFICATION PRINTED");
        String receivedData = "Jio Tag: Device Notification";
        Context context = getContext();
        Intent intent = new Intent(context, MainActivity.class);
        // Send data to NotificationView Class
        intent.putExtra("title", "Jio Tag: Device Notification");
        intent.putExtra("text", receivedData);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Jio Tag: Device Notification")
                .setSmallIcon(R.drawable.ic_action_settings_remote)
                .setAutoCancel(false)
                .setColor(0x00b359)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText("Jio Tag: Device Notification,Please dismiss this notification to stop repeated sound alert");
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            nm.createNotificationChannel(mChannel);
        }
        // NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        Notification m_notif = nbuilder.build();
        m_notif.flags = Notification.FLAG_INSISTENT;
        nm.notify(notificationId, m_notif);

        int duration_ms = JioUtils.getAlertDuration(getContext(),device_address,true);
        Log.d("DURATIONN","Device single click duration is" + duration_ms);
        final String dev_addr_final=device_address;
        final Runnable stopSoundR = new Runnable() {
            public void run() {
                nm.cancel(notificationId);
                updateJioPushNotification(dev_addr_final);
            }
        };

        boolean phone_alert_repeat=JioUtils.getPhoneAlertRepeat(getContext(),device_address);
        if(phone_alert_repeat == false) {
            m_handler.postDelayed(stopSoundR, duration_ms * 1000);
        }

    }

    //////////////////////////////////////////////////////////

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        m_bleDetails.clear();
        m_bleDetailsAdapter = new BleDetailsAdapter(m_bleDetails, this, recyclerView);
        recyclerView.setAdapter(m_bleDetailsAdapter);
        m_bleDetailsAdapter.setFragment(this);
        m_handler = new Handler();
        mManager = BleDeviceManager.getInstance(getActivity());
        checkLocationPermission();
        Log.d("GOTNUT", mManager + "");
    }

    public UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    public BleDevice getDeviceMatch(String dev_address) {
        boolean found = false;
        for (BleDevice device : mBleDeviceList) {
            if (device.address.equals(dev_address)) {
                found = true;
                return device;
            }
        }
        return null;
    }

    public void stopAlarm(int pos, String device_address) {
        Log.d("stopAlarm", "stopAlarm called");
        Intent inte = new Intent();
        inte.setAction("com.nutapp.notifications_alarm_disabled");
        inte.putExtra("DEV_ADDRESS", device_address);
        m_sharedPrefContext.sendBroadcast(inte);
        //mManager.changeRingState(mBleDeviceList.get(pos), BleDevice.STATE_QUIT);
        mManager.changeRingState(getDeviceMatch(device_address), BleDevice.STATE_QUIT);
    }

    public void startAlarm(int pos, String DEVICE_ADDRESS) {
        Log.d("startAlarm", "startAlarm called");
        boolean deviceAlertValue = JioUtils.getDeviceAlertSetting(m_sharedPrefContext, DEVICE_ADDRESS);
        Intent inte = new Intent();
        inte.putExtra("DEV_ADDRESS", DEVICE_ADDRESS);
        if (deviceAlertValue) {
            inte.setAction("com.nutapp.notifications_alarm_enabled");
            m_sharedPrefContext.sendBroadcast(inte);
            //mManager.changeRingState(mBleDeviceList.get(pos), BleDevice.STATE_RING);
            mManager.changeRingState(getDeviceMatch(DEVICE_ADDRESS), BleDevice.STATE_RING);
        } else {
            inte.setAction("com.nutapp.notifications_alarm_disabled");
            m_sharedPrefContext.sendBroadcast(inte);
            Toast.makeText(m_sharedPrefContext, "DEVICE ALERT SETTING DISABLED: PLEASE ENABLE IT. ", Toast.LENGTH_SHORT).show();
        }

    }

    File image;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        //File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        image = new File(storageDir, imageFileName + ".jpg");
        Log.d("IMAGEPATH", storageDir.getAbsolutePath() + imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    CameraDevice.StateCallback newStateCall = new CameraDevice.StateCallback() {

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d("CAMERROR", "onError");
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            super.onClosed(camera);
            Log.d("CAMCLOSE", "onClosed");
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d("CAMDISCON", "onDisconnected");
        }

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d("CAMOPEN", "onOpened");
          /*  try {
                CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                builder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_USE_SCENE_MODE);
                builder.set(CaptureRequest.CONTROL_SCENE_MODE, CaptureRequest.CONTROL_SCENE_MODE_PORTRAIT);
                Surface preview= new Surface();
                builder.addTarget(preview);
                final CaptureRequest req = builder.build();

                camera.createCaptureSession(Arrays.asList(preview), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {

                            currentSession = session;
                            session.setRepeatingRequest(req, new CameraCaptureSession.CaptureCallback() {
                            }, new Handler());
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        //do something
                    }
                }, new Handler());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }*/
        }
    };

    public void startCameraClick() {
        Log.d("CAMERACLICK", "startCameraClick");
    /*    CameraManager m_camManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] m_cams = m_camManager.getCameraIdList();
            String cameraId = m_cams[0];
            m_camManager.openCamera(cameraId, newStateCall, new Handler());
        } catch (Exception e) {
            Log.d("CAMEXCEPTION", "cameraException");
        }*/
    }

    public void addToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //File f = new File(mCurrentPhotoPath);
        File f = new File(image.getAbsolutePath());
        Log.w("INFO", image.getAbsolutePath().toString());
        Log.w("INFO", mCurrentPhotoPath.toString());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CAMTAG", "CAMPICTAKEN");
        if (requestCode == JioUtils.BT_ENABLE_REQUEST ) {
            if(resultCode != RESULT_OK){
                Toast.makeText(getContext(),"The App will close as BT is not enabled!!",Toast.LENGTH_SHORT).show();
                getActivity().finishAffinity();
                return;
            }
        }
        if (requestCode == m_bleDetailsAdapter.REQUEST_ATTACH_ASSET && resultCode == RESULT_OK) {
            Log.d("GOTASSET", "NAME ACT" + data.getStringExtra("POSITION_TYPE").toString());

            String assetType = data.getStringExtra("POSITION_TYPE");
            String dev_addr = data.getStringExtra("POSITION_ADDR");
            int positionAsset = data.getIntExtra("POSITION_NUT", 0);
            String customName = data.getStringExtra("CUSTOMNAME");
            String customName_new = data.getStringExtra("CUSTOMNAME_NEW");
            //save custom Name
            prefEditor.putString(dev_addr + "CUSTOMNAME", customName_new);
            Log.d("CUSTOMNAME", "CUSTOMNAME" + "=" + customName_new);
            prefEditor.commit();

            Log.d("RESULTACT", assetType + "$" + positionAsset);
            m_bleDetailsAdapter.setImageAsset(dev_addr, assetType, positionAsset, customName);
            Uri uri_image = Uri.parse(data.getStringExtra("IMAGEURI"));
            m_bleDetailsAdapter.setCameraGalleryImage(dev_addr, uri_image);
        }

        if (requestCode == 111 && resultCode == RESULT_OK) {
            Log.d("GOTNAME", "NAME ACT");
            String devAddress = data.getStringExtra("ADDR_DEV");
            String devAddressName = data.getStringExtra("ADDR_DEV_NAME");
            Log.d("RESULTACT", devAddress + "$" + devAddressName);
            m_bleDetailsAdapter.setFriendlyName(devAddress, devAddressName);
        }
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Log.d("CAMTAG", "PICTAKEN");
                mImageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                //mImageView.setImageBitmap(mImageBitmap);
/*                Intent i = new Intent(Intent.ACTION_VIEW);

                i.setDataAndType(Uri.fromFile(image), "image/jpeg");
                startActivity(i);*/
                addToGallery();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("CAMTAG", "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public void setItemPosition(int pos) {
        m_itemPos = pos;
        Log.d("POSITION OF ITEM", pos + "");
    }

    private double getDistanceFromRSSI(int rssi) {
        int referenceRssi = -65;
        float propagationConstant = 3;
        float exponent = (referenceRssi - rssi) / (10 * propagationConstant);
        double distance = Math.pow(10, exponent);
        return distance;
    }

    ////////
    public void enableBT() {
        if (m_bluetoothAdpater.isEnabled() == false) {
            Intent btIntent = new Intent(m_bluetoothAdpater.ACTION_REQUEST_ENABLE);
            startActivityForResult(btIntent,JioUtils.BT_ENABLE_REQUEST);
        } else {
            if (m_bluetoothAdpater.getState() == BluetoothAdapter.STATE_ON) {
                Log.d("BTCONNECTIONSTATUS", "BT CONNECTED");
                m_waitingForScan = false;
                Runnable startScanr = new Runnable() {
                    public void run() {
                        mManager.startScan();
                        stopProgressDialog();
                    }
                };
                startProgressDialog();
                m_handler.postDelayed(startScanr, 2000);

/*
                startProgressDialog();
                mManager.startScan();
                stopProgressDialog();
*/
            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("MENU", "MENUCLICKED");
        switch (item.getItemId()) {
            case R.id.navigation_scan:
                Log.d("MENU", "SCAN");
                enableBT();
                break;
            case R.id.navigation_location:
                Log.d("MENU", "LOCATION");

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void forcedBTOffExit(){
        Log.d("forcedBTOffExit", "forcedBTOffExit");
        try {
            if (m_btReceiver != null) {
                Log.d("forcedBTOffExit", "UNREGISTER BTRECV");
                getContext().unregisterReceiver(m_btReceiver);
            }
        }catch(Exception e){
            Log.d("forcedBTOffExit","NOT REGISTRED");
        }

        try {
            if (m_nutAppNotificationReceiver != null) {
                Log.d("forcedBTOffExit", "UNREGISTER NUTRECV");
                getContext().unregisterReceiver(m_nutAppNotificationReceiver);
            }
        }catch(Exception e){
            Log.d("forcedBTOffExit","CONNECTTTT NOT REGISTRED");
        }
        try{
            //startDisconnecting()
            mManager.removeScanResultCallback(this);
            mManager.removeEventCallback(this);
            mManager.removeConnectStateChangedCallback(this);
            mManager.unbind(this);
        }catch(Exception e){
            Log.d("forcedBTOffExit","NUT UNBIND");
        }

        try{
            Log.d("UNREGLOC", "UNREGISTER LOCATION RCVR");
            getContext().unregisterReceiver(m_locationStateReceiver);
        }catch(Exception e){
            Log.d("UNREGLOC","EXCEPTION IN UNREGLOC");
        }
    }

    AlertDialog.Builder builder_exit;
    AlertDialog m_disconnectAlert_exit;
    public void showBTOnOffDialog() {
        //builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog ));
        builder_exit = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_Holo_Dialog_Alert));
        builder_exit.setMessage("Unfortunately Bt is turned off while app is active!!!").setTitle("BT Alert")
                .setCancelable(false)
                .setNegativeButton("Exit and Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"The App will close as BT is not enabled!!",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        getActivity().finishAffinity();
                    }
                }).setIcon(R.drawable.ic_action_settings_remote);

        m_disconnectAlert_exit = builder_exit.create();
        //m_disconnectAlert.setTitle(bleDevice.name + " Disconnected");
        m_disconnectAlert_exit.setTitle("BT Alert");
        //m_disconnectAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.asset_dialog_round_bkgd));
        m_disconnectAlert_exit.show();
        Button b_negative = m_disconnectAlert_exit.getButton(DialogInterface.BUTTON_NEGATIVE);
        b_negative.setBackgroundColor(0xFFCCF2FD);
    }


    private HomeViewModel homeViewModel;

    public BroadcastReceiver m_btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BLUETOOTHBLE", intent.getAction().toString());
            if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_bt_turn_on_from_any")) {
                forcedBTOffExit();
                final Runnable exitRestart = new Runnable() {
                    public void run() {
                        showBTOnOffDialog();
                    }
                };
                m_handler.postDelayed(exitRestart, 200);
            }
            if (intent.getAction().equalsIgnoreCase("android.bluetooth.adapter.action.STATE_CHANGED")) {
                Log.d("BLUETOOTHBLE", "Bluetooth status changed");
                switch (m_bluetoothAdpater.getState()) {
                    case BluetoothAdapter.STATE_ON:
                        Log.d("BTCONNECTIONCALLBACK", "BT CONNECTED");
                        if (m_bleDetails.size() == 0) {
                            m_waitingForScan = false;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    m_sacanPending = true;
                                    mManager.unbind(m_devCon);
                                    mManager.addScanResultCallback(m_ScanCon);
                                    mManager.addConnectStateChangedCallback(m_connStateCall);
                                    mManager.addEventCallback(m_EventCallBack);
                                    mManager.bind(m_devCon);
                                }
                            });
                        }
                        break;

                    case BluetoothAdapter.STATE_OFF:
                        Log.d("BTCONNECTIONSTATUS", "BT DISCONNECTED");
                        Intent i = new Intent(context, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(i);
                        Intent btIntent = new Intent();
                        btIntent.setAction("com.nutapp.notifications_bt_turn_on_from_any");
                        getContext().sendBroadcast(btIntent);
                       break;
                }
            }
        }
    };

    @Override
    public void onResume() {
        Log.d("ONRESUME", "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("ONPAUSE", "onPause");
    }



    @Override
    public void onStop() {
        Log.d("ONSTOP", "removeScanResultCallback");
        super.onStop();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("ONATTACH", "onAttach Called");
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 5;

    private void checkLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionAlreadyGrantedInMain() == false) {
                Log.d("ENTERPERM", "checkLocationPermission");
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_PHONE_NUMBERS,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.RECEIVE_SMS
                                },
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                Log.d("PERM", "ALREADY GRANTED");
                mManager.addScanResultCallback(this);
                mManager.addConnectStateChangedCallback(this);
                mManager.addEventCallback(this);
                mManager.bind(this);
                m_bleDetailsAdapter.setFragment(this);
                m_devCon = this;
                m_ScanCon = this;
                m_connStateCall = this;
                m_EventCallBack = this;
                if (m_bleDetailsAdapter.getItemCount() <= 0) {
                    Log.d("BACKEY", "RELOAD");
                    enableBT();
                    // m_sacanPending=false;
                }
            }
        } else {
            Log.d("PERM", "ALREADY GRANTED");
            mManager.addScanResultCallback(this);
            mManager.addConnectStateChangedCallback(this);
            mManager.addEventCallback(this);
            mManager.bind(this);
            m_bleDetailsAdapter.setFragment(this);
            m_devCon = this;
            m_ScanCon = this;
            m_connStateCall = this;
            m_EventCallBack = this;
            if (m_bleDetailsAdapter.getItemCount() <= 0) {
                Log.d("BACKEY", "RELOAD");
                enableBT();
                // m_sacanPending=false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d("onRequest", "onRequestPermissionsResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                Log.d("INRESULT", "MY_PERMISSIONS_REQUEST_LOCATION" + permissions.toString());
                // If request is cancelled, the result arrays are empty.
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Log.d("NOPERM", "NOT Granted");
                        Toast.makeText(getContext(), "You have to accept all the permissions else the app will close", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                        return;
                    }
                }

                Log.d("INRESULT", "MY_PERMISSIONS_REQUEST_LOCATION GRANTED");
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                mManager.addScanResultCallback(this);
                mManager.addConnectStateChangedCallback(this);
                mManager.addEventCallback(this);
                mManager.bind(this);
                m_bleDetailsAdapter.setFragment(this);
                m_devCon = this;
                m_ScanCon = this;
                m_connStateCall = this;
                m_EventCallBack = this;
                //createSharedPreferences();
                if (m_bleDetailsAdapter.getItemCount() <= 0) {
                    Log.d("BACKEY", "RELOAD");
                    enableBT();
                    // m_sacanPending=false;
                }
            }
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ONCREATE", "onCreate Called");
        createSharedPreferences();
        // checkLocationPermission();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        /*View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        root = inflater.inflate(R.layout.recycleview, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(m_bleDetailsAdapter);

        IntentFilter intFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        intFilter.addAction("com.nutapp.notifications_bt_turn_on_from_any");
        getContext().registerReceiver(m_btReceiver, intFilter);


        /*getContext().unregisterReceiver(m_nutAppNotificationReceiver);*/

        IntentFilter nutFiler = new IntentFilter("com.nutapp.notifications_alarm_start");
        nutFiler.addAction("com.nutapp.notifications_disconnect");
        nutFiler.addAction("com.nutapp.notifications_alarm_stop");
        nutFiler.addAction("com.nutapp.notifications_connect");
        nutFiler.addAction("com.nutapp.notifications_showlocation");

        getContext().registerReceiver(m_nutAppNotificationReceiver, nutFiler);

        IntentFilter filter_gps = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter_gps.addAction(Intent.ACTION_PROVIDER_CHANGED);
        filter_gps.addAction("com.nutapp.notifications_location_turn_on");
        getContext().registerReceiver(m_locationStateReceiver, filter_gps);

        m_isReceiverRegistered = true;
        dialogp = new ProgressDialog(getContext());
        m_animationWaitDialog = new AppCompatDialog(getActivity(), android.R.style.Theme_Light);
        m_animationWaitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_animationWaitDialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        /*dialogp.setButton(DialogInterface.BUTTON_NEGATIVE,"Stop Scanning", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialogp.dismiss();
            }
        });*/
        dialogp.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("STOPSCAN", "SCANNINGSTOPPED");
                mManager.stopScan();
                m_waitingForScan = false;
            }
        });
        setHasOptionsMenu(true);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.nav_view);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        menuView.getChildAt(1).setBackground(getResources().getDrawable(R.drawable.main_circle));


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_scan:
                        Log.d("SCANMENU", "SCAN");
                        enableBT();
                        // Toast.makeText(MainActivity.this, "Camera", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_setting:
                        //startCamera();
                        Intent startMain = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(startMain);
                        //Toast.makeText(MainActivity.this, "Location Maps", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_location:
                        Log.d("NAVOPTION", "MYLOCATION");
                        readLocationDetails();
                        break;
                    case R.id.navigation_bell:
                        Log.d("BELL", "BELLOPTION");
                        try {
                            Object service = getActivity().getSystemService("statusbar");
                            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                            Method expand = statusbarManager.getMethod("expandNotificationsPanel");
                            expand.invoke(service);
                        }catch(Exception e){
                            Log.d("BELL", "EXCEPTIONNNNN");
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });

        ////Back key
        root.setFocusableInTouchMode(true);
        root.requestFocus();

        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_SHORT).show();
                        Log.d("BACKKEY", "BACKKEY PRESSED");
                        m_onBackKey = true;
                        onBackKeyEntered();
                        return false;
                    }
                }
                return false;
            }
        });


        return root;
    }

    public boolean isNetworkEnabled() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

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


    public void readLocationDetails() {
        Log.d("LOCATION", "readLocation");
        if (isLocationEnabled(getContext()) == true && isNetworkEnabled()) {
            Log.d("LOCENABLED", "LOCENABLED");
            m_fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            Task<Location> task = m_fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("LOCATION:", location.getLatitude() + " " + location.getLongitude());
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        List<Address> addresses = null;
                        Geocoder geocoder;
                        Locale loc = Locale.getDefault();
                        geocoder = new Geocoder(getContext(), loc);

                        try {
                            addresses = geocoder.getFromLocation(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    // In this sample, get just a single address.
                                    1);
                            Address address = addresses.get(0);
                            Log.d("ADDRESS", address.getAddressLine(0));
                            Log.d("ADDRESS", address.getCountryName());
                            Toast.makeText(getContext(), "Current Location is: " + address.getAddressLine(0), Toast.LENGTH_SHORT).show();
                            Intent mapsIntent = new Intent(getContext(), MapsActivity.class);
                            mapsIntent.putExtra("LATITUDE", location.getLatitude());
                            mapsIntent.putExtra("LONGITUDE", location.getLongitude());
                            mapsIntent.putExtra("ADDRESS", address.getAddressLine(0));
                            startActivity(mapsIntent);
                        } catch (Exception e) {
                            Log.d("LOCATIONEXCEPTION", "EXCEPTION");
                        }
                    }

                }
            });
        } else {
            Toast.makeText(getContext(), "Please Enable Location and Network to See current location!!!! ", Toast.LENGTH_SHORT).show();
        }

    }


    public String getLocationDetails() {
        Log.d("LOCATION", "getLocationDetails");

        if (isLocationEnabled(getContext()) == true && isNetworkEnabled()) {
            Log.d("LOCENABLED", "LOCENABLED");
            m_fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            Task<Location> task = m_fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("LOCATION:", location.getLatitude() + " " + location.getLongitude());
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        List<Address> addresses = null;
                        Geocoder geocoder;
                        Locale loc = Locale.getDefault();
                        geocoder = new Geocoder(getContext(), loc);

                        try {
                            addresses = geocoder.getFromLocation(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    // In this sample, get just a single address.
                                    1);
                            Address address = addresses.get(0);
                            Log.d("ADDRESS", address.getAddressLine(0));
                            Log.d("ADDRESS", address.getCountryName());
                            ret_addr = address.getAddressLine(0);
                            m_addr_pending = true;
                            if (m_addr_pending == true) {
                                m_bleDetailsAdapter.setLocationDetails(ret_addr);
                                m_addr_pending = false;
                                Toast.makeText(getContext(), "Current Location is: " + address.getAddressLine(0), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.d("LOCATIONEXCEPTION", "EXCEPTION");
                        }
                    }

                }
            });
        } else {
            Toast.makeText(getContext(), "Please Enable Location and Network to See current location!!!! ", Toast.LENGTH_SHORT).show();
        }
        if (m_addr_pending == true) {
            m_bleDetailsAdapter.setLocationDetails(ret_addr);
            m_addr_pending = false;
        }
        return ret_addr;
    }


}