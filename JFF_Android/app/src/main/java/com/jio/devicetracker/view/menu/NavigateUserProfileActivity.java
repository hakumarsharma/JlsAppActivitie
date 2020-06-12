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

package com.jio.devicetracker.view.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.ActiveSessionListAdapter;
import com.jio.devicetracker.view.adapter.ProfileTrackedByListAdapter;

import java.util.ArrayList;
import java.util.List;

public class NavigateUserProfileActivity extends Activity implements View.OnClickListener {

    private TextView userName;
    private TextView userNumber;
    private List listOnActiveSession;
    private DBManager mDbManager;
    private RecyclerView trackedList;
    private ProfileTrackedByListAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent intent = getIntent();
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.PROFILE_TITLE);
        mDbManager = new DBManager(this);
        title.setTypeface(Util.mTypeface(this,5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        userName = findViewById(R.id.userName);
        userNumber = findViewById(R.id.userNumber);
        userName.setText(intent.getStringExtra("Name"));
        userNumber.setText(intent.getStringExtra("Number"));
        trackedList = findViewById(R.id.tracked_list);
        Button editBtn = findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        trackedList.setLayoutManager(linearLayoutManager);
        makeGroupInfoPerUserRequestAPICall();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.edit_btn:
                gotoUpdateProfileActivity();
                break;
        }
    }

    private void gotoUpdateProfileActivity() {
        Intent intent = new Intent(this,EditUserProfileActivity.class);
        intent.putExtra("Name",userName.getText().toString());
        intent.putExtra("Number",userNumber.getText().toString());
        startActivity(intent);
    }
    /**
     * Get All Group info per user API Call
     */
    protected void makeGroupInfoPerUserRequestAPICall() {
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupInfoPerUserRequest(new GetGroupInfoPerUserRequestSuccessListener(), new GetGroupInfoPerUserRequestErrorListener(), mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * GetGroupInfoPerUserRequest Success listener
     */
    private class GetGroupInfoPerUserRequestSuccessListener implements com.android.volley.Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            addDatainList();
           /* adapterEventListener();
            isAnyMemberActive();*/
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements com.android.volley.Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GET_GROUP_INFO_PER_USER_ERROR, Constant.ALERT_TITLE, NavigateUserProfileActivity.this);
            }
        }
    }

    /**
     * Adds data in List
     */
    private void addDatainList() {
        DBManager mDbManager = new DBManager(getApplicationContext());
        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        List<GroupMemberDataList> mGroupMemberList = mDbManager.getAllGroupMemberData();
        listOnActiveSession = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            if (data.getStatus().equalsIgnoreCase(Constant.ACTIVE) && !data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)) {
                HomeActivityListData homeActivityListData = new HomeActivityListData();
                homeActivityListData.setGroupName(data.getGroupName());
                homeActivityListData.setGroupId(data.getGroupId());
                homeActivityListData.setCreatedBy(data.getCreatedBy());
                homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                homeActivityListData.setProfileImage(R.drawable.ic_group_button);
                homeActivityListData.setFrom(data.getFrom());
                homeActivityListData.setTo(data.getTo());
                listOnActiveSession.add(homeActivityListData);
            }
        }

        for (GroupMemberDataList groupMemberDataList : mGroupMemberList) {
            GroupMemberDataList data = new GroupMemberDataList();
            if (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getStatus().equalsIgnoreCase(Constant.ACTIVE)
                    && !groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.EXITED) && !groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.REMOVED)) {
                data.setName(groupMemberDataList.getName());
                data.setNumber(groupMemberDataList.getNumber());
                data.setConsentStatus(groupMemberDataList.getConsentStatus());
                data.setConsentId(groupMemberDataList.getConsentId());
                data.setUserId(groupMemberDataList.getUserId());
                data.setDeviceId(groupMemberDataList.getDeviceId());
                data.setGroupId(groupMemberDataList.getGroupId());
                data.setProfileImage(groupMemberDataList.getProfileImage());
                data.setFrom(mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getFrom());
                data.setTo(mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getTo());
                if (groupMemberDataList.isGroupAdmin() == true) {
                    data.setGroupAdmin(true);
                } else {
                    data.setGroupAdmin(false);
                }
                listOnActiveSession.add(data);
            }
        }
        mAdapter = new ProfileTrackedByListAdapter(listOnActiveSession);
        trackedList.setAdapter(mAdapter);
       // trackingList.setAdapter(mAdapter);
    }

}
