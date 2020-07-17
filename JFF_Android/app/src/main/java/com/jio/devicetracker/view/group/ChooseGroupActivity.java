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

package com.jio.devicetracker.view.group;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.DeviceTableData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.SearchDeviceStatusData;
import com.jio.devicetracker.database.pojo.request.AddDeviceRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.request.GetUserDevicesListRequest;
import com.jio.devicetracker.database.pojo.response.AddDeviceResponse;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.database.pojo.response.GetUserDevicesListResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
import com.jio.devicetracker.view.adapter.ChooseGroupListAdapter;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;

import java.util.ArrayList;
import java.util.List;

public class ChooseGroupActivity extends BaseActivity implements View.OnClickListener {

    private DBManager mDbManager;
    private ChooseGroupListAdapter mAdapter;
    private EditText trackeeNameEditText;
    private String userId;
    private ImageView memberIcon;
    private TextView groupText;
    private CardView cardViewGroup;
    private String phoneNumber;
    private String imeiNumber;
    private String devicename;
    private String groupId;
    private List<HomeActivityListData> chooseGroupDataList;
    private String label;
    private Button continueBtn;
    private boolean isAddLater;
    private boolean isNavigateToCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group);
        Intent intent = getIntent();
        label = intent.getStringExtra(Constant.REAL_ICON);
        phoneNumber = intent.getStringExtra(Constant.DEVICE_PHONE_NUMBER);
        imeiNumber = intent.getStringExtra(Constant.DEVICE_IMEI_NUMBER);
        devicename = intent.getStringExtra(Constant.TITLE_NAME);
        initUI();
        setMemberIcon(label);
        initDataMember();
        makeGroupInfoPerUserRequestAPICall();
    }

    // Set the memberIcon
    private void setMemberIcon(String label) {
        trackeeNameEditText.setText(devicename);
        if (label != null && !label.isEmpty()) {
            if (label.equalsIgnoreCase(Constant.MOM)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
            } else if (label.equalsIgnoreCase(Constant.FATHER)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
            } else if (label.equalsIgnoreCase(Constant.HUSBAND)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
            } else if (label.equalsIgnoreCase(Constant.CAT)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
            } else if (label.equalsIgnoreCase(Constant.DOG)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
            } else if (label.equalsIgnoreCase(Constant.OTHER_PET)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
            } else if (label.equalsIgnoreCase(Constant.KID)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
            } else if (label.equalsIgnoreCase(Constant.OTHER)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
            }
            if (label.equalsIgnoreCase(Constant.WIFE)) {
                if (label != null && !label.isEmpty()) {
                    if (label.equalsIgnoreCase(Constant.WIFE)) {
                        memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                    }
                }

            }
        }
    }


    /**
     * Initialize data members
     */
    private void initDataMember() {
        mDbManager = new DBManager(this);
        userId = mDbManager.getAdminLoginDetail().getUserId();
    }

    /**
     * Initialize UI component
     */
    private void initUI() {
        memberIcon = findViewById(R.id.userIcon);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.Choose_Group);
        ImageView createGroup = findViewById(R.id.createGroup);
        createGroup.setVisibility(View.VISIBLE);
        createGroup.setOnClickListener(this);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        groupText = findViewById(R.id.group_detail_text);
        cardViewGroup = findViewById(R.id.cardViewList);

        title.setTypeface(Util.mTypeface(this, 5));
        trackeeNameEditText = findViewById(R.id.trackeeNameEditText);
        trackeeNameEditText.setTypeface(Util.mTypeface(this, 5));
        Button chooseGroupButton = findViewById(R.id.continueChooseGroup);
        chooseGroupButton.setTypeface(Util.mTypeface(this, 5));
        Button addLater = findViewById(R.id.addLater);
        continueBtn = findViewById(R.id.continueChooseGroup);
        continueBtn.setOnClickListener(this);
        addLater.setOnClickListener(this);
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(this);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    /**
     * To do event handling
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        // To do
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.createGroup:
                isNavigateToCreateGroup = true;
                isAddLater = false;
                getUserDevicesList();
                break;
            case R.id.addLater:
                isNavigateToCreateGroup = false;
                isAddLater = true;
                getUserDevicesList();
                break;
            case R.id.continueChooseGroup:
                isNavigateToCreateGroup = false;
                isAddLater = false;
                getUserDevicesList();
                break;
            default:
                // Todo
                break;
        }
    }

    private void gotoCreateGroupActivity() {
        Intent createGroupIntent = new Intent(this, CreateGroupActivity.class);
        DashboardMainActivity.flowFromDevice = true;
        createGroupIntent.putExtra(Constant.TRACKEE_NAME, trackeeNameEditText.getText().toString());
        createGroupIntent.putExtra(Constant.TRACKEE_NUMBER, phoneNumber);
        startActivity(createGroupIntent);
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
            addDatainList();
            adapterEventListener();
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                showCustomAlertWithText(Constant.GET_GROUP_INFO_PER_USER_ERROR);
            }
        }
    }

    /**
     * Parse the response and store in DB(Group Table and Member table)
     */
    public void parseResponseStoreInDatabase(GetGroupInfoPerUserResponse
                                                     getGroupInfoPerUserResponse) {
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
            if (!(data.getGroupOwner().isEmpty())) {
                homeActivityListData.setGroupOwnerName(data.getGroupOwner().get(0).getName());
                homeActivityListData.setGroupOwnerPhoneNumber(data.getGroupOwner().get(0).getPhone());
                homeActivityListData.setGroupOwnerUserId(data.getGroupOwner().get(0).getUserId());
            }
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
     * Adapter Listener
     */
    private void adapterEventListener() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickPagerListener(new ChooseGroupListAdapter.RecyclerViewClickListener() {
                @Override
                public void groupButtonClicked(HomeActivityListData homeActivityListData, String groupIconSelection) {
                    updateUIInChooseGroupActivity(homeActivityListData);
                    if (groupIconSelection != null && !groupIconSelection.equalsIgnoreCase(Constant.GROUP_SELECTED)) {
                        groupId = homeActivityListData.getGroupId();
                    } else {
                        groupId = "";
                    }

                }
            });
        }
    }

    /**
     * Displays created group in recycler view
     */
    private void addDatainList() {
        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        List<HomeActivityListData> mGroupIconList = mDbManager.getAllGroupIconTableData();
        chooseGroupDataList = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            if (data.getCreatedBy() != null && data.getCreatedBy().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getUserId())) {
                if (!data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME) && !data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_DEVICE_GROUP_NAME) && (data.getStatus().equalsIgnoreCase("Active") || data.getStatus().equalsIgnoreCase("Scheduled"))) {
                    HomeActivityListData homeActivityListData = new HomeActivityListData();
                    homeActivityListData.setGroupName(data.getGroupName());
                    homeActivityListData.setGroupId(data.getGroupId());
                    homeActivityListData.setStatus(data.getStatus());
                    homeActivityListData.setCreatedBy(data.getCreatedBy());
                    homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                    homeActivityListData.setFrom(data.getFrom());
                    homeActivityListData.setTo(data.getTo());
                    if (mGroupIconList.toArray().length > 0) {
                        for (HomeActivityListData mHomeActivityListData : mGroupIconList) {
                            if (mHomeActivityListData.getGroupId().equalsIgnoreCase(data.getGroupId())) {
                                    homeActivityListData.setGroupIcon(mHomeActivityListData.getGroupIcon());
                            }
                        }
                    }else {
                        homeActivityListData.setGroupIcon("default_group");
                    }

                    // If user uninstalls and installs app, and creates a group then groupiconlist will not be empty
                    // so in that case groupicon will icon be null as our groupid is captured based on selection this condition is placed
                    if (homeActivityListData.getGroupIcon() == null){
                        homeActivityListData.setGroupIcon("default_group");
                    }
                    chooseGroupDataList.add(homeActivityListData);
                }
            }
        }

        if (chooseGroupDataList.isEmpty()) {
            groupText.setVisibility(View.VISIBLE);
            cardViewGroup.setVisibility(View.INVISIBLE);
            continueBtn.setBackground(getResources().getDrawable(R.drawable.selector));
            continueBtn.setEnabled(false);

        }
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        RecyclerView mRecyclerView = findViewById(R.id.chooseGroupRecyclerViewWithInfo);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChooseGroupListAdapter(chooseGroupDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void addMemberToCreatedGroup(String groupId) {
        if (groupId == null || groupId.isEmpty()) {
            showCustomAlertWithText(Constant.GROUP_CHOOSE_CONDITION);
            return;
        }
        if (trackeeNameEditText.getText() == null || trackeeNameEditText.getText().length() == 0) {
            showCustomAlertWithText(Constant.NAME_VALIDATION);
            return;
        }
        this.createdGroupId = groupId;
        this.memberName = trackeeNameEditText.getText().toString();
        this.memberNumber = phoneNumber;
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        this.isFromDevice = true;
        this.isNavigateToGroupsFragment = true;
        addMemberInGroupAPICall();
    }

    private void createGroupAndAddContactDetails() {
        this.memberName = trackeeNameEditText.getText().toString();
        this.memberNumber = phoneNumber;
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        this.isFromDevice = true;
        isNavigateToGroupsFragment = false;
        setUserIcon(label);
        createGroupAndAddContactAPICall(Constant.INDIVIDUAL_DEVICE_GROUP_NAME);
    }

    private void updateUIInChooseGroupActivity(HomeActivityListData mData) {
        List<HomeActivityListData> mHomeActivityListData = new ArrayList<>();
        for (HomeActivityListData data : chooseGroupDataList) {
            HomeActivityListData homeActivityListData = new HomeActivityListData();
            homeActivityListData.setGroupName(data.getGroupName());
            homeActivityListData.setGroupId(data.getGroupId());
            homeActivityListData.setStatus(data.getStatus());
            homeActivityListData.setCreatedBy(data.getCreatedBy());
            homeActivityListData.setUpdatedBy(data.getUpdatedBy());
            homeActivityListData.setFrom(data.getFrom());
            homeActivityListData.setTo(data.getTo());
            if (mData.getGroupIcon() != null && mData.getGroupIcon().equalsIgnoreCase(Constant.GROUP_SELECTED)) {
                homeActivityListData.setGroupIcon(data.getGroupIcon());
            } else if (mData.getGroupId().equalsIgnoreCase(data.getGroupId())) {
                homeActivityListData.setGroupIcon("groupSelected");
            } else {
                homeActivityListData.setGroupIcon(data.getGroupIcon());
            }
            mHomeActivityListData.add(homeActivityListData);
        }
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        RecyclerView mRecyclerView = findViewById(R.id.chooseGroupRecyclerViewWithInfo);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChooseGroupListAdapter(mHomeActivityListData, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Verify and assign API Call for the white-listing of device
     */
    private void makeVerifyAndAssignAPICall() {
        AddDeviceData addDeviceData = new AddDeviceData();
        List<AddDeviceData.Devices> mList = new ArrayList<>();
        AddDeviceData.Devices devices = new AddDeviceData().new Devices();
        devices.setMac(imeiNumber);
        devices.setPhone(imeiNumber);
        devices.setIdentifier("imei");
        devices.setName(trackeeNameEditText.getText().toString());
        devices.setType("watch");
        devices.setModel("watch");
        AddDeviceData.Devices.Metaprofile metaprofile = new AddDeviceData().new Devices().new Metaprofile();
        metaprofile.setFirst(trackeeNameEditText.getText().toString());
        metaprofile.setSecond("success");
        devices.setMetaprofile(metaprofile);
        AddDeviceData.Flags flags = new AddDeviceData().new Flags();
        flags.setSkipAddDeviceToGroup(false);
        addDeviceData.setFlags(flags);
        mList.add(devices);
        addDeviceData.setDevices(mList);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new AddDeviceRequest(new AddDeviceRequestSuccessListener(), new AddDeviceRequestErrorListener(), mDbManager.getAdminLoginDetail().getUserToken(), userId, addDeviceData));
    }

    /**
     * Verify & Assign API call success listener
     */
    private class AddDeviceRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            AddDeviceResponse addDeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), AddDeviceResponse.class);
            if (addDeviceResponse.getCode() == 200) {
                DeviceTableData deviceTableData = mDbManager.getDeviceTableData(imeiNumber);
                if (deviceTableData == null) {
                    DeviceTableData mDeviceTableData = new DeviceTableData();
                    mDeviceTableData.setPhoneNumber(phoneNumber);
                    mDeviceTableData.setImeiNumber(imeiNumber);
                    mDeviceTableData.setAdditionCount(0);
                    mDbManager.insertIntoDeviceTable(mDeviceTableData);
                } else {
                    int count = deviceTableData.getAdditionCount();
                    DeviceTableData mDeviceTableData = new DeviceTableData();
                    mDeviceTableData.setPhoneNumber(phoneNumber);
                    mDeviceTableData.setAdditionCount(++count);
                    mDbManager.updateIntoDeviceTable(mDeviceTableData);
                }
                if (isNavigateToCreateGroup) {
                    gotoCreateGroupActivity();
                } else if (isAddLater) {
                    createGroupAndAddContactDetails();
                } else {
                    addMemberToCreatedGroup(groupId);
                }
                //Toast.makeText(ChooseGroupActivity.this, Constant.SUCCESSFULL_DEVICE_ADDITION, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Verify & Assign API call error listener
     */
    private class AddDeviceRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            showCustomAlertWithText(Constant.UNSUCCESSFULL_DEVICE_ADD);

        }
    }


    /**
     * Get User Devices list API call
     */
    private void getUserDevicesList() {
        if (trackeeNameEditText == null || trackeeNameEditText.getText().toString().isEmpty()) {
            showCustomAlertWithText(Constant.DEVICE_NAME_VALIDATION);
            return;
        }
        AdminLoginData adminLoginDetail = mDbManager.getAdminLoginDetail();
        List<String> data = new ArrayList<>();
        if (adminLoginDetail != null) {
            data.add(adminLoginDetail.getUserId());
            SearchDeviceStatusData searchDeviceStatusData = new SearchDeviceStatusData();
            SearchDeviceStatusData.Device device = searchDeviceStatusData.new Device();
            device.setUsersAssigned(data);
            searchDeviceStatusData.setDevice(device);
            GroupRequestHandler.getInstance(getApplicationContext()).handleRequest(new GetUserDevicesListRequest(new GetDeviceRequestSuccessListener(), new GetDeviceRequestErrorListener(), searchDeviceStatusData));
        }
    }

    /**
     * Get Devices list API call success listener
     */
    private class GetDeviceRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetUserDevicesListResponse getDeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetUserDevicesListResponse.class);
            if (getDeviceResponse.getCode() == 200) {
                boolean isNumberExists = false;
                for (GetUserDevicesListResponse.Data data : getDeviceResponse.getData()) {

                    // for (GetUserDevicesListResponse.Devices devices : data.getDevices()){
                    if (data.getDevices().getImei().equalsIgnoreCase(imeiNumber)) {
                        isNumberExists = true;
                        break;
                    }
                    // }

                }
                if (isNumberExists) {
                    if (isNavigateToCreateGroup) {
                        gotoCreateGroupActivity();
                    } else if (isAddLater) {
                        createGroupAndAddContactDetails();
                    } else {
                        addMemberToCreatedGroup(groupId);
                    }
                } else {
                    makeVerifyAndAssignAPICall();
                }
            }
        }
    }

    /**
     * Get Devices list API call error listener
     */
    private class GetDeviceRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            showCustomAlertWithText(Constant.UNSUCCESSFULL_DEVICE_ADD);
        }
    }
}

