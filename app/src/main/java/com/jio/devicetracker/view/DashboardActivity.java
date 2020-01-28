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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jio.devicetracker.R;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import com.jio.devicetracker.database.pojo.FMSAPIData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.TrackerdeviceData;
import com.jio.devicetracker.database.pojo.request.FMSTrackRequest;
import com.jio.devicetracker.database.pojo.request.TrackdeviceRequest;
import com.jio.devicetracker.database.pojo.response.LocationApiResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.TrackerDeviceListAdapter;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    public static String trackeeIMEI = null;
    private String trackeeName = "";
    private DashboardActivity dashboardActivity = null;
    public static String adminEmail;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView user_account_name = null;
    Locale locale = Locale.ENGLISH;

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
        MessageReceiver.bindListener(this);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
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
        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
        if (!hasPermissions(this, permissions)) {
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
                    checkConsentPublishMQTTMessage(data);
                    Log.d(TAG, "Value of data" + selectedData.size());
                } else {
                    for (MultipleselectData multipleselectData : selectedData) {
                        if (multipleselectData.getPhone().equalsIgnoreCase(data.getPhone())) {
                            selectedData.remove(multipleselectData); // TODO include break after this if you want to stop loop once the device is deleted
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
        userToken = adminLoginData.getUserToken();
        adminEmail = adminLoginData.getEmail();
    }

    private void checkConsentPublishMQTTMessage(MultipleselectData multipleselectData) {
        Util util = Util.getInstance();
        MQTTManager mqttManager = new MQTTManager();
        List<AddedDeviceData> consentData = mDbManager.getAlldataFromFMS();
        for (AddedDeviceData consent : consentData) {
            if (consent.getPhoneNumber().equalsIgnoreCase(multipleselectData.getPhone()) && consent.getConsentStaus().trim().equalsIgnoreCase("yes jiotracker")) {
                String topic = "jioiot/svckm/jiophone/" + util.getIMEI(getApplicationContext()) + "/uc/fwd/sesid";
                Log.d("Topic --> ", topic);
                trackeeIMEI = consent.getImeiNumber();
                String message = "{\"reqId\":\"" + consent.getImeiNumber() + "\",\"trkId\":\"" + util.getIMEI(getApplicationContext()) + "\",\"sesId\":\"" + Util.getInstance().getSessionId() + "\",\"trknr\":\"" + RegistrationActivity.phoneNumber.substring(2) + "\",\"reqnr\":\"" + consent.getPhoneNumber() + "\"}";
                Log.d("Message --> ", message);
                mqttManager.publishMessage(topic, message);
            }
        }
    }

    public DashboardActivity getDashboardActivity() {
        if (dashboardActivity == null) {
            dashboardActivity = new DashboardActivity();
            return dashboardActivity;
        }
        return dashboardActivity;
    }

    private void gotoEditScreen(String relation, String phoneNumber) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("number", phoneNumber);
        intent.putExtra("Relation", relation);
        startActivity(intent);
    }

    private void isDevicePresent() {
        DBManager dbManager = new DBManager(getApplicationContext());
        devicePresent = findViewById(R.id.devicePresent);
        List<AddedDeviceData> alldata = null;
        if (RegistrationActivity.isFMSFlow == false) {
            alldata = dbManager.getAlldata(adminEmail);
            if (alldata.isEmpty()) {
                listView.setVisibility(View.INVISIBLE);
                devicePresent.setVisibility(View.VISIBLE);
            } else {
                devicePresent.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } else {
            alldata = dbManager.getAlldataFromFMS();
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
        if (RegistrationActivity.isFMSFlow == true) {
            gotoAddDeviceActivity();
        } else {
            showProgressBarDialog();
            TrackerdeviceData data = new TrackerdeviceData();
            TrackerdeviceData.StartsWith startsWith = data.new StartsWith();
            startsWith.setCurrentDate(Util.convertTimeToEpochtime());
            data.setStartsWith(startsWith);
            devicePresent.setVisibility(View.GONE);
            RequestHandler.getInstance(getApplicationContext()).handleRequest(new TrackdeviceRequest(new SuccessListener(), new ErrorListener(), token, data));
        }
    }


    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            mtrackerresponse = Util.getInstance().getPojoObject(String.valueOf(response), TrackerdeviceResponse.class);
            data = mtrackerresponse.getmData();
            Log.d(TAG, "Response print" + response);
            gotoAddDeviceScreen(data);
            progressDialog.dismiss();
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            //Toast.makeText(getApplicationContext(), "Token is null", Toast.LENGTH_SHORT).show();
        }
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


    private void trackDevice() {
        boolean flag = false;
        if (RegistrationActivity.isFMSFlow == false) {
            List<AddedDeviceData> consentData = mDbManager.getAlldata(adminEmail);
            if (selectedData.isEmpty()) {
                Util.alertDilogBox(Constant.CHOOSE_DEVICE, Constant.ALERT_TITLE, this);
                return;
            }
            for(AddedDeviceData addedDeviceData : consentData) {
                for(MultipleselectData multipleselectData : selectedData) {
                    if(addedDeviceData.getPhoneNumber().equalsIgnoreCase(multipleselectData.getPhone())){
                        if(addedDeviceData.getConsentStaus().trim().equalsIgnoreCase("Yes JioTracker")){
                            latLngMap = mDbManager.getLatLongForMap(selectedData, addedDeviceData.getPhoneNumber());
                            namingMap.put(multipleselectData.getName(), latLngMap);
                            flag = true;
                        } else {
                            Util.alertDilogBox(Constant.CONSENT_NOTAPPROVED + addedDeviceData.getPhoneNumber(), Constant.ALERT_TITLE, this);
                            flag = false;
                            return;
                        }
                    }
                }
            }
            if (flag) {
                startTheScheduler();
                Intent map = new Intent(this, MapsActivity.class);
                startActivity(map);
            }
        } else {
            showProgressBarDialog();
            List<String> imeiNumbers = new ArrayList<>();
            List<String> phoneNumbers = new ArrayList<>();
            List<AddedDeviceData> consentData = mDbManager.getAlldataFromFMS();
            if (selectedData.isEmpty()) {
                Util.alertDilogBox("Please select the number for tracking", "Jio Alert", this);
                return;
            }
            for (int i = 0; i < consentData.size(); i++) {
                if (selectedData.get(i).getPhone().equals(consentData.get(i).getPhoneNumber())) {
                    if (consentData.get(i).getConsentStaus().toLowerCase(locale).contains("yes jiotracker")) {
                        imeiNumbers.add(consentData.get(i).getImeiNumber());
                        phoneNumbers.add(consentData.get(i).getPhoneNumber());
                        trackeeName = consentData.get(i).getName();
                    } else {
                        Util.alertDilogBox("Consent is not apporoved for phone number " + consentData.get(i).getPhoneNumber(), "Jio Alert", this);
                    }
                }
            }

            FMSAPIData fmsapiData = new FMSAPIData();
            fmsapiData.setEvt(new String[]{"GPS"});
            fmsapiData.setDvt("SIM");
            fmsapiData.setImi(imeiNumbers);
            fmsapiData.setEfd(14546957247L);
            fmsapiData.setMob(phoneNumbers);
            fmsapiData.setTid("456");
            RequestHandler.getInstance(getApplicationContext()).handleFMSRequest(new FMSTrackRequest(new FMSSuccessListener(), new FMSErrorListener(), fmsapiData));
        }
    }

    private class FMSSuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            LocationApiResponse fmsAPIResponse = Util.getInstance().getPojoObject(String.valueOf(response), LocationApiResponse.class);
            fmsLatLngMap.put(Double.parseDouble(fmsAPIResponse.new Event().getLatitute()), Double.parseDouble(fmsAPIResponse.new Event().getLongnitute()));
            fmsNamingMap.put(trackeeName, fmsLatLngMap);
            progressDialog.dismiss();
            startActivity(new Intent(DashboardActivity.this, FMSMapActivity.class));
        }
    }

    private class FMSErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Map<Double, Double> hashMap = new HashMap<>();
            hashMap.put(12.976786, 77.593926);
            fmsNamingMap.put("JIO Location", hashMap);
            progressDialog.dismiss();
            startActivity(new Intent(DashboardActivity.this, FMSMapActivity.class));
            Toast.makeText(getApplicationContext(), Constant.FMS_SERVERISSUE, Toast.LENGTH_SHORT).show();
        }
    }

    public void startTheScheduler() {
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            Log.i("TAG", "Inside Scheduler");
            this.runOnUiThread(() -> {
                makeAPICall(userToken);
                new MapsActivity().showMapOnTimeInterval();
            });
        }, 0, MapsActivity.refreshIntervalTime, TimeUnit.SECONDS);
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

    private void gotoAddDeviceActivity() {
        startActivity(new Intent(getApplicationContext(), NewDeviceActivity.class));
    }

    private void gotoAddDeviceScreen(List<TrackerdeviceResponse.Data> mData) {
        Intent intent = new Intent(getApplicationContext(), NewDeviceActivity.class);
        intent.putExtra("DeviceData", (Serializable) mData);
        startActivity(intent);
    }

    private void sendSMS(String phoneNumber) {
        String userName = mDbManager.getAdminDetail();
        String consentStatus = mDbManager.getConsentStatusBorqs(phoneNumber);
        if ("Yes JioTracker".equalsIgnoreCase(consentStatus)) {
            Util.alertDilogBox(Constant.CONSENT_APPROVED, Constant.ALERT_TITLE, this);
        } else {
            new SendSMSTask().execute(phoneNumber, userName + " from JioTracker application wants to track your location, please reply with \"Yes JioTracker\" or \"No JioTracker !");
            Toast.makeText(this, "Consent sent", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "Received message -> " + message + " from phone number -> " + phoneNum, Toast.LENGTH_SHORT).show();
        Log.d("Received Message --> ", message);
        String phone = phoneNum.substring(3);
        if (!RegistrationActivity.isFMSFlow) {
            mDbManager.updateConsentInBors(phone, message.toLowerCase(locale).trim());
            showDatainList();
            /*if (RegistrationDetailActivity.phoneNumber != null && message.length() == 4) {
                otpNumber = message;
                BorqsOTPActivity.phoneOTP.setText(otpNumber);
            }*/
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
        progressDialog = ProgressDialog.show(this, "", Constant.LOADING_DATA, true);
        progressDialog.setCancelable(true);
    }

    public void alertDilogBoxWithCancelbtn(String message, String title, String phoneNumber, int position) {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        //adb.setView(alertDialogView);
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

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDevicePresent();
    }
}