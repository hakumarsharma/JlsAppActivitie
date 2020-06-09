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

package com.jio.devicetracker.view.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.ApproveRejectConsentData;
import com.jio.devicetracker.database.pojo.request.ApproveConsentRequest;
import com.jio.devicetracker.database.pojo.request.RejectConsentRequest;
import com.jio.devicetracker.database.pojo.response.ApproveRejectAPIResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.HowToUseActivity;
import com.jio.devicetracker.view.adapter.DashboardAdapter;
import com.jio.devicetracker.view.device.QRReaderInstruction;
import com.jio.devicetracker.view.group.CreateGroupActivity;
import com.jio.devicetracker.view.menu.NavigateSupportActivity;
import com.jio.devicetracker.view.menu.NavigateUserProfileActivity;
import com.jio.devicetracker.view.people.AddPeopleActivity;
import com.jio.devicetracker.view.signinsignup.SigninSignupActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardMainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private ViewPager viewPager;
    private DashboardAdapter dashboardAdapter;
    private ImageView addGroupInDashboard;
    private DrawerLayout drawerLayout;
    private static DBManager mDbManager;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private Location currentLocation;
    private GoogleApiClient googleApiClient;
    private static int signalStrengthValue;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private static Double latitude;
    private static Double longitude;
    private String userPhoneNumber;
    private static int batteryLevel;
    private String consentId;
    private TextView groupTitle;
    private TextView peopleTitle;
    private TextView deviceTitle;
    private TextView manualTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_main);
        Toolbar toolbar = findViewById(R.id.dashboardToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        mDbManager = new DBManager(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        title.setText(Constant.DASHBOARD_TITLE);
        title.setTypeface(Util.mTypeface(this, 5));
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        toggle.setDrawerIndicatorEnabled(false);

        toggle.setHomeAsUpIndicator(R.drawable.ic_more);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setNavigationData();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        initUI();
        initializeDataMembers();
        Util.getInstance().setAutologinStatus(this, true);
    }

    private void initUI() {
        groupTitle = findViewById(R.id.group_detail);
        peopleTitle = findViewById(R.id.people_detail);
        deviceTitle = findViewById(R.id.device_detail);
        groupTitle.setOnClickListener(this);
        peopleTitle.setOnClickListener(this);
        deviceTitle.setOnClickListener(this);
        groupTitle.setText("Groups\n" + Html.fromHtml(getResources().getString(R.string.white_indicater)));
        groupTitle.setTypeface(Util.mTypeface(this, 5));
        peopleTitle.setTypeface(Util.mTypeface(this, 5));
        deviceTitle.setTypeface(Util.mTypeface(this, 5));
        viewPager = findViewById(R.id.viewPager);
        addGroupInDashboard = findViewById(R.id.createGroup);
        addGroupInDashboard.setVisibility(View.VISIBLE);
        addGroupInDashboard.setOnClickListener(this);
        dashboardAdapter = new DashboardAdapter(getSupportFragmentManager(), 3);
        viewPager.setAdapter(dashboardAdapter);
        pageChangeListener();
        deepLinkingURICheck();
    }


    /**
     * Called when you change the page
     */
    private void pageChangeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // To do
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    groupTitle.setText("Groups\n" + Html.fromHtml(getResources().getString(R.string.white_indicater)));
                    groupTitle.setTextColor(Color.WHITE);
                    peopleTitle.setText("People");
                    peopleTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                    deviceTitle.setText("Devices");
                    deviceTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                } else if (position == 1) {

                    peopleTitle.setText("People\n" + Html.fromHtml(getResources().getString(R.string.white_indicater)));
                    peopleTitle.setTextColor(Color.WHITE);
                    groupTitle.setText("Groups");
                    groupTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                    deviceTitle.setText("Devices");
                    deviceTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                } else {
                    deviceTitle.setText("Devices\n" + Html.fromHtml(getResources().getString(R.string.white_indicater)));
                    deviceTitle.setTextColor(Color.WHITE);
                    peopleTitle.setText("People");
                    peopleTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                    groupTitle.setText("Groups");
                    groupTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // To do
            }
        });
    }


    private void initializeDataMembers() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(broadcastreceiver, intentFilter);
        setUpGClient();
        new Thread(new SendLocation()).start();
        userPhoneNumber = mDbManager.getAdminLoginDetail().getPhoneNumber();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createGroup) {
            switch (viewPager.getCurrentItem()) {
                case 0:
                    startActivity(new Intent(this, CreateGroupActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(this, AddPeopleActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(this, QRReaderInstruction.class));
                    break;
            }
        }
        if (v.getId() == R.id.group_detail) {
            viewPager.setCurrentItem(0);
            groupTitle.setText("Groups\n" + Html.fromHtml(getResources().getString(R.string.white_indicater)));
            groupTitle.setTextColor(Color.WHITE);
            peopleTitle.setText("People");
            peopleTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
            deviceTitle.setText("Devices");
            deviceTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
        }
        if (v.getId() == R.id.people_detail) {
            viewPager.setCurrentItem(1);
            peopleTitle.setText("People\n" + Html.fromHtml(getResources().getString(R.string.white_indicater)));
            peopleTitle.setTextColor(Color.WHITE);
            groupTitle.setText("Groups");
            groupTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
            deviceTitle.setText("Devices");
            deviceTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
        }
        if (v.getId() == R.id.device_detail) {
            viewPager.setCurrentItem(2);
            deviceTitle.setText("Devices\n" + Html.fromHtml(getResources().getString(R.string.white_indicater)));
            deviceTitle.setTextColor(Color.WHITE);
            peopleTitle.setText("People");
            peopleTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
            groupTitle.setText("Groups");
            groupTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
        }
    }

    /**
     * Deep Link URI Check, gets called when you click on Deep link URI and
     * it comes back to the Home screen, not login page
     */
    private void deepLinkingURICheck() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            showDialog(data);
        }
    }

    /**
     * Display Dialog when you click on Deep link URI
     */
    public void showDialog(Uri data) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_display_dialog);
        dialog.setTitle(Constant.TITLE);
        dialog.getWindow().setLayout(1000, 500);
        final Button yes = dialog.findViewById(R.id.positive);
        final Button no = dialog.findViewById(R.id.negative);
        yes.setOnClickListener(v -> {
            if (data != null && data.toString().contains(getString(R.string.approveURI))) {
                approveConsentRequestAPICall(data.toString().substring(data.toString().length() - 5, data.toString().length()), data.toString().substring(data.toString().indexOf(Constant.CONSENT_ID) + 10, data.toString().indexOf("&")));
            }
            dialog.dismiss();
        });

        no.setOnClickListener(v -> {
            if (data != null && data.toString().contains(getString(R.string.approveURI))) {
                rejectConsentRequestAPICall(data.toString().substring(data.toString().length() - 5, data.toString().length()), data.toString().substring(data.toString().indexOf(Constant.CONSENT_ID) + 10, data.toString().indexOf("&")));
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * Approve Consent Request API Call
     *
     * @param consentId
     * @param token
     */
    private void approveConsentRequestAPICall(String token, String consentId) {
        this.consentId = consentId;
        ApproveRejectConsentData approveRejectConsentData = new ApproveRejectConsentData();
        ApproveRejectConsentData.Consent consent = new ApproveRejectConsentData().new Consent();
        ApproveRejectConsentData.Token tokenData = new ApproveRejectConsentData().new Token();
        tokenData.setValue(token);
        consent.setStatus(Constant.APPROVED);
        consent.setToken(tokenData);
        approveRejectConsentData.setConsent(consent);
        GroupRequestHandler.getInstance(this).handleRequest(new ApproveConsentRequest(new ApproveConsentRequestSuccessListener(), new ApproveConsentRequestErrorListener(), approveRejectConsentData, consentId));
    }

    /**
     * Approve Consent Request Success Listener
     */
    private class ApproveConsentRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            ApproveRejectAPIResponse approveRejectAPIResponse = Util.getInstance().getPojoObject(String.valueOf(response), ApproveRejectAPIResponse.class);
            if (approveRejectAPIResponse.getCode() == 200) {
                mDbManager.updateConsentInGroupMemberTable(consentId, Constant.APPROVED);
                Toast.makeText(DashboardMainActivity.this, Constant.CONSENT_APPROVED_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Approve Consent Request error Listener
     */
    private class ApproveConsentRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(DashboardMainActivity.this, Constant.CONSENT_NOT_APPROVED_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Reject Consent Request API Call
     */
    private void rejectConsentRequestAPICall(String token, String consentId) {
        this.consentId = consentId;
        ApproveRejectConsentData approveRejectConsentData = new ApproveRejectConsentData();
        ApproveRejectConsentData.Consent consent = new ApproveRejectConsentData().new Consent();
        ApproveRejectConsentData.Token tokenData = new ApproveRejectConsentData().new Token();
        tokenData.setValue(token);
        consent.setStatus(Constant.REJECTED);
        consent.setToken(tokenData);
        approveRejectConsentData.setConsent(consent);
        GroupRequestHandler.getInstance(this).handleRequest(new RejectConsentRequest(new RejectConsentRequestSuccessListener(), new RejectConsentRequestErrorListener(), approveRejectConsentData, consentId));
    }

    /**
     * Reject Consent Request API Success Listener
     */
    private class RejectConsentRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            ApproveRejectAPIResponse approveRejectAPIResponse = Util.getInstance().getPojoObject(String.valueOf(response), ApproveRejectAPIResponse.class);
            if (approveRejectAPIResponse.getCode() == 200) {
                mDbManager.updateConsentInGroupMemberTable(consentId, Constant.REJECTED);
                Toast.makeText(DashboardMainActivity.this, Constant.CONSENT_REJECTED_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Reject Consent Request API error Listener
     */
    private class RejectConsentRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(DashboardMainActivity.this, Constant.CONSENT_NOT_REJECTED_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    private void setNavigationData() {
        NavigationView navigationView = findViewById(R.id.nv);
        View header = navigationView.getHeaderView(0);
        ImageView profileIcn = header.findViewById(R.id.profileIcon);
        TextView userAccountName = header.findViewById(R.id.user_account_name);
        TextView userPhoneNumber = header.findViewById(R.id.user_number);
        RelativeLayout logoutLayout = findViewById(R.id.logout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLogoutData();
            }
        });
        if (!Util.userName.isEmpty() && !Util.userNumber.isEmpty()) {
            userAccountName.setText(Util.userName.substring(0, 1).toUpperCase(Locale.ROOT) + Util.userName.substring(1));
            userPhoneNumber.setText(Util.userNumber);
        }
        ImageView backDrawer = header.findViewById(R.id.back);
        backDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });
        profileIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNavigateUserProfileActivity(userAccountName.getText().toString(), userPhoneNumber.getText().toString());
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.notification:
                        //gotoNotificationActivity();
                        break;
                    case R.id.settings:
                        //gotoSettingsActivity();
                        break;
                    case R.id.howtoadd:
                        goToHowtoUseActivity();
                        break;
                    case R.id.support:
                        startActivity(new Intent(DashboardMainActivity.this, NavigateSupportActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void gotoNavigateUserProfileActivity(String name, String number) {
        Intent intent = new Intent(this, NavigateUserProfileActivity.class);
        intent.putExtra("Name", name);
        intent.putExtra("Number", number);
        startActivity(intent);
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

    /**
     * Navigates to the Login Activity
     */
    private void goToLoginActivity() {
        startActivity(new Intent(this, SigninSignupActivity.class));
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getCurrentLocation();
            MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
            TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
            TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    /**
     * Class to calculate the signal strength
     */
    class MyPhoneStateListener extends PhoneStateListener {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            TelephonyManager mTelephonyManager = (TelephonyManager) DashboardMainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission not granted");
                }
            }
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

    /**
     * Location Google API Connect
     */
    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    /**
     * Send location to the borqs every 15 seconds
     */
    public class SendLocation implements Runnable {
        public void run() {
            while (true) {
                try {
                    makeMQTTConnection();
                    Thread.sleep(30000);
                    publishMessage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Publish the MQTT message along with battery level, signal strength and time format
     */
    public void publishMessage() {
        if (getCurrentLocation() != null) {
            latitude = getCurrentLocation().getLatitude();
            longitude = getCurrentLocation().getLongitude();
            String message = "{\"imi\":\"" + userPhoneNumber + "\",\"evt\":\"GPS\",\"dvt\":\"JioDevice_g\",\"alc\":\"0\",\"lat\":\"" + latitude + "\",\"lon\":\"" + longitude + "\",\"ltd\":\"0\",\n" +
                    "\"lnd\":\"0\",\"dir\":\"0\",\"pos\":\"A\",\"spd\":\"" + 12 + "\",\"tms\":\"" + Util.getInstance().getMQTTTimeFormat() + "\",\"odo\":\"0\",\"ios\":\"0\",\"bat\":\"" + batteryLevel + "\",\"sig\":\"" + signalStrengthValue + "\"}";
            String topic = "jioiot/svcd/tracker/" + userPhoneNumber + "/uc/fwd/locinfo";
            System.out.println("Message --> " + message);
            System.out.println("Topic -->" + topic);
            new MQTTManager().publishMessage(topic, message);
        }
    }

    /**
     * Broadcast receiver to calculate battery strength
     */
    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryLevel = (int) (((float) level / (float) scale) * 100.0f);
        }
    };

    /**
     * Connect to the MQTT server
     */
    public void makeMQTTConnection() {
        MQTTManager mqttManager = new MQTTManager();
        mqttManager.getMQTTClient(this);
        mqttManager.connetMQTT();
    }

    /**
     * Captures the current Location and then it returns
     */
    private Location getCurrentLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this, Looper.getMainLooper());
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(DashboardMainActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        currentLocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(DashboardMainActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                                default:
                                    // To do
                                    break;
                            }
                        }
                    });
                }
            }
        }
        return currentLocation;
    }

    private void goToHowtoUseActivity() {
        startActivity(new Intent(this, HowToUseActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastreceiver != null) {
            unregisterReceiver(broadcastreceiver);
        }
    }

}
