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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.TrackingYouListAdapter;
import com.jio.devicetracker.view.adapter.TrackedByYouListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class shows all the active session(To whom you are tracking or you are tracked by)
 */
public class ActiveSessionActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView trackingByYouListRecyclerView;
    private RecyclerView trackingYouListRecyclerView;
    private DBManager mDbManager;
    private CardView trackedCardInstruction;
    private CardView trackingCardInstruction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.TRACK_MANAGEMENT_TITLE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        TextView trackedTitle = findViewById(R.id.tracked_title);
        TextView trackingTitle = findViewById(R.id.tracking_title);
        trackedTitle.setTypeface(Util.mTypeface(this, 3));
        trackingTitle.setTypeface(Util.mTypeface(this, 3));
        TextView trackedDefaultText = findViewById(R.id.tracked_default_text);
        trackedDefaultText.setTypeface(Util.mTypeface(this, 5));
        TextView trackingDefaultText = findViewById(R.id.tracking_default_text);
        trackingDefaultText.setTypeface(Util.mTypeface(this, 5));
        trackedCardInstruction = findViewById(R.id.default_tracked_text_card);
        trackingCardInstruction = findViewById(R.id.default_tracking_text_card);
        trackingByYouListRecyclerView = findViewById(R.id.trackingByYouList);
        trackingYouListRecyclerView = findViewById(R.id.trackingYouList);
        mDbManager = new DBManager(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        trackingByYouListRecyclerView.setLayoutManager(linearLayoutManager);
        trackingYouListRecyclerView.setLayoutManager(linearLayoutManager1);
        makeGroupInfoPerUserRequestAPICall();
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(this);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    /**
     * Get All Group info per user API Call
     */
    protected void makeGroupInfoPerUserRequestAPICall() {
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupInfoPerUserRequest(new GetGroupInfoPerUserRequestSuccessListener(), new GetGroupInfoPerUserRequestErrorListener(), mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * GetGroupInfoPerUserRequest Success listener
     */
    private class GetGroupInfoPerUserRequestSuccessListener implements com.android.volley.Response.Listener {
        @Override
        public void onResponse(Object response) {
            addDatainList();
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements com.android.volley.Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                showCustomAlertWithText(Constant.GET_GROUP_INFO_PER_USER_ERROR);
            }
        }
    }

    /**
     * Adds data in List
     */
    private void addDatainList() {
        DBManager mDbManager = new DBManager(getApplicationContext());
        String userId = mDbManager.getAdminLoginDetail().getUserId();
        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        List<GroupMemberDataList> mGroupMemberList = mDbManager.getAllGroupMemberData();
        List<HomeActivityListData> listOnActiveSession = new ArrayList<>();

        // Adding Tracked by you list in Active Session
        for (HomeActivityListData data : groupDetailList) {
            if (data.getStatus().equalsIgnoreCase(Constant.ACTIVE)
                    && !data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)
                    && !data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_DEVICE_GROUP_NAME)
                    && data.getCreatedBy().equalsIgnoreCase(userId)) {
                HomeActivityListData homeActivityListData = new HomeActivityListData();
                homeActivityListData.setGroupName(data.getGroupName());
                homeActivityListData.setGroupId(data.getGroupId());
                homeActivityListData.setCreatedBy(data.getCreatedBy());
                homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                homeActivityListData.setProfileImage(R.drawable.ic_group_button);
                homeActivityListData.setFrom(data.getFrom());
                homeActivityListData.setTo(data.getTo());
                homeActivityListData.setConsentsCount(data.getConsentsCount());
                listOnActiveSession.add(homeActivityListData);
            }
        }

        // Add Tracking you in list of Active session
        List<HomeActivityListData> trackingYouList = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            for (GroupMemberDataList groupMemberDataList : mGroupMemberList) {
                if (!userId.equalsIgnoreCase(data.getGroupOwnerUserId())
                        && data.getStatus().equalsIgnoreCase(Constant.ACTIVE)
                        && data.getGroupId().equalsIgnoreCase(groupMemberDataList.getGroupId())
                        && groupMemberDataList.getUserId().equalsIgnoreCase(userId)
                        && groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.APPROVED)) {
                    HomeActivityListData trackingYou = new HomeActivityListData();
                    if (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)) {
                        trackingYou.setGroupOwnerName(data.getGroupOwnerName());
                        trackingYou.setGroupOwnerPhoneNumber(data.getGroupOwnerPhoneNumber());
                        trackingYou.setGroupOwnerUserId(data.getGroupOwnerUserId());
                        trackingYou.setGroupId(data.getGroupId());
                        trackingYou.setGroupName(data.getGroupOwnerName());
                        trackingYou.setConsentId(groupMemberDataList.getConsentId());
                        trackingYou.setConsentsCount(data.getConsentsCount());
                        trackingYouList.add(trackingYou);
                    } else {
                        trackingYou.setGroupOwnerName(data.getGroupOwnerName());
                        trackingYou.setGroupOwnerPhoneNumber(data.getGroupOwnerPhoneNumber());
                        trackingYou.setGroupOwnerUserId(data.getGroupOwnerUserId());
                        trackingYou.setGroupId(data.getGroupId());
                        trackingYou.setGroupName(data.getGroupName());
                        trackingYou.setConsentsCount(data.getConsentsCount());
                        trackingYouList.add(trackingYou);
                    }
                }
            }
        }


        for (GroupMemberDataList groupMemberDataList : mGroupMemberList) {
            HomeActivityListData data = new HomeActivityListData();
            if (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName() != null
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getStatus().equalsIgnoreCase(Constant.ACTIVE)
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getCreatedBy().equalsIgnoreCase(userId)
                    && !groupMemberDataList.getUserId().equalsIgnoreCase(userId)
                    && (groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.APPROVED)
                    || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.PENDING))) {
                data.setName(groupMemberDataList.getName());
                data.setNumber(groupMemberDataList.getNumber());
                data.setConsentStaus(groupMemberDataList.getConsentStatus());
                data.setConsentId(groupMemberDataList.getConsentId());
                data.setUserId(groupMemberDataList.getUserId());
                data.setDeviceId(groupMemberDataList.getDeviceId());
                data.setGroupId(groupMemberDataList.getGroupId());
                data.setFrom(mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getFrom());
                data.setTo(mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getTo());
                listOnActiveSession.add(data);
            }
        }

        if (listOnActiveSession.isEmpty()) {
            trackedCardInstruction.setVisibility(View.VISIBLE);
        } else {
            trackedCardInstruction.setVisibility(View.INVISIBLE);
        }

        if (trackingYouList.isEmpty()) {
            trackingCardInstruction.setVisibility(View.VISIBLE);
        } else {
            trackingCardInstruction.setVisibility(View.INVISIBLE);
        }

        TrackedByYouListAdapter mTrackedByYouListAdapter = new TrackedByYouListAdapter(listOnActiveSession, this);
        TrackingYouListAdapter mTrackingYouListAdapter = new TrackingYouListAdapter(trackingYouList, this);
        trackingByYouListRecyclerView.setAdapter(mTrackedByYouListAdapter);
        trackingYouListRecyclerView.setAdapter(mTrackingYouListAdapter);
    }
}
