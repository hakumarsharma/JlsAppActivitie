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

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.database.pojo.SearchEventData;
import com.jio.devicetracker.database.pojo.TrackingYou;
import com.jio.devicetracker.database.pojo.request.DeleteGroupRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.request.SearchEventRequest;
import com.jio.devicetracker.database.pojo.response.SearchEventResponse;
import com.jio.devicetracker.network.ExitRemoveDeleteAPI;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.TrackingYouListAdapter;
import com.jio.devicetracker.view.location.MapsActivity;
import com.jio.devicetracker.view.adapter.TrackedByYouListAdapter;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class shows all the active session(To whom you are tracking or you are tracked by)
 */
public class ActiveSessionActivity extends AppCompatActivity implements View.OnClickListener {
    private TrackedByYouListAdapter mTrackedByYouListAdapter;
    private TrackingYouListAdapter mTrackingYouListAdapter;
    private RecyclerView trackingByYouListRecyclerView;
    private RecyclerView trackingYouListRecyclerView;
    private List<HomeActivityListData> listOnActiveSession;
    private TextView activeMemberPresent;
    private int position;
    private String errorMessage;
    private String groupId;
    private String groupMemberName;
    private DBManager mDbManager;
    private boolean isGroup;
    private TextView trackedTitle;
    private TextView trackingTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.ACTIVE_SESSION_TITLE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        trackedTitle = findViewById(R.id.tracked_title);
        trackingTitle = findViewById(R.id.tracking_title);
        trackedTitle.setTypeface(Util.mTypeface(this, 3));
        trackingTitle.setTypeface(Util.mTypeface(this, 3));
        trackingByYouListRecyclerView = findViewById(R.id.trackingByYouList);
        trackingYouListRecyclerView = findViewById(R.id.trackingYouList);
        activeMemberPresent = findViewById(R.id.activeMemberPresent);
        mDbManager = new DBManager(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        trackingByYouListRecyclerView.setLayoutManager(linearLayoutManager);
        trackingYouListRecyclerView.setLayoutManager(linearLayoutManager1);
        makeGroupInfoPerUserRequestAPICall();
    }

    /**
     * Track the group
     *
     * @param homeActivityListData
     */
    private void trackUserForGroup(HomeActivityListData homeActivityListData) {
        Util.getInstance().showProgressBarDialog(this);
        SearchEventData searchEventData = new SearchEventData();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.LOCATION);
        mList.add(Constant.SOS);
        searchEventData.setTypes(mList);
        isGroup = true;
        GroupRequestHandler.getInstance(this).handleRequest(new SearchEventRequest(new SearchEventRequestSuccessListener(), new SearchEventRequestErrorListener(), searchEventData, mDbManager.getAdminLoginDetail().getUserId(), homeActivityListData.getGroupId(), Constant.GET_LOCATION_URL));
    }

    /**
     * Track User for group Member
     */
    private void trackUser(GroupMemberDataList groupMemberDataList) {
        groupMemberName = groupMemberDataList.getName();
        Util.getInstance().showProgressBarDialog(this);
        SearchEventData searchEventData = new SearchEventData();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.LOCATION);
        mList.add(Constant.SOS);
        searchEventData.setTypes(mList);
        isGroup = false;
        GroupRequestHandler.getInstance(this).handleRequest(new SearchEventRequest(new SearchEventRequestSuccessListener(), new SearchEventRequestErrorListener(), searchEventData, groupMemberDataList.getUserId(), groupMemberDataList.getGroupId(), Constant.GET_LOCATION_URL));
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    /**
     * Search Event Request API call Success Listener
     */
    private class SearchEventRequestSuccessListener implements com.android.volley.Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            SearchEventResponse searchEventResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchEventResponse.class);
            if (searchEventResponse.getMessage().equalsIgnoreCase(Constant.NO_EVENTS_FOUND_RESPONSE)) {
                Util.alertDilogBox(Constant.LOCATION_NOT_FOUND, Constant.ALERT_TITLE, ActiveSessionActivity.this);
            } else {
                List<MapData> mapDataList = new ArrayList<>();
                List<SearchEventResponse.Data> mList = searchEventResponse.getData();
                if (isGroup) {
                    List<GroupMemberDataList> grpMembersOfParticularGroupId = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
                    for (SearchEventResponse.Data data : mList) {
                        for (GroupMemberDataList grpMembers : grpMembersOfParticularGroupId) {
                            if (grpMembers.getUserId().equalsIgnoreCase(data.getUserId())) {
                                MapData mapData = new MapData();
                                mapData.setLatitude(data.getLocation().getLat());
                                mapData.setLongitude(data.getLocation().getLng());
                                mapData.setName(grpMembers.getName());
                                mapDataList.add(mapData);
                            }
                        }
                    }
                } else {
                    if (!mList.isEmpty()) {
                        MapData mapData = new MapData();
                        mapData.setLatitude(mList.get(0).getLocation().getLat());
                        mapData.setLongitude(mList.get(0).getLocation().getLng());
                        mapData.setName(groupMemberName);
                        mapDataList.add(mapData);
                    }
                }
                Intent intent = new Intent(ActiveSessionActivity.this, MapsActivity.class);
                intent.putParcelableArrayListExtra(Constant.MAP_DATA, (ArrayList<? extends Parcelable>) mapDataList);
                startActivity(intent);
            }
        }
    }

    /**
     * Search Event Request API Call Error listener
     */
    private class SearchEventRequestErrorListener implements com.android.volley.Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.FETCH_LOCATION_ERROR, Constant.ALERT_TITLE, ActiveSessionActivity.this);
        }
    }

    /**
     * Checks if any group/member is in active state or else display no active member found
     */
    private void isAnyMemberActive() {
        /*if (listOnActiveSession.isEmpty()) {
            mRecyclerList.setVisibility(View.INVISIBLE);
            activeMemberPresent.setVisibility(View.VISIBLE);
        } else {
            activeMemberPresent.setVisibility(View.GONE);
            mRecyclerList.setVisibility(View.VISIBLE);
            trackingTitle.setVisibility(View.VISIBLE);
            trackedTitle.setVisibility(View.VISIBLE);
            trackingList.setVisibility(View.VISIBLE);
        }*/
    }

    /**
     * Make Remove from Group API Call using retrofit
     *
     * @param phoneNumber
     * @param groupId
     */
    private void makeRemoveAPICall(String phoneNumber, String groupId) {
        DBManager mDbManager = new DBManager(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ExitRemoveDeleteAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExitRemoveDeleteAPI api = retrofit.create(ExitRemoveDeleteAPI.class);
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(phoneNumber);
        consent.setStatus(Constant.REMOVED);
        exitRemovedGroupData.setConsent(consent);
        RequestBody body = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), new Gson().toJson(exitRemovedGroupData));
        Call<ResponseBody> call = api.deleteGroupDetails(Constant.BEARER + mDbManager.getAdminLoginDetail().getUserToken(),
                Constant.APPLICATION_JSON, mDbManager.getAdminLoginDetail().getUserId(), Constant.SESSION_GROUPS, groupId, body);
        Util.getInstance().showProgressBarDialog(this);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Util.progressDialog.dismiss();
                    Toast.makeText(ActiveSessionActivity.this, Constant.REMOVE_FROM_GROUP_SUCCESS, Toast.LENGTH_SHORT).show();
                    mDbManager.deleteSelectedDataFromGroup(groupId);
                    mDbManager.deleteSelectedDataFromGroupMember(groupId);
                    mTrackedByYouListAdapter.removeItem(position);
                    addDatainList();
                    isAnyMemberActive();
                } else {
                    Util.progressDialog.dismiss();
                    Util.alertDilogBox(errorMessage, Constant.ALERT_TITLE, ActiveSessionActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(errorMessage, Constant.ALERT_TITLE, ActiveSessionActivity.this);
            }
        });
    }

    /**
     * Make Exit from group API Call using retrofit
     *
     * @param phoneNumber
     * @param groupId
     */
    private void makeExitAPICall(String phoneNumber, String groupId) {
        DBManager mDbManager = new DBManager(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ExitRemoveDeleteAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExitRemoveDeleteAPI api = retrofit.create(ExitRemoveDeleteAPI.class);
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(phoneNumber);
        consent.setStatus(Constant.EXITED);
        exitRemovedGroupData.setConsent(consent);
        RequestBody body = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), new Gson().toJson(exitRemovedGroupData));
        Call<ResponseBody> call = api.deleteGroupDetails(Constant.BEARER + mDbManager.getAdminLoginDetail().getUserToken(),
                Constant.APPLICATION_JSON, mDbManager.getAdminLoginDetail().getUserId(), Constant.SESSION_GROUPS, groupId, body);
        Util.getInstance().showProgressBarDialog(this);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Util.progressDialog.dismiss();
                    Toast.makeText(ActiveSessionActivity.this, Constant.EXIT_FROM_GROUP_SUCCESS, Toast.LENGTH_SHORT).show();
                    mDbManager.deleteSelectedDataFromGroupMember(groupId);
                    mTrackedByYouListAdapter.removeItem(position);
                    addDatainList();
                    isAnyMemberActive();
                } else {
                    Util.progressDialog.dismiss();
                    Util.alertDilogBox(errorMessage, Constant.ALERT_TITLE, ActiveSessionActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(errorMessage, Constant.ALERT_TITLE, ActiveSessionActivity.this);
            }
        });
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
            isAnyMemberActive();
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements com.android.volley.Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GET_GROUP_INFO_PER_USER_ERROR, Constant.ALERT_TITLE, ActiveSessionActivity.this);
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
        listOnActiveSession = new ArrayList<>();

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
                listOnActiveSession.add(homeActivityListData);
            }
        }

        // Adding Tracking you in list of Active session
        List<TrackingYou> trackingYouList = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            for (GroupMemberDataList groupMemberDataList : mGroupMemberList) {
                if (!userId.equalsIgnoreCase(data.getGroupOwnerUserId())
                        && data.getStatus().equalsIgnoreCase(Constant.ACTIVE)
                        && data.getGroupId().equalsIgnoreCase(groupMemberDataList.getGroupId())
                        && groupMemberDataList.getUserId().equalsIgnoreCase(userId)
                        && (groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.APPROVED))) {
                    TrackingYou trackingYou = new TrackingYou();
                    trackingYou.setGroupOwnerName(data.getGroupOwnerName());
                    trackingYou.setGroupOwnerPhoneNumber(data.getGroupOwnerPhoneNumber());
                    trackingYou.setGroupOwnerUserId(data.getGroupOwnerUserId());
                    trackingYou.setGroupId(data.getGroupId());
                    trackingYou.setGroupName(data.getGroupName());
                    trackingYouList.add(trackingYou);
                }
            }
        }

        /*for (GroupMemberDataList groupMemberDataList : mGroupMemberList) {
            GroupMemberDataList data = new GroupMemberDataList();
            if (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName() != null
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getStatus().equalsIgnoreCase(Constant.ACTIVE)
                    && !groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.EXITED)
                    && !groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.REMOVED)) {
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
        }*/
        mTrackedByYouListAdapter = new TrackedByYouListAdapter(listOnActiveSession, this);
        mTrackingYouListAdapter = new TrackingYouListAdapter(trackingYouList, this);
        trackingByYouListRecyclerView.setAdapter(mTrackedByYouListAdapter);
        trackingYouListRecyclerView.setAdapter(mTrackingYouListAdapter);
    }
}
