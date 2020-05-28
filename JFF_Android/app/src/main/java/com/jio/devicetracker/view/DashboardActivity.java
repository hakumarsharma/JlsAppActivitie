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

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
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
import com.jio.devicetracker.R;

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
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddMemberInGroupData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.ApproveRejectConsentData;
import com.jio.devicetracker.database.pojo.CreateGroupData;
import com.jio.devicetracker.database.pojo.GroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.SearchDeviceStatusData;
import com.jio.devicetracker.database.pojo.SearchEventData;
import com.jio.devicetracker.database.pojo.request.AddMemberInGroupRequest;
import com.jio.devicetracker.database.pojo.request.ApproveConsentRequest;
import com.jio.devicetracker.database.pojo.request.CreateGroupRequest;
import com.jio.devicetracker.database.pojo.request.DeleteGroupRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.request.RejectConsentRequest;
import com.jio.devicetracker.database.pojo.request.SearchDeviceStatusRequest;
import com.jio.devicetracker.database.pojo.request.SearchEventRequest;
import com.jio.devicetracker.database.pojo.response.ApproveRejectAPIResponse;
import com.jio.devicetracker.database.pojo.response.CreateGroupResponse;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.database.pojo.response.SearchEventResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.ConsentTimeUpdate;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.TrackerDeviceListAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;


/**
 * Implementation of Dashboard Screen to show the trackee list and hamburger menu.
 */
public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, MessageListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static RecyclerView listView;
    public static List<MultipleselectData> selectedData;
    public static List<TrackerdeviceResponse.Data> data;
    private String groupMemberName;
    private static TrackerDeviceListAdapter adapter;
    public static List<String> consentListPhoneNumber = null;
    private static DBManager mDbManager;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView user_account_name = null;
    private Locale locale = Locale.ENGLISH;
    private static int batteryLevel;
    private DrawerLayout drawerLayout;
    private static Double latitude;
    private static Double longitude;
    private static int signalStrengthValue;
    private WorkManager mWorkManager = null;
    private static Thread thread = null;
    public static List<GroupData> specificGroupMemberData = null;
    public static List<HomeActivityListData> listOnHomeScreens;
    public static String groupName = "";
    private static String userId;
    private String grpId;
    private String userPhoneNumber;
    private String consentId;
    private int listPosition;
    private List listOnDashBoard;
    public static List<GroupMemberDataList> grpMemberDataList;
    public static List<HomeActivityListData> grpDataList;
    private static TextView devicePresent;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private int counter = 0;
    private List<MapData> mapDataList;
    private List<GroupMemberDataList> mGroupMemberList;
    private HomeActivityListData mHomeActivityListData;
    private boolean isConsentButtonClicked;
    private GroupMemberDataList mGroupMemberDataList;
    private boolean isConsentButtonClickedForGroupMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setLayoutData();
        setNavigationData();
        initializeDataMember();
//        Util.getAdminDetail(this);
        setConstaint();
//        startService();
        registerReceiver();
        deepLinkingURICheck();
        makeGroupInfoPerUserRequestAPICall();
    }

    /**
     * Initialize all the data members
     */
    private void initializeDataMember() {
        selectedData = new ArrayList<>();
        mWorkManager = WorkManager.getInstance();
        consentListPhoneNumber = new LinkedList<>();
        mapDataList = new ArrayList<>();
        Util.getInstance().getIMEI(this);
        mDbManager = new DBManager(this);
        thread = new Thread(new RefreshMap());
        //thread.start();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(broadcastreceiver, intentFilter);
        setUpGClient();
        userId = mDbManager.getAdminLoginDetail().getUserId();
        userPhoneNumber = mDbManager.getAdminLoginDetail().getPhoneNumber();
        new Thread(new SendLocation()).start();
        grpMemberDataList = new CopyOnWriteArrayList<>();
        grpDataList = new CopyOnWriteArrayList<>();
        devicePresent = findViewById(R.id.devicePresent);
        MessageListener messageListener = new DashboardActivity();
        MessageReceiver.bindListener(messageListener);
        if (specificGroupMemberData == null) {
            specificGroupMemberData = new ArrayList<>();
        }
        if (listOnHomeScreens == null) {
            listOnHomeScreens = new ArrayList<>();
        }
    }

    /**
     * Sets Navigation data
     */
    private void setNavigationData() {
        NavigationView navigationView = findViewById(R.id.nv);
        View header = navigationView.getHeaderView(0);
        user_account_name = header.findViewById(R.id.user_account_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(item -> {
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
                case R.id.activeSessions:
                    gotoActiveSessionActivity();
                    break;
                case R.id.home:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    break;
                case R.id.logout:
                    updateLogoutData();
                    break;
                default:
                    return true;
            }
            return true;
        });
    }

    /**
     * Adapter Listener
     */
    private void adapterEventListener() {
        if (adapter != null) {
            adapter.setOnItemClickPagerListener(new TrackerDeviceListAdapter.RecyclerViewClickListener() {
                @Override
                public void recyclerViewListClicked(View v, int position, MultipleselectData data, int val) {
                    if (val == 2) {
                        selectedData.add(data);
                    } else if (val == 3) {
                        Iterator<MultipleselectData> iterator = selectedData.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().getPhone().equalsIgnoreCase(data.getPhone())) {
                                iterator.remove();
                            }
                        }
                    } else {
                        List<MultipleselectData> groupData = mDbManager.getGroupLatLongdata(data.getName());
                        for (MultipleselectData groupDataLatLon : groupData) {
                            selectedData.add(groupDataLatLon);
                        }
                    }
                }

                @Override
                public void clickonListLayout(String selectedGroupName, String groupId, int profileImage) {
                    if (profileImage != R.drawable.ic_user) {
                        groupName = selectedGroupName;
                        goToAddDeviceActivity(groupId);
                    }
                }

                @Override
                public void consentClickForGroupMember(GroupMemberDataList groupMemberDataList) {
                    mGroupMemberList = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupMemberDataList.getGroupId());
                    isConsentButtonClicked = false;
                    isConsentButtonClickedForGroupMember = true;
                    grpId = groupMemberDataList.getGroupId();
                    mGroupMemberDataList = groupMemberDataList;
                    makeDeleteGroupAPICall(groupMemberDataList.getGroupId());
                }

                // Delete the existing group create new group wih same details
                @Override
                public void consentButtonClickForGroup(HomeActivityListData homeActivityListData) {
                    mHomeActivityListData = homeActivityListData;
                    grpId = homeActivityListData.getGroupId();
                    mGroupMemberList = mDbManager.getAllGroupMemberDataBasedOnGroupId(homeActivityListData.getGroupId());
                    makeDeleteGroupAPICall(homeActivityListData.getGroupId());
                    isConsentButtonClicked = true;
                    isConsentButtonClickedForGroupMember = false;
                }

                @Override
                public void checkBoxClickedForGroupMember(GroupMemberDataList mDataList) {
                    grpMemberDataList.add(mDataList);
                }

                @Override
                public void checkBoxClickedForGroup(HomeActivityListData homeActivityListData) {
                    grpId = homeActivityListData.getGroupId();
                    grpDataList.add(homeActivityListData);
                }

                @Override
                public void onPopupMenuClicked(View v, int position, String name, String number, String groupId, String isGroupMember) {
                    PopupMenu popup = new PopupMenu(DashboardActivity.this, v);
                    popup.inflate(R.menu.options_menu);
                    popup.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.editOnCardView:
                                gotoEditScreen(name, number, groupId, isGroupMember);
                                break;
                            case R.id.deleteOnCardView:
                                alertDilogBoxWithCancelbtn(Constant.DELETC_DEVICE, Constant.ALERT_TITLE, groupId);
                                grpId = groupId;
                                listPosition = position;
                                break;
                            default:
                                break;
                        }
                        return false;
                    });
                    popup.show();
                }
            });
        }
    }

    /**
     * Create Group API call
     */
    private void createGroupAPICall(String groupName) {
        CreateGroupData createGroupData = new CreateGroupData();
        createGroupData.setName(groupName);
        createGroupData.setType("one_to_one");
        CreateGroupData.Session session = new CreateGroupData().new Session();
        session.setFrom(Util.getInstance().getTimeEpochFormatAfterCertainTime(1));
        session.setTo(Util.getInstance().getTimeEpochFormatAfterCertainTime(2));
        createGroupData.setSession(session);
        GroupRequestHandler.getInstance(this).handleRequest(new CreateGroupRequest(new CreateGroupSuccessListener(), new CreateGroupErrorListener(), createGroupData, mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * Create Group Success Listener
     */
    private class CreateGroupSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            CreateGroupResponse createGroupResponse = Util.getInstance().getPojoObject(String.valueOf(response), CreateGroupResponse.class);
            mDbManager.insertIntoGroupTable(createGroupResponse);
            addUsersInsideGroup(createGroupResponse.getData().getId());
        }
    }

    /**
     * Create Group error Listener
     */
    private class CreateGroupErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.REQUEST_CONSENT_FAILED, Constant.ALERT_TITLE, DashboardActivity.this);
        }
    }

    /**
     * Adds all the existing user inside the group
     *
     * @param groupId
     */
    private void addUsersInsideGroup(String groupId) {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        if (isConsentButtonClicked && ! mGroupMemberList.isEmpty()) {
            for (GroupMemberDataList groupMemberDataList : mGroupMemberList) {
                AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
                List<String> mList = new ArrayList<>();
                mList.add(Constant.EVENTS);
                consents.setEntities(mList);
                consents.setPhone(groupMemberDataList.getNumber());
                consents.setName(groupMemberDataList.getName());
                consentList.add(consents);
            }
        } else if (isConsentButtonClickedForGroupMember && ! mGroupMemberList.isEmpty()) {
            for (GroupMemberDataList groupMemberDataList : mGroupMemberList) {
                AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
                List<String> mList = new ArrayList<>();
                mList.add(Constant.EVENTS);
                consents.setEntities(mList);
                consents.setPhone(groupMemberDataList.getNumber());
                consents.setName(groupMemberDataList.getName());
                consentList.add(consents);
            }
        }
        addMemberInGroupData.setConsents(consentList);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, groupId, userId));
    }

    /**
     * Add Member in group Success Listener
     */
    private class AddMemberInGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                mDbManager.insertGroupMemberDataInTable(groupMemberResponse);
                makeGroupInfoPerUserRequestAPICall();
            }
        }
    }

    /**
     * Add Member in Group Error Listener
     */
    private class AddMemberInGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.REQUEST_CONSENT_FAILED, Constant.ALERT_TITLE, DashboardActivity.this);
        }
    }

    /**
     * Goto Group List Activity
     */
    private void goToGroupListActivity(String groupId, String userId) {
        Intent intent = new Intent(this, GroupListActivity.class);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.USER_ID, userId);
        intent.putExtra(Constant.GROUPNAME, groupName);
        startActivity(intent);
        finish();
    }

    /**
     * Goto add device activity
     */
    private void goToAddDeviceActivity(String groupId) {
//        Intent intent = new Intent(this, AddDeviceActivity.class);
//        intent.putExtra(Constant.GROUP_ID, groupId);
////        intent.putExtra(Constant.USER_ID, userId);
//        intent.putExtra(Constant.GROUPNAME, groupName);
//        startActivity(intent);
//        finish();

        Intent intent = new Intent(this, AddPeopleActivity.class);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.GROUPNAME, groupName);
        startActivity(intent);
    }


    /**
     * Navigates to the Active Session Activity
     */
    private void gotoActiveSessionActivity() {
        Intent intent = new Intent(this, ActiveSessionActivity.class);
        intent.putExtra(Constant.USER_ID, userId);
        startActivity(intent);
    }

    // Gets called when app receives message
    @Override
    public void messageReceived(String message, String phoneNum) {
        if (message != null || ! "".equalsIgnoreCase(message)) {
            System.out.println("Message is " + message);
//            GroupRequestHandler.getInstance(this).handleRequest(new GetGroupInfoPerUserRequest(new GetGroupInfoPerUserRequestSuccessListener(), new GetGroupInfoPerUserRequestErrorListener(), userId));
        }
    }

    /**
     * Sets all the layout data
     */
    private void setLayoutData() {
        Toolbar toolbar = findViewById(R.id.customToolbar);
        listView = findViewById(R.id.listView);
        FloatingActionButton fabCreateGroup = findViewById(R.id.createGroup);
        FloatingActionButton fabAddDevice = findViewById(R.id.addDevice);
        FloatingActionButton fabAddContact = findViewById(R.id.addContact);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(linearLayoutManager);
        Button trackBtn = toolbar.findViewById(R.id.track);
        trackBtn.setVisibility(View.VISIBLE);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.DASHBOARD_TITLE);
        trackBtn.setOnClickListener(this);
        fabCreateGroup.setOnClickListener(this);
        fabAddDevice.setOnClickListener(this);
        fabAddContact.setOnClickListener(this);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    /**
     * Navigates to the RefreshIntervalSettingActivity
     */
    private void goToRefreshIntervalSettingActivity() {
        startActivity(new Intent(this, RefreshIntervalSettingActivity.class));
    }

   /* private void startService() {
        Intent serviceIntent = new Intent(this, SendLocationService.class);
        serviceIntent.putExtra("inputExtra", getString(R.string.notification_subtitle));
        ContextCompat.startForegroundService(this, serviceIntent);
    }*/

    /**
     * Shows Alert Dialog with Yes and No options
     *
     * @param message
     * @param title
     * @param groupId
     */
    public void alertDilogBoxWithCancelbtn(String message, String title, String groupId) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton(Constant.OK, (dialog, which) -> {
            makeDeleteGroupAPICall(groupId);
            isConsentButtonClicked = false;
            isConsentButtonClickedForGroupMember = false;
        });
        adb.setNegativeButton(Constant.CANCEL, (dialog, which) -> dialog.cancel());
        adb.show();
    }

    /**
     * Get All Group info per user API Call
     */
    protected void makeGroupInfoPerUserRequestAPICall() {
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupInfoPerUserRequest(new GetGroupInfoPerUserRequestSuccessListener(), new GetGroupInfoPerUserRequestErrorListener(), userId));
    }

    /**
     * GetGroupInfoPerUserRequest Success listener
     */
    private class GetGroupInfoPerUserRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetGroupInfoPerUserResponse getGroupInfoPerUserResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetGroupInfoPerUserResponse.class);
            parseResponseStoreInDatabase(getGroupInfoPerUserResponse);
            addDataInHomeScreen();
            adapterEventListener();
            isDevicePresent();
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GET_GROUP_INFO_PER_USER_ERROR, Constant.ALERT_TITLE, DashboardActivity.this);
            }
        }
    }

    /**
     * Parse the response and store in DB(Group Table and Member table)
     */
    public void parseResponseStoreInDatabase(GetGroupInfoPerUserResponse getGroupInfoPerUserResponse) {
        List<HomeActivityListData> groupList = new ArrayList<>();
        List<GroupMemberDataList> mGroupMemberDataLists = new ArrayList<>();
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
            HomeActivityListData homeActivityListData = new HomeActivityListData();
            homeActivityListData.setGroupName(data.getGroupName());
            homeActivityListData.setCreatedBy(data.getCreatedBy());
            homeActivityListData.setGroupId(data.getId());
            homeActivityListData.setStatus(data.getStatus());
            homeActivityListData.setUpdatedBy(data.getUpdatedBy());
            homeActivityListData.setFrom(data.getSession().getFrom());
            homeActivityListData.setTo(data.getSession().getTo());
            homeActivityListData.setGroupOwnerName(data.getGroupOwner().get(0).getName());
            homeActivityListData.setGroupOwnerPhoneNumber(data.getGroupOwner().get(0).getPhone());
            homeActivityListData.setGroupOwnerUserId(data.getGroupOwner().get(0).getUserId());
            groupList.add(homeActivityListData);
        }
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
            if (!data.getStatus().equalsIgnoreCase(Constant.CLOSED)) {
                for (GetGroupInfoPerUserResponse.Consents mConsents : data.getConsents()) {
                    GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
                    groupMemberDataList.setConsentId(mConsents.getConsentId());
                    groupMemberDataList.setNumber(mConsents.getPhone());
                    groupMemberDataList.setGroupAdmin(mConsents.isGroupAdmin());
                    groupMemberDataList.setGroupId(data.getId());
                    groupMemberDataList.setConsentStatus(mConsents.getStatus());
                    groupMemberDataList.setName(mConsents.getName());
                    groupMemberDataList.setUserId(mConsents.getUserId());
                    mGroupMemberDataLists.add(groupMemberDataList);
                }
            }
        }
        mDbManager.insertAllDataIntoGroupTable(groupList);
        mDbManager.insertGroupMemberDataInListFormat(mGroupMemberDataLists);
    }

    /**
     * Delete the Group and update the database
     */
    private void makeDeleteGroupAPICall(String groupId) {
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new DeleteGroupRequest(new DeleteGroupRequestSuccessListener(), new DeleteGroupRequestErrorListener(), groupId, userId));
    }

    /**
     * Delete Group Request API Call Success Listener and create new group if Session time is completed and Request Consent button is clicked
     */
    private class DeleteGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            if (isConsentButtonClicked) {
                mDbManager.deleteSelectedDataFromGroup(grpId);
                mDbManager.deleteSelectedDataFromGroupMember(grpId);
                createGroupAPICall(mHomeActivityListData.getGroupName());
            } else if (isConsentButtonClickedForGroupMember) {
                createGroupAPICall(mDbManager.getGroupDetail(mGroupMemberDataList.getGroupId()).getGroupName());
                mDbManager.deleteSelectedDataFromGroup(grpId);
                mDbManager.deleteSelectedDataFromGroupMember(grpId);
            } else {
                mDbManager.deleteSelectedDataFromGroup(grpId);
                mDbManager.deleteSelectedDataFromGroupMember(grpId);
                Util.progressDialog.dismiss();
                adapter.removeItem(listPosition);
                isDevicePresent();
                addDataInHomeScreen();
            }

        }
    }

    /**
     * Delete Group Request API Call Error Listener
     */
    private class DeleteGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.GROUP_DELETION_FAILURE, Constant.ALERT_TITLE, DashboardActivity.this);
        }
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
     * Navigates to the profile Activity
     */
    private void gotoProfileActivity() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    /**
     * Updates Logout data in database
     */
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
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            user_account_name.setText(Util.userName.substring(0, 1).toUpperCase(locale) + Util.userName.substring(1));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            String topic = "jioiot/svcd/jiophone/" + userPhoneNumber + "/uc/fwd/locinfo";
            System.out.println("Message --> " + message);
            System.out.println("Topic -->" + topic);
            new MQTTManager().publishMessage(topic, message);
        }
    }

    /**
     * Navigates to the Edit screen by carrying name and number
     *
     * @param name
     * @param phonenumber
     */
    private void gotoEditScreen(String name, String phonenumber, String groupId, String isGroupMember) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(Constant.NAME, name);
        intent.putExtra(Constant.NUMBER_CARRIER, phonenumber);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.USER_ID, userId);
        if (isGroupMember.equalsIgnoreCase(Constant.GROUP)) {
            intent.putExtra(Constant.IS_GROUP_MEMBER, isGroupMember);
        } else {
            intent.putExtra(Constant.IS_GROUP_MEMBER, isGroupMember);
        }
        startActivity(intent);
    }

    /**
     * Checks device is present or not, if not present show the help text otherwise display the added devices
     */
    protected void isDevicePresent() {
        if (listOnDashBoard.isEmpty()) {
            listView.setVisibility(View.INVISIBLE);
            devicePresent.setVisibility(View.VISIBLE);
        } else {
            devicePresent.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        makeGroupInfoPerUserRequestAPICall();
    }

    /**
     * Gets called when we click on button
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createGroup:
                if (checkLimitationInGroupIndividualCreation()) {
                    Util.alertDilogBox(Constant.USER_LIMITATION, Constant.ALERT_TITLE, this);
                    return;
                }
                startActivity(new Intent(this, CreateGroupActivity.class));
                break;
            case R.id.track:
                trackDevice();
                break;
            case R.id.addDevice:
                if (checkLimitationInGroupIndividualCreation()) {
                    Util.alertDilogBox(Constant.USER_LIMITATION, Constant.ALERT_TITLE, this);
                    return;
                }
                gotoQRScannerScreen();
                break;
            case R.id.addContact:
                if (checkLimitationInGroupIndividualCreation()) {
                    Util.alertDilogBox(Constant.USER_LIMITATION, Constant.ALERT_TITLE, this);
                    return;
                }
                gotoContactsDetailsActivity();
                break;
            default:
//                Log.d("TAG", "Some other button is clicked");
                break;
        }
    }

    /**
     * Navigates to the ContactDetailsActivity activity by counting individual device
     * which should be less than equal to ten.
     */
    private void gotoQRScannerScreen() {
        Intent intent = new Intent(this, QRReaderInstruction.class);
        intent.putExtra(Constant.IS_COMING_FROM_ADD_DEVICE, true);
        intent.putExtra(Constant.USER_ID, userId);
        startActivity(intent);
    }

    /**
     * Navigates to the Contact Details Activity
     */
    private void gotoContactsDetailsActivity() {
        Intent intent = new Intent(this, AddPeopleActivity.class);
        intent.putExtra(Constant.IS_COMING_FROM_ADD_CONTACT, true);
        intent.putExtra(Constant.USER_ID, userId);
        startActivity(intent);
    }

    /**
     * Search Event API call
     */
    private void trackDevice() {
        if (grpMemberDataList.isEmpty() && grpDataList.isEmpty()) {
            Util.alertDilogBox(Constant.CHOOSE_DEVICE, Constant.ALERT_TITLE, this);
            return;
        } else if (!grpMemberDataList.isEmpty() && grpMemberDataList.get(0).getConsentStatus().equalsIgnoreCase(Constant.PENDING)) {
            Util.alertDilogBox(Constant.CONSENT_NOT_APPROVED, Constant.ALERT_TITLE, this);
            return;
        } else if (!grpDataList.isEmpty() && grpDataList.get(0).getStatus().equalsIgnoreCase(Constant.PENDING)) {
            Util.alertDilogBox(Constant.CONSENT_NOT_APPROVED, Constant.ALERT_TITLE, this);
            return;
        } else {
            // If Group is selected for tracking
            if (! grpDataList.isEmpty()) {
                Util.getInstance().showProgressBarDialog(this);
                SearchEventData searchEventData = new SearchEventData();
                List<String> mList = new ArrayList<>();
                mList.add(Constant.LOCATION);
                mList.add(Constant.SOS);
                searchEventData.setTypes(mList);
                GroupRequestHandler.getInstance(this).handleRequest(new SearchEventRequest(new SearchEventRequestSuccessListener(), new SearchEventRequestErrorListener(), searchEventData, userId, grpDataList.get(0).getGroupId(), Constant.GET_LOCATION_URL));
            }
            // Make a count of number of selected devices and call the API that many times
            // If group members are selected for tracking
            else if (counter < grpMemberDataList.size()) {
                groupMemberName = grpMemberDataList.get(counter).getName();
                Util.getInstance().showProgressBarDialog(this);
                SearchEventData searchEventData = new SearchEventData();
                List<String> mList = new ArrayList<>();
                mList.add(Constant.LOCATION);
                mList.add(Constant.SOS);
                searchEventData.setTypes(mList);
                GroupRequestHandler.getInstance(this).handleRequest(new SearchEventRequest(new SearchEventRequestSuccessListener(), new SearchEventRequestErrorListener(), searchEventData, userId, grpMemberDataList.get(counter).getGroupId(), Constant.GET_LOCATION_URL));
            }
        }
    }

    /**
     * Search Event Request API call Success Listener
     */
    private class SearchEventRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            SearchEventResponse searchEventResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchEventResponse.class);
            if (searchEventResponse.getMessage().equalsIgnoreCase(Constant.NO_EVENTS_FOUND_RESPONSE)) {
                Util.alertDilogBox(Constant.LOCATION_NOT_FOUND, Constant.ALERT_TITLE, DashboardActivity.this);
            } else {
                List<SearchEventResponse.Data> mList = searchEventResponse.getData();
                if (! grpDataList.isEmpty()) {
                    List<GroupMemberDataList> grpMembersOfParticularGroupId = mDbManager.getAllGroupMemberDataBasedOnGroupId(grpDataList.get(0).getGroupId());
                    for (SearchEventResponse.Data data : mList) {
                        for (GroupMemberDataList grpMembers : grpMembersOfParticularGroupId) {
                            if (grpMembers.getDeviceId().equalsIgnoreCase(data.getDevice()) && grpMembers.getUserId().equalsIgnoreCase(data.getUserId())) {
                                MapData mapData = new MapData();
                                mapData.setLatitude(data.getLocation().getLat());
                                mapData.setLongitude(data.getLocation().getLng());
                                mapData.setName(grpMembers.getName());
                                mapDataList.add(mapData);
                                if (mapDataList.size() == grpMembersOfParticularGroupId.size()) {
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (! mList.isEmpty()) {
                        MapData mapData = new MapData();
                        mapData.setLatitude(mList.get(0).getLocation().getLat());
                        mapData.setLongitude(mList.get(0).getLocation().getLng());
                        mapData.setName(groupMemberName);
                        mapDataList.add(mapData);
                    }
                }
                if (!mapDataList.isEmpty() && grpMemberDataList.size() == mapDataList.size()) {
                    counter = 0;
                    goToMapActivity();
                } else if (!mapDataList.isEmpty() && ! grpDataList.isEmpty()) {
                    counter = 0;
                    goToMapActivity();
                } else {
                    counter++;
                    trackDevice();
                }
            }
        }
    }

    /**
     * Search Event Request API Call Error listener
     */
    private class SearchEventRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.FETCH_LOCATION_ERROR, Constant.ALERT_TITLE, DashboardActivity.this);
        }
    }

    /**
     * Get latest lat-long from server after particular time interval
     */
    private void trackDeviceAfterTimeInterval() {
        AdminLoginData adminLoginDetail = mDbManager.getAdminLoginDetail();
        List<String> data = new ArrayList<>();
        if (adminLoginDetail != null) {
            data.add(adminLoginDetail.getUserId());
            SearchDeviceStatusData searchDeviceStatusData = new SearchDeviceStatusData();
            SearchDeviceStatusData.Device device = searchDeviceStatusData.new Device();
            device.setUsersAssigned(data);
            searchDeviceStatusData.setDevice(device);
            RequestHandler.getInstance(this).handleRequest(new SearchDeviceStatusRequest(new SearchDeviceStatusAfterTimeIntervalSuccessListener(), new SearchDeviceStatusAfterTimeIntervalErrorListener(), Util.userToken, searchDeviceStatusData));
        }
    }

    /**
     * Success Listener of Search device status request
     */
    private class SearchDeviceStatusAfterTimeIntervalSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            /*SearchEventResponse searchEventResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchEventResponse.class);
            if (!selectedData.isEmpty()) {
                namingMap.clear();
                for (SearchEventResponse.Data data : searchEventResponse.getData()) {
                    for (MultipleselectData multipleselectData : selectedData) {
                        if (data.getDevice().getImei() != null) {
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
            }*/
        }
    }

    /**
     * Error Listener of Search device status request
     */
    private class SearchDeviceStatusAfterTimeIntervalErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.alertDilogBox(Constant.LOCATION_NOT_FOUND, Constant.ALERT_TITLE, DashboardActivity.this);
            }
        }
    }

    /**
     * start map thread scheduler
     */
    public void startTheScheduler() {
        if (thread != null) {
            thread.interrupt();
            thread = new Thread(new RefreshMap());
            //thread.start();
        }
    }

    /**
     * Refresh Map thread
     */
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

    /**
     * Connect to the MQTT server
     */
    public void makeMQTTConnection() {
        MQTTManager mqttManager = new MQTTManager();
        mqttManager.getMQTTClient(this);
        mqttManager.connetMQTT();
    }

    /**
     * Navigates to the Map activity
     */
    private void goToMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putParcelableArrayListExtra(Constant.MAP_DATA, (ArrayList<? extends Parcelable>) mapDataList);
        startActivity(intent);
    }

    /**
     * Class to calculate the signal strength
     */
    class MyPhoneStateListener extends PhoneStateListener {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            TelephonyManager mTelephonyManager = (TelephonyManager) DashboardActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
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
     * Send location to the borqs every 15 seconds
     */
    public class SendLocation implements Runnable {
        public void run() {
            while (true) {
                try {
                    makeMQTTConnection();
                    Thread.sleep(60000);
                    publishMessage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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

    /**
     * register the receiver
     */
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
                makeGroupInfoPerUserRequestAPICall();
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
                Toast.makeText(DashboardActivity.this, Constant.CONSENT_APPROVED_MESSAGE, Toast.LENGTH_SHORT).show();
                makeGroupInfoPerUserRequestAPICall();
            }
        }
    }

    /**
     * Approve Consent Request error Listener
     */
    private class ApproveConsentRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(DashboardActivity.this, Constant.CONSENT_NOT_APPROVED_MESSAGE, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DashboardActivity.this, Constant.CONSENT_REJECTED_MESSAGE, Toast.LENGTH_SHORT).show();
                addDataInHomeScreen();
            }
        }
    }

    /**
     * Reject Consent Request API error Listener
     */
    private class RejectConsentRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(DashboardActivity.this, Constant.CONSENT_NOT_REJECTED_MESSAGE, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (currentLocation != null) {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
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
     * Returns the current Location
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
                                            .checkSelfPermission(DashboardActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        currentLocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(DashboardActivity.this,
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

    /**
     * To check How many individual/user has been created
     *
     * @return true if it crosses the limit that is 10 or else returns false
     */
    private boolean checkLimitationInGroupIndividualCreation() {
        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        List<GroupMemberDataList> mGroupMemberList = mDbManager.getAllGroupMemberData();
        int count = 1;
        for (HomeActivityListData data : groupDetailList) {
            if (data.getCreatedBy() != null && data.getCreatedBy().equalsIgnoreCase(userId)) {
                if (!data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)) {
                    count++;
                }
            }
        }
        for (GroupMemberDataList groupMemberDataList : mGroupMemberList) {
            if (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getCreatedBy().equalsIgnoreCase(userId)
                    && !groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.REMOVED)) {
                count++;
            }
        }
        return count > 10;
    }

    /**
     * Adds data in Home screen using Recycler view, It is the main method to display devices in Home screen
     */
    protected void addDataInHomeScreen() {
        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        List<GroupMemberDataList> mGroupMemberList = mDbManager.getAllGroupMemberData();
        listOnDashBoard = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            if (data.getCreatedBy() != null && data.getCreatedBy().equalsIgnoreCase(userId)) {
                if (!data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)) {
                    HomeActivityListData homeActivityListData = new HomeActivityListData();
                    homeActivityListData.setGroupName(data.getGroupName());
                    homeActivityListData.setGroupId(data.getGroupId());
                    homeActivityListData.setStatus(data.getStatus());
                    homeActivityListData.setCreatedBy(data.getCreatedBy());
                    homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                    homeActivityListData.setProfileImage(data.getProfileImage());
                    homeActivityListData.setFrom(data.getFrom());
                    homeActivityListData.setTo(data.getTo());
                    listOnDashBoard.add(homeActivityListData);
                }
            }
        }
        for (GroupMemberDataList groupMemberDataList : mGroupMemberList) {
            GroupMemberDataList data = new GroupMemberDataList();
            if (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getCreatedBy().equalsIgnoreCase(userId)
                    && !groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.REMOVED)
                    && !groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.EXITED)) {
                data.setName(groupMemberDataList.getName());
                data.setNumber(groupMemberDataList.getNumber());
                data.setConsentStatus(groupMemberDataList.getConsentStatus());
                data.setGroupStatus(mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getStatus());
                data.setConsentId(groupMemberDataList.getConsentId());
                data.setUserId(groupMemberDataList.getUserId());
                data.setDeviceId(groupMemberDataList.getDeviceId());
                data.setGroupId(groupMemberDataList.getGroupId());
                data.setProfileImage(groupMemberDataList.getProfileImage());
                if (groupMemberDataList.isGroupAdmin() == true) {
                    data.setGroupAdmin(true);
                } else {
                    data.setGroupAdmin(false);
                }
                listOnDashBoard.add(data);
            }
        }
        adapter = new TrackerDeviceListAdapter(listOnDashBoard, this);
        listView.setAdapter(adapter);
    }
}
