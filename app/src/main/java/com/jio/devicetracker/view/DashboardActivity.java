// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jio.devicetracker.R;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.SearchDeviceStatusData;
import com.jio.devicetracker.database.pojo.SearchEventData;
import com.jio.devicetracker.database.pojo.TrackerdeviceData;
import com.jio.devicetracker.database.pojo.request.SearchDeviceStatusRequest;
import com.jio.devicetracker.database.pojo.request.SearchEventRequest;
import com.jio.devicetracker.database.pojo.request.TrackdeviceRequest;
import com.jio.devicetracker.database.pojo.response.SearchDeviceStatusResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.TrackerDeviceListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;

/**
 * Implementation of Dashboard Screen to show the trackee list and hamburger menu.
 */
public class DashboardActivity extends AppCompatActivity implements MessageListener, View.OnClickListener {

    Toolbar toolbar;
    private RecyclerView listView;
    private String TAG = "DashboardActivity";
    private TrackerdeviceResponse mtrackerresponse;
    Intent intent = null;
    private static String userToken = null;
    public static List<MultipleselectData> selectedData;
    public static List<TrackerdeviceResponse.Data> data;
    private TrackerDeviceListAdapter adapter;
    private static List<AddedDeviceData> addDeviceList;
    private static final int PERMIT_ALL = 1;
    public static List<String> consentListPhoneNumber = null;
    public static Map<Double, Double> latLngMap = null;
    private static DBManager mDbManager;
    private TextView devicePresent = null;
    private ProgressDialog progressDialog = null;
    public static Map<String, Map<Double, Double>> namingMap = null;
    private static Context context = null;
    public static Map<Double, Double> fmsLatLngMap = null;
    public static Map<String, Map<Double, Double>> fmsNamingMap = null;
    private String trackeeName = "";
    public static String adminEmail;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView user_account_name = null;
    private List<SubscriptionInfo> subscriptionInfos;
    Locale locale = Locale.ENGLISH;
    private int batteryLevel;
    private FusedLocationProviderClient client;
    String[] permissions = {READ_SMS, RECEIVE_SMS, SEND_SMS, READ_PHONE_STATE, ACCESS_COARSE_LOCATION};
    private static Double latitude;
    private static Double longitude;
    private static int signalStrengthValue;
    private int REQUEST_COARSE_LOCATION_PERMISSION = 1;
    private static List<String> deviceIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.customToolbar);
        listView = findViewById(R.id.listView);
        FloatingActionButton actionBtn = findViewById(R.id.fab);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(linearLayoutManager);
        intent = getIntent();
        mDbManager = new DBManager(this);
        consentRequestBox(intent.getBooleanExtra("flag", false), intent.getStringExtra("phone"));
        Button trackBtn = toolbar.findViewById(R.id.track);
        trackBtn.setVisibility(View.VISIBLE);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.DASHBOARD_TITLE + "               ");
        trackBtn.setOnClickListener(this);
        actionBtn.setOnClickListener(this);
        setSupportActionBar(toolbar);
        MessageReceiver.bindListener(DashboardActivity.this);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        requestPermission();
        NavigationView navigationView = findViewById(R.id.nv);
        View header = navigationView.getHeaderView(0);
        user_account_name = header.findViewById(R.id.user_account_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selectedData = new ArrayList<>();
        addDeviceList = new ArrayList<>();
        latLngMap = new LinkedHashMap<>();
        consentListPhoneNumber = new LinkedList<>();
        namingMap = new HashMap<>();
        fmsNamingMap = new HashMap<>();
        context = getApplicationContext();
        fmsLatLngMap = new LinkedHashMap<>();

        getAdminDetail();
        Util.getInstance().getIMEI(this);
        if (!hasPermissions(DashboardActivity.this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMIT_ALL);
        }

        if (RegistrationActivity.isFMSFlow == false) {
            showDatainList();
        } else {
            showDataFromFMS();
        }
        adapter.setOnItemClickPagerListener(new TrackerDeviceListAdapter.RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position, MultipleselectData data, boolean val) {
                if (val) {
                    selectedData.add(data);
//                    checkConsentPublishMQTTMessage(data);
                    Log.d(TAG, "Value of data" + selectedData.size());
                } else {
                    for (MultipleselectData multipleselectData : selectedData) {
                        if (multipleselectData.getPhone() == data.getPhone()) {
                            selectedData.remove(multipleselectData);
                        }
                    }
                }
            }

            @Override
            public void recyclerviewEditList(String relation, String phoneNumber) {
                gotoEditScreen(relation, phoneNumber);
            }

            @Override
            public void recyclerviewDeleteList(String phoneNumber, int position) {

                alertDilogBoxWithCancelbtn(Constant.DELETC_DEVICE, Constant.ALERT_TITLE, phoneNumber, position);

            }

            @Override
            public void consetClick(String phoneNumber) {
                sendSMS(phoneNumber);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.profile:
                        gotoProfileActivity();
                        break;
                    case R.id.settings:
                        Toast.makeText(DashboardActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.helpPrivacy:
                        Toast.makeText(DashboardActivity.this, "Help & Support", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.logout:
                        updateLogoutData();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        sendLocationInTimeInterval();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        DashboardActivity.this.registerReceiver(broadcastreceiver, intentFilter);
        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        client = LocationServices.getFusedLocationProviderClient(this);
        deviceIds = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastreceiver != null) {
            unregisterReceiver(broadcastreceiver);
        }
    }

    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryLevel = (int) (((float) level / (float) scale) * 100.0f);
        }
    };

    /****** To get the lat and long of the current device*******/
    private void getLocation() {
        client.getLastLocation().addOnSuccessListener(DashboardActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(@NonNull Location location) {
                if (location != null) {
                    latitude = location.getLongitude();
                    longitude = location.getLongitude();
                }
            }
        });
    }

    private void gotoProfileActivity() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void updateLogoutData() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(Constant.ALERT_TITLE);
        adb.setMessage(Constant.LOGOUT_CONFIRMATION_MESSAGE);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mDbManager.updateLogoutData();
                goToLoginActivity();
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        adb.show();
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            AdminLoginData adminLoginData = mDbManager.getAdminLoginDetail();
            user_account_name.setText(adminLoginData.getName().substring(0, 1).toUpperCase(locale) + adminLoginData.getName().substring(1));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAdminDetail() {
        AdminLoginData adminLoginData = new DBManager(this).getAdminLoginDetail();
        if(adminLoginData != null) {
            userToken = adminLoginData.getUserToken();
            adminEmail = adminLoginData.getEmail();
        }
    }

    /***** Publish the MQTT message along with battery level, signal strength and time format*********/
    public void publishMessage() {
        getLocation();
        String message = "{\"imi\":\"" + Util.imeiNumber + "\",\"evt\":\"GPS\",\"dvt\":\"JioDevice_g\",\"alc\":\"0\",\"lat\":\"" + latitude + "\",\"lon\":\"" + longitude + "\",\"ltd\":\"0\",\n" +
                "\"lnd\":\"0\",\"dir\":\"0\",\"pos\":\"A\",\"spd\":\"" + 12 + "\",\"tms\":\"" + getMQTTTimeFormat() + "\",\"odo\":\"0\",\"ios\":\"0\",\"bat\":\"" + batteryLevel + "\",\"sig\":\"" + signalStrengthValue + "\"}";
        String topic = Constant.MQTT_SIT_TOPIC;
        Log.d("Message --> ", message);
        Log.d("Topic --> ", topic);
        new MQTTManager().publishMessage(topic, message);
    }

    private String getMQTTTimeFormat() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        return sdf.format(c.getTime());
    }

    private void gotoEditScreen(String relation, String phoneNumber) {
        Intent intent = new Intent(DashboardActivity.this, EditActivity.class);
        intent.putExtra("number", phoneNumber);
        intent.putExtra("Relation", relation);
        startActivity(intent);
    }

    private void isDevicePresent() {
        devicePresent = findViewById(R.id.devicePresent);
        List<AddedDeviceData> alldata = null;
        if (RegistrationActivity.isFMSFlow == false) {
            alldata = mDbManager.getAlldata(adminEmail);
            if (alldata.isEmpty()) {
                listView.setVisibility(View.INVISIBLE);
                devicePresent.setVisibility(View.VISIBLE);
            } else {
                devicePresent.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } else {
            alldata = mDbManager.getAlldataFromFMS();
            if (alldata.isEmpty()) {
                listView.setVisibility(View.INVISIBLE);
                devicePresent.setVisibility(View.VISIBLE);
            } else {
                devicePresent.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getServicecall(String token) {
        gotoAddDeviceScreen();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                getServicecall(userToken);
                break;
            case R.id.track:
                trackDevice();
                break;
            default:
                Log.d("TAG", "Some other button is clicked");
                break;
        }
    }

    /***** Track the device ***********/
    private void trackDevice() {
        showProgressBarDialog();
        SearchDeviceStatusData searchDeviceStatusData = new SearchDeviceStatusData();
        SearchDeviceStatusData.Device device = searchDeviceStatusData.new Device();
        AdminLoginData adminLoginDetail = mDbManager.getAdminLoginDetail();
        List<String> data = new ArrayList<>();
        data.add(adminLoginDetail.getUserId());
        device.setUsersAssigned(data);
        searchDeviceStatusData.setDevice(device);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new SearchDeviceStatusRequest(new SearchDeviceStatusSuccessListener(), new SearchDeviceStatusErrorListener(), userToken, searchDeviceStatusData));
    }

    /****** Search device status response success listener **************/
    private class SearchDeviceStatusSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            SearchDeviceStatusResponse searchDeviceStatusResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchDeviceStatusResponse.class);
            if (selectedData.size() > 0) {
                for (SearchDeviceStatusResponse.Data data : searchDeviceStatusResponse.getData())
                    for (MultipleselectData multipleselectData : selectedData) {
                        if (multipleselectData.getPhone().equalsIgnoreCase(data.getDevice().getPhone())) {
                            deviceIds.add(data.getDevice().getId());
                        }
                    }
                SearchEventData searchEventData = new SearchEventData();
                SearchEventData.Device device = searchEventData.new Device();
                device.setId(deviceIds.get(0));
                searchEventData.setFrom(Util.convertTimeToEpochtime());
                searchEventData.setTo(Util.getTimeEpochFormatAfterCertainTime(Constant.EPOCH_TIME_DURATION));
                SearchEventData.Flags flags = searchEventData.new Flags();
                flags.setPopulateGeofence(false);
                flags.setPopulateRoute(false);
                searchEventData.setFlags(flags);
                searchEventData.setDevice(device);
                RequestHandler.getInstance(getApplicationContext()).handleRequest(new SearchEventRequest(new SearchEventSuccessListener(), new SearchEventErrorListener(), userToken, searchEventData));
            } else {
                Log.d("TAG", "Please add the device first and then select");
            }
        }
    }

    /****** Search device status response error listener **************/
    private class SearchDeviceStatusErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("TAG", "Error Response");
        }
    }

    /********** Search Even Success Listener ************/
    private class SearchEventSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            progressDialog.dismiss();
        }
    }

    /********** Search Even Error Listener ************/
    private class SearchEventErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("TAG", "Error in Search Event Response");
            progressDialog.dismiss();
        }
    }

    public void startTheScheduler() {
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            Log.i("TAG", "Inside Scheduler");
            DashboardActivity.this.runOnUiThread(() -> {
                makeAPICall(userToken);
                new MapsActivity().showMapOnTimeInterval();
            });
        }, 0, MapsActivity.refreshIntervalTime, TimeUnit.SECONDS);
    }

    /******** Sends the current location message to the Borqs portal after every 10 seconds ************/
    public void sendLocationInTimeInterval() {
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            DashboardActivity.this.runOnUiThread(() -> {
                makeMQTTConnection();
                publishMessage();
            });
        }, 0, Constant.MQTT_TIME_INTERVAL, TimeUnit.SECONDS);
    }

    /******* Connect to the MQTT ************/
    private void makeMQTTConnection() {
        MQTTManager mqttManager = new MQTTManager();
        mqttManager.getMQTTClient(this);
        mqttManager.connetMQTT();
    }

    public void makeAPICall(String token) {
        TrackerdeviceData data = new TrackerdeviceData();
        TrackerdeviceData.StartsWith startsWith = data.new StartsWith();
        startsWith.setCurrentDate(Util.convertTimeToEpochtime());
        data.setStartsWith(startsWith);
        if (context != null) {
            RequestHandler.getInstance(context).handleRequest(new TrackdeviceRequest(new SuccessAPICall(), new UnSuccessfullAPICall(), token, data));
        }
    }

    private class SuccessAPICall implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            mtrackerresponse = Util.getInstance().getPojoObject(String.valueOf(response), TrackerdeviceResponse.class);
            data = mtrackerresponse.getmData();
            Log.d(TAG, "Response print" + response);
            if (mDbManager != null) {
                mDbManager.updateBorqsData(data);
            }
        }
    }

    private class UnSuccessfullAPICall implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(DashboardActivity.this, Constant.TOKEN_NULL, Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoAddDeviceScreen() {
        Intent intent = new Intent(getApplicationContext(), NewDeviceActivity.class);
        startActivity(intent);
    }

    private void sendSMS(String phoneNumber) {
        String userName = mDbManager.getAdminDetail();
        String consentStatus = mDbManager.getConsentStatusBorqs(phoneNumber);
        if ("Yes JioTracker".equalsIgnoreCase(consentStatus)) {
            Util.alertDilogBox(Constant.CONSENT_APPROVED, Constant.ALERT_TITLE, this);
        } else {
            String phoneNumber1 = subscriptionInfos.get(0).getNumber();
            new SendSMSTask().execute(phoneNumber, userName + " from JioTracker application wants to track your location, please click on given link https://www.example.com/home?data="+phoneNumber1.trim().substring(2,phoneNumber1.length()));
            Toast.makeText(DashboardActivity.this, "Consent sent", Toast.LENGTH_SHORT).show();
            if (!RegistrationActivity.isFMSFlow) {
                mDbManager.updatependingConsent(phoneNumber);
                showDatainList();
            } else {
                mDbManager.updatependingConsentFMS(phoneNumber);
                showDataFromFMS();
            }
        }
    }

    public boolean hasPermissions(Context context, String[] permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void messageReceived(String message, String phoneNum) {
        Toast.makeText(DashboardActivity.this, "Received message -> " + message + " from phone number -> " + phoneNum, Toast.LENGTH_SHORT).show();
        Log.d("Received Message --> ", message);
        String phone = phoneNum.substring(3);
        if (!RegistrationActivity.isFMSFlow) {
            mDbManager.updateConsentInBors(phone, message.toLowerCase(locale).trim());
            showDatainList();
        } else {
            mDbManager.updateConsentInFMS(phone, message);
            showDataFromFMS();
        }
    }

    public void showDatainList() {
        addDeviceList = mDbManager.getAlldata(adminEmail);
        if (addDeviceList != null) {
            adapter = new TrackerDeviceListAdapter(this, addDeviceList);
            listView.setAdapter(adapter);
        }
    }

    private void showDataFromFMS() {
        addDeviceList = mDbManager.getAlldataFromFMS();
        if (addDeviceList != null) {
            adapter = new TrackerDeviceListAdapter(this, addDeviceList);
            listView.setAdapter(adapter);
        }
    }

    private void showProgressBarDialog() {
        progressDialog = ProgressDialog.show(DashboardActivity.this, "", Constant.LOADING_DATA, true);
        progressDialog.setCancelable(true);
    }

    public void alertDilogBoxWithCancelbtn(String message, String title, String phoneNumber, int position) {
        AlertDialog.Builder adb = new AlertDialog.Builder(DashboardActivity.this);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", (dialog, which) -> {
            if (RegistrationActivity.isFMSFlow == false) {
                mDbManager.deleteSelectedData(phoneNumber);
            } else {
                mDbManager.deleteSelectedDataformFMS(phoneNumber);
            }
            //mDbManager.deleteSelectedData(phoneNumber);
            adapter.removeItem(position);
            isDevicePresent();
        });
        adb.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        adb.show();
    }

    public void consentRequestBox(boolean flag, String phoneNumber) {
        if (flag) {
            Util.alertDilogBox("Request consent for device " + phoneNumber, "Jio Alert", this);
            flag = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if(data != null ){
            String number=data.toString().substring(data.toString().length()-10);
            showDialog(number);
        }
        isDevicePresent();
    }

    public void showDialog(String number) {
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setContentView(R.layout.number_display_dialog);
        dialog.setTitle("Title...");
        dialog.getWindow().setLayout(1000, 500);


        // set the custom dialog components - text, image and button
        final Button yes = (Button) dialog.findViewById(R.id.positive);
        Button no = (Button) dialog.findViewById(R.id.negative);



        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendSMSTask().execute(number, "Yes Jiotracker");
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "COARSE_PERMISSION_GRANTED");
            } else {
                Toast.makeText(getApplicationContext(), "COARSE PEMISSSION NOT GRANTED", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (requestCode == 100) {
            if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
        }
    }


    /************  Class to calculate the signal strength ***********************/
    class MyPhoneStateListener extends PhoneStateListener {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(DashboardActivity.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSION);
                return;
            } else {
                List<CellInfo> cellInfoList = mTelephonyManager.getAllCellInfo();
                if (cellInfoList != null) {
                    for (CellInfo cellInfo : cellInfoList) {
                        if (cellInfo instanceof CellInfoLte) {
                            signalStrengthValue = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                        }
                    }
                }
            }
        }
    }
}