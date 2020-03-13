/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.jio.devicetracker.R;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.GroupData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.ListOnHomeScreen;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.SearchDeviceStatusData;
import com.jio.devicetracker.database.pojo.request.SearchDeviceStatusRequest;
import com.jio.devicetracker.database.pojo.response.SearchDeviceStatusResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendLocationService;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.ConsentTimeUpdate;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.HomeActivityListAdapter;
import com.jio.devicetracker.view.adapter.TrackerDeviceListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Implementation of Dashboard Screen to show the trackee list and hamburger menu.
 */
public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView listView;
    private static String userToken = null;
    public static List<MultipleselectData> selectedData;
    public static List<TrackerdeviceResponse.Data> data;
    private TrackerDeviceListAdapter adapter;
    public static List<String> consentListPhoneNumber = null;
    private static DBManager mDbManager;
    public static Map<String, Map<Double, Double>> namingMap = null;
    private static Context context = null;
    public static String adminEmail;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView user_account_name = null;
    private List<SubscriptionInfo> subscriptionInfos;
    Locale locale = Locale.ENGLISH;
    private static int batteryLevel;
    private static FusedLocationProviderClient client;
    private static Double latitude;
    private static Double longitude;
    private static int signalStrengthValue;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private WorkManager mWorkManager = null;
    private static Thread thread = null;
    public static List<GroupData> specificGroupMemberData = null;
    public static List<HomeActivityListData> listOnHomeScreens;
    public static String groupName = "";
    public static boolean isAddIndividual = false;
    public static boolean isComingFromGroupList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Intent intent = getIntent();
        consentRequestBox(intent.getBooleanExtra("flag", false), intent.getStringExtra("name"));
        isPermissionGranted();
        setLayoutData();
        setNavigationData();
        initializeDataMember();
       // checkPermission();
        getAdminDetail();
        setConstaint();
        startService();
        registerReceiver();
        deepLinkingURICheck();
        addDataInHomeScreen();
        adapterEventListener();
        insertUserData();
    }

    private void insertUserData() {
/*
        List<HomeActivityListData> userList = new ArrayList<>();
        HomeActivityListData homeActivityListData = new HomeActivityListData();
        homeActivityListData.setName("umapathi");
        homeActivityListData.setNumber("9091020584");
        homeActivityListData.setLat("12.9140667");
        homeActivityListData.setLng("77.6650655");

        HomeActivityListData homeActivityListData1 = new HomeActivityListData();
        homeActivityListData1.setName("Teja sree");
        homeActivityListData1.setNumber("8088422893");
        homeActivityListData1.setLat("12.9950641");
        homeActivityListData1.setLng("77.6810009");

*/


    }

    private void isPermissionGranted() {
        if (LoginActivity.isAccessCoarsePermissionGranted == false) {
            Util.alertDilogBox(Constant.ACCESS_COARSE_PERMISSION_ALERT, Constant.ALERT_TITLE, this);
        }
    }



    private void adapterEventListener() {
        if (adapter != null) {
            adapter.setOnItemClickPagerListener(new TrackerDeviceListAdapter.RecyclerViewClickListener() {
                @Override
                public void recyclerViewListClicked(View v, int position, MultipleselectData data, boolean val) {
                    if (val) {
                        selectedData.add(data);
                    } else {
                        Iterator<MultipleselectData> iterator = selectedData.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().getPhone().equalsIgnoreCase(data.getPhone())) {
                                iterator.remove();
                            }
                        }
                    }
                }

                @Override
                public void recyclerviewEditList(String relation, String phoneNumber) {
                    gotoEditScreen(relation,phoneNumber);
                }

               @Override
                public void recyclerviewDeleteList(String phoneNumber, int position) {
                    alertDilogBoxWithCancelbtn(Constant.DELETC_DEVICE, Constant.ALERT_TITLE, phoneNumber, position);

                }

                @Override
                public void consetClick(String phoneNumber) {
                    showSpinnerforConsentTime(phoneNumber);
                }

               /* @Override
                public void onPopupMenuClicked(View v, int position, String name, String relation) {
                    PopupMenu popup = new PopupMenu(DashboardActivity.this, v);
                    popup.inflate(R.menu.options_menu);
                    popup.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.editOnCardView:
                                gotoEditScreen(name, relation);
                                break;
                            case R.id.deleteOnCardView:
                                Toast.makeText(DashboardActivity.this, "You have selected delete", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    });
                    popup.show();
                }*/

               /* @Override
                public void onRecyclerViewItemClick(View v, int position, String name) {
                    groupName = name;
//                    if(listOnHomeScreens != null && listOnHomeScreens.get(position).isGroupMember() == false) {
//                        startActivity(new Intent(DashboardActivity.this, GroupListActivity.class));
//                    }
                }*/
            });
        }
    }

    private void initializeDataMember() {
        selectedData = new ArrayList<>();
        mWorkManager = WorkManager.getInstance();
        consentListPhoneNumber = new LinkedList<>();
        namingMap = new HashMap<>();
        Util.getInstance().getIMEI(this);
        mDbManager = new DBManager(this);
        thread = new Thread(new RefreshMap());
        thread.start();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(broadcastreceiver, intentFilter);
        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (specificGroupMemberData == null) {
            specificGroupMemberData = new ArrayList<>();
        }
        if (listOnHomeScreens == null) {
            listOnHomeScreens = new ArrayList<>();
        }
    }

    private void setNavigationData() {
        NavigationView navigationView = findViewById(R.id.nv);
        View header = navigationView.getHeaderView(0);
        user_account_name = header.findViewById(R.id.user_account_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.profile:
                        gotoProfileActivity();
                        break;
                    case R.id.settings:
                        goToRefreshIntervalSettingActivity();
                        break;
                    case R.id.helpPrivacy:
//                        Toast.makeText(DashboardActivity.this, "Help & Support", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.traceelist:
                        gotoTraceeListScreen();
                        break;
                    case R.id.trackerListMenu:
                        gotoTrackerListScreen();
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
    }

    private void gotoTraceeListScreen() {
        Intent intent = new Intent(this, TraceeListActivity.class);
        startActivity(intent);
    }

    private void gotoTrackerListScreen() {
        startActivity(new Intent(this, TrackerListActivity.class));
    }

    private void setLayoutData() {
        Toolbar toolbar = findViewById(R.id.customToolbar);
        listView = findViewById(R.id.listView);
        FloatingActionButton fabCreateGroup = findViewById(R.id.createGroup);
        FloatingActionButton fabAddDevice= findViewById(R.id.addDevice);
        FloatingActionButton fabAddContact = findViewById(R.id.addContact);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(linearLayoutManager);
        Button trackBtn = toolbar.findViewById(R.id.track);
        //ImageView scanner = toolbar.findViewById(R.id.qrScanner);
        trackBtn.setVisibility(View.VISIBLE);
//        scanner.setVisibility(View.VISIBLE);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.DASHBOARD_TITLE + "               ");
        trackBtn.setOnClickListener(this);
//        scanner.setOnClickListener(this);
        fabCreateGroup.setOnClickListener(this);
        fabAddDevice.setOnClickListener(this);
        fabAddContact.setOnClickListener(this);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        context = getApplicationContext();
    }

    private void goToRefreshIntervalSettingActivity() {
        startActivity(new Intent(this, RefreshIntervalSettingActivity.class));
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, SendLocationService.class);
        serviceIntent.putExtra("inputExtra", getString(R.string.notification_subtitle));
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void alertDilogBoxWithCancelbtn(String message, String title, String phoneNumber, int position) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", (dialog, which) -> {
            mDbManager.deleteSelectedData(phoneNumber);
//            adapter.removeItem(position);
            adapter.notifyDataSetChanged();
            isDevicePresent();
        });
        adb.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        adb.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastreceiver != null) {
            unregisterReceiver(broadcastreceiver);
        }
        if (mRecevier != null) {
            unregisterReceiver(mRecevier);
        }
    }

    // Broadcast to calculate battery strength
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
        if (client != null) {
            client.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            });
        }
    }

    private void gotoProfileActivity() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void updateLogoutData() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(Constant.ALERT_TITLE);
        adb.setMessage(Constant.LOGOUT_CONFIRMATION_MESSAGE);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", (dialog, which) -> {
            Util.clearAutologinstatus(this);
            mDbManager.updateLogoutData();
            goToLoginActivity();
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
        if (adminLoginData != null) {
            userToken = adminLoginData.getUserToken();
            adminEmail = adminLoginData.getEmail();
        }
    }

    /***** Publish the MQTT message along with battery level, signal strength and time format*********/
    public void publishMessage() {
        getLocation();
        String message = "{\"imi\":\"" + Util.imeiNumber + "\",\"evt\":\"GPS\",\"dvt\":\"JioDevice_g\",\"alc\":\"0\",\"lat\":\"" + latitude + "\",\"lon\":\"" + longitude + "\",\"ltd\":\"0\",\n" +
                "\"lnd\":\"0\",\"dir\":\"0\",\"pos\":\"A\",\"spd\":\"" + 12 + "\",\"tms\":\"" + Util.getInstance().getMQTTTimeFormat() + "\",\"odo\":\"0\",\"ios\":\"0\",\"bat\":\"" + batteryLevel + "\",\"sig\":\"" + signalStrengthValue + "\"}";
        String topic = Constant.MQTT_CIT_TOPIC;
        new MQTTManager().publishMessage(topic, message);
    }

    private void gotoEditScreen(String name, String phonenumber) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(Constant.NAME, name);
        intent.putExtra(Constant.NUMBER_CARRIER, phonenumber);
        startActivity(intent);
    }

    private void isDevicePresent() {
        TextView devicePresent = findViewById(R.id.devicePresent);
//        if(listOnHomeScreens.isEmpty()) {
//            listView.setVisibility(View.INVISIBLE);
//            devicePresent.setVisibility(View.VISIBLE);
//        }else {
//            devicePresent.setVisibility(View.GONE);
//            listView.setVisibility(View.VISIBLE);
//        }

       List<HomeActivityListData> alldata = null;
        alldata = mDbManager.getAlldata(adminEmail);
        if (alldata.isEmpty()) {
            listView.setVisibility(View.INVISIBLE);
            devicePresent.setVisibility(View.VISIBLE);
        } else {
            devicePresent.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
//        adapter = new HomeActivityListAdapter(alldata);
//        listView.setAdapter(adapter);
//        List<HomeActivityListData> mList = new ArrayList<>();
//        if (!listOnHomeScreens.isEmpty()) {
//            for (ListOnHomeScreen listOnHomeScreenData : listOnHomeScreens) {
//                HomeActivityListData data = new HomeActivityListData();
//                data.setName(listOnHomeScreenData.getName());
//                data.setNumber(listOnHomeScreenData.getRelationWithName());
//                data.setGroupMember(listOnHomeScreenData.isGroupMember());
//                mList.add(data);
//            }
//            adapter = new HomeActivityListAdapter(mList);
//            listView.setAdapter(adapter);
//        }
    }

    public void getServicecall() {
        gotoAddDeviceScreen();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createGroup:
//                checkNumberOfGroups();
                gotoGroupNameActivity();
                break;
            case R.id.track:
                trackDevice();
                break;
            case R.id.addDevice:
                gotoQRScannerScreen();
                break;
            case R.id.addContact:
//                checkNumberOfIndividualUser();
                gotoContactsDetailsActivity();
                break;
            default:
//                Log.d("TAG", "Some other button is clicked");
                break;
        }
    }

    private void checkNumberOfIndividualUser() {
        int individualUserCount = 1;
        if( isComingFromGroupList) {
            isComingFromGroupList = false;
        }
        if(listOnHomeScreens != null) {
            for(HomeActivityListData listOnHomeScreen : listOnHomeScreens) {
                if(listOnHomeScreen.isGroupMember()){
                    individualUserCount ++;
                }
            }
        }
        if(individualUserCount > 10) {
            Util.alertDilogBox(Constant.USER_LIMITATION, "Jio Alert", this);
        }else {
            gotoContactsDetailsActivity();
        }
    }

    private void checkNumberOfGroups() {
        int groupCount = 1;
        if(listOnHomeScreens != null) {
            for(HomeActivityListData listOnHomeScreen : listOnHomeScreens) {
                if(listOnHomeScreen.isGroupMember() == false){
                    groupCount ++;
                }
            }
        }
        if(groupCount > 2) {
            Util.alertDilogBox(Constant.GROUP_LIMITATION, "Jio Alert", this);
            return;
        }else {
            gotoGroupNameActivity();
        }
    }

    private void gotoGroupNameActivity() {
        isAddIndividual = false;
        startActivity(new Intent(this, GroupNameActivity.class));
    }

    private void gotoQRScannerScreen() {
        isAddIndividual = false;
        isComingFromGroupList = false;
        startActivity(new Intent(this, ContactDetailsActivity.class));
    }

    private void gotoContactsDetailsActivity() {
        isAddIndividual = true;
        startActivity(new Intent(this, ContactDetailsActivity.class));
    }

    /***** Track the device ***********/
    private void trackDevice() {
        if (LoginActivity.isAccessCoarsePermissionGranted == false) {
            Util.alertDilogBox(Constant.ACCESS_COARSE_PERMISSION_ALERT, Constant.ALERT_TITLE, this);
        } else if (selectedData.isEmpty()) {
            Util.alertDilogBox(Constant.CHOOSE_DEVICE, Constant.ALERT_TITLE, this);
        } else {
            Util.getInstance().showProgressBarDialog(this);
            AdminLoginData adminLoginDetail = mDbManager.getAdminLoginDetail();
            List<String> data = new ArrayList<>();
            data.add(adminLoginDetail.getUserId());
            SearchDeviceStatusData searchDeviceStatusData = new SearchDeviceStatusData();
            SearchDeviceStatusData.Device device = searchDeviceStatusData.new Device();
            device.setUsersAssigned(data);
            searchDeviceStatusData.setDevice(device);
            RequestHandler.getInstance(getApplicationContext()).handleRequest(new SearchDeviceStatusRequest(new SearchDeviceStatusSuccessListener(), new SearchDeviceStatusErrorListener(), userToken, searchDeviceStatusData));
        }
    }

    /****** Search device status response success listener **************/
    private class SearchDeviceStatusSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            SearchDeviceStatusResponse searchDeviceStatusResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchDeviceStatusResponse.class);
            if (!selectedData.isEmpty()) {
                namingMap.clear();
                for (SearchDeviceStatusResponse.Data data : searchDeviceStatusResponse.getData()) {
                    for (MultipleselectData multipleselectData : selectedData) {
                        if (multipleselectData.getImeiNumber().equalsIgnoreCase(data.getDevice().getImei())) {
                            Map<Double, Double> latLngMap = new HashMap<>();
                            if (data.getLocation() != null) {
                                latLngMap.put(data.getLocation().getLat(), data.getLocation().getLng());
                                namingMap.put(data.getDevice().getName(), latLngMap);
                            }
                        }
                    }
                }
            }
            Util.progressDialog.dismiss();
            if (!namingMap.isEmpty()) {
                goToMapActivity();
            }
        }
    }

    /****** Search device status response error listener **************/
    private class SearchDeviceStatusErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
//            Log.d("TAG", "Error Response");
        }
    }

    private void trackDeviceAfterTimeInterval() {
        AdminLoginData adminLoginDetail = mDbManager.getAdminLoginDetail();
        List<String> data = new ArrayList<>();
        if (adminLoginDetail != null) {
            data.add(adminLoginDetail.getUserId());
            SearchDeviceStatusData searchDeviceStatusData = new SearchDeviceStatusData();
            SearchDeviceStatusData.Device device = searchDeviceStatusData.new Device();
            device.setUsersAssigned(data);
            searchDeviceStatusData.setDevice(device);
            RequestHandler.getInstance(this).handleRequest(new SearchDeviceStatusRequest(new SearchDeviceStatusAfterTimeIntervalSuccessListener(), new SearchDeviceStatusAfterTimeIntervalErrorListener(), userToken, searchDeviceStatusData));
        }
    }

    private class SearchDeviceStatusAfterTimeIntervalSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            SearchDeviceStatusResponse searchDeviceStatusResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchDeviceStatusResponse.class);
            if (!selectedData.isEmpty()) {
                namingMap.clear();
                for (SearchDeviceStatusResponse.Data data : searchDeviceStatusResponse.getData()) {
                    for (MultipleselectData multipleselectData : selectedData) {
                        if (multipleselectData.getImeiNumber().equalsIgnoreCase(data.getDevice().getImei())) {
                            Map<Double, Double> latLngMap = new HashMap<>();
                            if (data.getLocation() != null) {
                                latLngMap.put(data.getLocation().getLat(), data.getLocation().getLng());
                                namingMap.put(data.getDevice().getName(), latLngMap);
                            }
                        }
                    }
                }
            }
        }
    }

    private class SearchDeviceStatusAfterTimeIntervalErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
//            Log.d("TAG", "Error Response");
        }
    }

    /**************** Refresh map thread scheduler **************************/
    public void startTheScheduler() {
        if (thread != null) {
            thread.interrupt();
            thread = new Thread(new RefreshMap());
            thread.start();
        }
    }

    // Refresh Map thread
    public class RefreshMap implements Runnable {
        public void run() {
            while (true) {
                DashboardActivity.this.runOnUiThread(() -> {
                    trackDeviceAfterTimeInterval();
                    new MapsActivity().showMapOnTimeInterval();
                });
                try {
                    Thread.sleep(RefreshIntervalSettingActivity.refreshIntervalTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /******* Connect to the MQTT ************/
    public void makeMQTTConnection() {
        MQTTManager mqttManager = new MQTTManager();
        mqttManager.getMQTTClient(this);
        mqttManager.connetMQTT();
    }

    private void gotoAddDeviceScreen() {
        Intent intent = new Intent(getApplicationContext(), NewDeviceActivity.class);
        startActivity(intent);
    }

    private void goToMapActivity() {
        Util.setLocationFlagStatus(this, true);
        Util.clearAutologinstatus(this);
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    private void sendSMS(String phoneNumber) {
        String userName = mDbManager.getAdminDetail();
        String imei = Util.getInstance().getIMEI(this);
        String consentStatus = mDbManager.getConsentStatusBorqs(phoneNumber);
        if (Constant.CONSENT_STATUS_MSG.equalsIgnoreCase(consentStatus)) {
            Util.alertDilogBox(Constant.CONSENT_APPROVED, Constant.ALERT_TITLE, this);
        } else {
            String phoneNumber1 = null;
            if (subscriptionInfos != null) {
                phoneNumber1 = subscriptionInfos.get(0).getNumber();
            }
            new SendSMSTask().execute(phoneNumber, userName + Constant.CONSENT_MSG_TO_TRACKEE + phoneNumber1.trim().substring(2, phoneNumber1.length()) + "&" + userName + "&" + imei);
            Toast.makeText(this, Constant.CONSENT_MSG_SENT, Toast.LENGTH_SHORT).show();
            mDbManager.updatependingConsent(phoneNumber);
        }
    }

    public void consentRequestBox(boolean flag, String name) {
        if (flag) {
            Util.alertDilogBox(Constant.START_TRACKING + name + Constant.REQUEST_CONSENT_USER, "Alert", this);
            flag = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDevicePresent();
    }

    /************  Class to calculate the signal strength ***********************/
    class MyPhoneStateListener extends PhoneStateListener {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission not granted");
                }
            }
            if (LoginActivity.isAccessCoarsePermissionGranted) {
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


    public void checkPermission() {
        int fineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int fineLocationCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int readphoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (readphoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (fineLocationCoarse != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        } else {
            subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                // If request is cancelled, the result arrays are empty.
                for (int grantResult : grantResults) {
                    if (grantResults.length > 0 && grantResult == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission granted");
                    }
                }
            }
            default:
//                Log.d("TAG", "Something went wrong");
                break;
        }
    }

    public class SendLocation implements Runnable {
        public void run() {
            while (true) {
                makeMQTTConnection();
                publishMessage();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void storeConsentTime(String phoneNumber, int approvalTime) {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDateandTime = sdf.format(currentTime);
        mDbManager.updateConsentTime(phoneNumber, currentDateandTime.trim(), approvalTime);
    }

    public void setConstaint() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresStorageNotLow(false)
                .build();

        PeriodicWorkRequest refreshWork =
                new PeriodicWorkRequest.Builder(ConsentTimeUpdate.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();
        mWorkManager.enqueue(refreshWork);
    }

    public void registerReceiver() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(getString(R.string.customintent));
        registerReceiver(mRecevier, intent);
    }

    public BroadcastReceiver mRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            showDatainList();
            boolean flag = intent.getBooleanExtra("TokenFlag", false);
            if (flag) {
                Util.clearAutologinstatus(DashboardActivity.this);
                mDbManager.updateLogoutData();
                goToLoginActivity();
            }
        }
    };


    private void deepLinkingURICheck() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.toString().contains(getString(R.string.yesjff))) {
            String number = data.toString().substring(data.toString().length() - 10);
            mDbManager.updateConsentInBors(number, Constant.CONSENT_STATUS_MSG);
//            showDatainList();
        } else if (data != null && data.toString().contains(getString(R.string.nojff))) {
            String number = data.toString().substring(data.toString().length() - 10);
            mDbManager.updateConsentInBors(number, Constant.NO_JIO_TRACKER);
        }
    }

    private void showSpinnerforConsentTime(String phoneNumber) {
        String time[] = {Constant.MIN_15, Constant.MIN_25, Constant.MIN_30, Constant.MIN_40};
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_consent_time);
        dialog.setTitle(Constant.DIALOG_TITLE);
        dialog.getWindow().setLayout(1000, 500);
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, time);
        Spinner spinner = dialog.findViewById(R.id.consentSpinner);
        Button close = dialog.findViewById(R.id.close);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int consentTimeApproval;
                String value = (String) parent.getItemAtPosition(position);
                if (value.contains("15")) {
                    consentTimeApproval = 14;
                } else if (value.contains("25")) {
                    consentTimeApproval = 23;
                } else if (value.contains("30")) {
                    consentTimeApproval = 28;

                } else {
                    consentTimeApproval = 38;
                }
                storeConsentTime(phoneNumber, consentTimeApproval);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // To do
            }
        });
        close.setOnClickListener(v -> {
            sendSMS(phoneNumber);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void addDataInHomeScreen() {
//        List<HomeActivityListData> mList = new ArrayList<>();
//        if (!listOnHomeScreens.isEmpty()) {
//            for (ListOnHomeScreen listOnHomeScreenData : listOnHomeScreens) {
//                HomeActivityListData data = new HomeActivityListData();
//                data.setName(listOnHomeScreenData.getName());
//                data.setNumber(listOnHomeScreenData.getRelationWithName());
//                data.setGroupMember(listOnHomeScreenData.isGroupMember());
//                mList.add(data);
//            }
//            adapter = new HomeActivityListAdapter(mList);
//            listView.setAdapter(adapter);
//        }
        List<HomeActivityListData> alldata = null;
        alldata = mDbManager.getAllDevicedata(adminEmail);
        adapter = new TrackerDeviceListAdapter(this,alldata);
        listView.setAdapter(adapter);
    }
}