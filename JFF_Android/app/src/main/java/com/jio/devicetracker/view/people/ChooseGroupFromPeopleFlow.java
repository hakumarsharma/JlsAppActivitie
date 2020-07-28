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

package com.jio.devicetracker.view.people;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
import com.jio.devicetracker.view.adapter.ChooseGroupListAdapter;
import com.jio.devicetracker.view.adapter.DashboardAdapter;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;
import com.jio.devicetracker.view.group.ChooseGroupActivity;
import com.jio.devicetracker.view.group.CreateGroupActivity;

import java.util.ArrayList;
import java.util.List;

public class ChooseGroupFromPeopleFlow extends BaseActivity implements View.OnClickListener {
    private String userId;
    private DBManager mDbManager;
    private CardView cardViewGroup;
    private String groupId;
    private ChooseGroupListAdapter mAdapter;
    private String name;
    private String phoneNumber;
    private TextView groupText;
    private List<HomeActivityListData> chooseGroupDataList;
    private Button continueChooseGroup;
    private Boolean isFromPeopleAddToGroup;
    private Boolean isFromDeviceAddToGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group_people_flow);
        initDataMember();
        initUI();
        makeGroupInfoPerUserRequestAPICall();
    }
    /**
     * Get All Group info per user API Call
     */
    protected void makeGroupInfoPerUserRequestAPICall() {
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupInfoPerUserRequest(new GetGroupInfoPerUserRequestSuccessListener(), new GetGroupInfoPerUserRequestErrorListener(), userId));
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
        cardViewGroup = findViewById(R.id.cardViewList);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.ADD_GROUP);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        ImageView add = findViewById(R.id.createGroup);
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(this);
        Intent intent = getIntent();
        groupText = findViewById(R.id.group_detail_text);
        name = intent.getStringExtra(Constant.TRACKEE_NAME);
        phoneNumber = intent.getStringExtra(Constant.TRACKEE_NUMBER);
        isFromPeopleAddToGroup = intent.getBooleanExtra(Constant.IS_PEOPLE_ADD_TO_GROUP,false);
        isFromDeviceAddToGroup = intent.getBooleanExtra(Constant.IS_DEVICE_ADD_TO_GROUP,false);
        continueChooseGroup = findViewById(R.id.continueChooseGroup);
        continueChooseGroup.setOnClickListener(this);
        Button addLater = findViewById(R.id.addLater);
        addLater.setOnClickListener(this);

    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage){
        CustomAlertActivity alertActivity = new CustomAlertActivity(this);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.createGroup:
                gotoCreateGroupActivity();
                break;
            case R.id.continueChooseGroup:
                addMemberToCreatedGroup(groupId);
                break;
            case R.id.addLater:
                if (isFromPeopleAddToGroup){
                    Intent intent = new Intent(this, DashboardMainActivity.class);
                    intent.putExtra(Constant.Add_People, true);
                    intent.putExtra(Constant.Add_Device, false);
                    startActivity(intent);
                } else if (isFromDeviceAddToGroup){
                    Intent intent = new Intent(this, DashboardMainActivity.class);
                    intent.putExtra(Constant.Add_People, false);
                    intent.putExtra(Constant.Add_Device, true);
                    startActivity(intent);
                } else {
                    createGroupAndAddContactDetails();
                }
                break;
            default:
                // Todo
                break;
        }
    }

    private void gotoCreateGroupActivity() {
        Intent createGroupIntent = new Intent(this, CreateGroupActivity.class);
        createGroupIntent.putExtra(Constant.TRACKEE_NAME,name);
        createGroupIntent.putExtra(Constant.TRACKEE_NUMBER,phoneNumber);
        startActivity(createGroupIntent);
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
            if(!(data.getGroupOwner().isEmpty())) {
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
                    if(groupIconSelection != null && !groupIconSelection.equalsIgnoreCase(Constant.GROUP_SELECTED)){
                        groupId = homeActivityListData.getGroupId();
                    } else {
                        groupId = "";
                    }

                }

               /* @Override
                public void groupButtonClicked(HomeActivityListData homeActivityListData) {
                    groupId = homeActivityListData.getGroupId();
                    updateUIInChooseGroupActivity(homeActivityListData);
                    //addMemberToCreatedGroup(homeActivityListData.getGroupId());
                }*/
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

        if(chooseGroupDataList.isEmpty()) {
            groupText.setVisibility(View.VISIBLE);
            cardViewGroup.setVisibility(View.INVISIBLE);
            continueChooseGroup.setEnabled(false);
            continueChooseGroup.setBackground(getResources().getDrawable(R.drawable.selector));
        }
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,4);
        RecyclerView mRecyclerView = findViewById(R.id.chooseGroupRecyclerViewWithInfo);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChooseGroupListAdapter(chooseGroupDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void addMemberToCreatedGroup(String groupId) {
        if(groupId == null || groupId.isEmpty()) {
            showCustomAlertWithText(Constant.GROUP_CHOOSE_MEMBER_CONDITION);
            return;
        }
        this.createdGroupId = groupId;
        this.memberName = name;
        this.memberNumber = phoneNumber;
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        this.isFromDevice = true;
        this.isNavigateToGroupsFragment = true;
        addMemberInGroupAPICall();
    }
    private void createGroupAndAddContactDetails() {
        this.memberName = name;
        this.memberNumber = phoneNumber;
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        this.isFromDevice = false;
        createGroupAndAddContactAPICall(Constant.INDIVIDUAL_USER_GROUP_NAME);
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
}
