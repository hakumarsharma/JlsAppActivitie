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

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
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
import com.jio.devicetracker.database.pojo.request.DeleteGroupRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.request.SearchEventRequest;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.database.pojo.response.SearchEventResponse;
import com.jio.devicetracker.network.ExitRemoveDeleteAPI;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.ActiveSessionListAdapter;

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
public class ActiveSessionActivity extends AppCompatActivity {
    private ActiveSessionListAdapter mAdapter;
    private RecyclerView mRecyclerList;
    private List listOnActiveSession;
    private TextView activeMemberPresent;
    private int position;
    private String errorMessage;
    private String groupId;
    private String groupMemberName;
    private DBManager mDbManager;
    private boolean isGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.ACTIVE_SESSION_TITLE);
        mRecyclerList = findViewById(R.id.activeSessionsList);
        activeMemberPresent = findViewById(R.id.activeMemberPresent);
        mDbManager = new DBManager(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
        /*addDatainList();
        adapterEventListener();
        isAnyMemberActive();*/
        makeGroupInfoPerUserRequestAPICall();
    }

    private void adapterEventListener() {
        mAdapter.setOnItemClickPagerListener(new ActiveSessionListAdapter.RecyclerViewClickListener() {
            @Override
            public void clickOnListLayout(int image, String groupName, String groupId, String createdBy) {
                if (image == R.drawable.ic_group_button) {
                    Intent intent = new Intent(ActiveSessionActivity.this, ActiveMemberActivity.class);
                    intent.putExtra(Constant.GROUPNAME, groupName);
                    intent.putExtra(Constant.GROUP_ID, groupId);
                    intent.putExtra(Constant.CREATED_BY, createdBy);
                    ActiveSessionActivity.this.startActivity(intent);
                }
            }

            /**
             * Exit or Remove API call for Group
             * @param v
             * @param position
             * @param homeActivityListData
             */
            @Override
            public void onPopupMenuClickedForGroup(View v, int position, HomeActivityListData homeActivityListData) {
                PopupMenu popup = new PopupMenu(ActiveSessionActivity.this, v);
                ActiveSessionActivity.this.position = position;
                DBManager mDbManager = new DBManager(ActiveSessionActivity.this);
                String userId = mDbManager.getAdminLoginDetail().getUserId();
                ActiveSessionActivity.this.groupId = homeActivityListData.getGroupId();
                popup.getMenu().add(Menu.NONE, 1, 1, Constant.TRACK);
                if (homeActivityListData.getCreatedBy() != null && homeActivityListData.getCreatedBy().equalsIgnoreCase(userId)) { // Check through updated by not by isGroupAdmin
                    popup.getMenu().add(Menu.NONE, 2, 2, Constant.REMOVE);
                    errorMessage = Constant.REMOVE_FROM_GROUP_FAILURE;
                } else {
                    popup.getMenu().add(Menu.NONE, 3, 3, Constant.EXIT);
                    errorMessage = Constant.EXIT_FROM_GROUP_FAILURE;
                }
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case 1:
                            trackUserForGroup(homeActivityListData);
                            break;
                        case 2:
                            ActiveSessionActivity.this.makeDeleteGroupAPICall(userId, homeActivityListData.getGroupId());
                            break;
                        case 3:
                            ActiveSessionActivity.this.makeExitAPICall(mDbManager.getAdminLoginDetail().getPhoneNumber(), homeActivityListData.getGroupId());
                            break;
                        default:
                            break;
                    }
                    return false;
                });
                popup.show();
            }

            /**
             * Exit or Remove API call for Group Member
             * @param v
             * @param position
             * @param groupMemberDataList
             */
            @Override
            public void onPopupMenuClickedForMember(View v, int position, GroupMemberDataList groupMemberDataList) {
                PopupMenu popup = new PopupMenu(ActiveSessionActivity.this, v);
                ActiveSessionActivity.this.position = position;
                DBManager mDbManager = new DBManager(ActiveSessionActivity.this);
                String userId = mDbManager.getAdminLoginDetail().getUserId();
                String createdBy = mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getCreatedBy();
                popup.getMenu().add(Menu.NONE, 1, 1, Constant.TRACK);
                if (createdBy != null && createdBy.equalsIgnoreCase(userId)) { // Check through updated by not by isGroupAdmin
                    popup.getMenu().add(Menu.NONE, 2, 2, Constant.REMOVE);
                    errorMessage = Constant.REMOVE_FROM_GROUP_FAILURE;
                } else {
                    popup.getMenu().add(Menu.NONE, 3, 3, Constant.EXIT);
                    errorMessage = Constant.EXIT_FROM_GROUP_FAILURE;
                }
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case 1:
                            trackUser(groupMemberDataList);
                            break;
                        case 2:
                            ActiveSessionActivity.this.makeRemoveAPICall(groupMemberDataList.getNumber(), groupMemberDataList.getGroupId());
                            break;
                        case 3:
                            ActiveSessionActivity.this.makeExitAPICall(groupMemberDataList.getNumber(), groupMemberDataList.getGroupId());
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
                    for (SearchEventResponse.Data data : mList) {
                        MapData mapData = new MapData();
                        mapData.setLatitude(data.getLocation().getLat());
                        mapData.setLongitude(data.getLocation().getLng());
                        mapData.setName(groupMemberName);
                        mapDataList.add(mapData);
                        break;
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
        if (listOnActiveSession.isEmpty()) {
            mRecyclerList.setVisibility(View.INVISIBLE);
            activeMemberPresent.setVisibility(View.VISIBLE);
        } else {
            activeMemberPresent.setVisibility(View.GONE);
            mRecyclerList.setVisibility(View.VISIBLE);
        }
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
                    mAdapter.removeItem(position);
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
                    mAdapter.removeItem(position);
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
     * Delete the Group and update the database
     */
    private void makeDeleteGroupAPICall(String userId, String groupId) {
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new DeleteGroupRequest(new DeleteGroupRequestSuccessListener(), new DeleteGroupRequestErrorListener(), groupId, userId));
    }

    /**
     * Delete Group Request API Call Success Listener
     */
    private class DeleteGroupRequestSuccessListener implements com.android.volley.Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            DBManager mDbManager = new DBManager(ActiveSessionActivity.this);
            mDbManager.deleteSelectedDataFromGroup(groupId);
            mDbManager.deleteSelectedDataFromGroupMember(groupId);
            mAdapter.removeItem(position);
            addDatainList();
            isAnyMemberActive();
        }
    }

    /**
     * Delete Group Request API Call Error Listener
     */
    private class DeleteGroupRequestErrorListener implements com.android.volley.Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.GROUP_DELETION_FAILURE, Constant.ALERT_TITLE, ActiveSessionActivity.this);
        }
    }


    /**
     * To Refresh our Home page call Get all for one user API Call
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        makeGroupInfoPerUserRequestAPICall();
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
            GetGroupInfoPerUserResponse getGroupInfoPerUserResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetGroupInfoPerUserResponse.class);
            DashboardActivity dashboardActivity = new DashboardActivity();
            dashboardActivity.parseResponseStoreInDatabase(getGroupInfoPerUserResponse);
            dashboardActivity.addDataInHomeScreen();
            dashboardActivity.isDevicePresent();
            addDatainList();
            adapterEventListener();
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
        mAdapter = new ActiveSessionListAdapter(listOnActiveSession);
        mRecyclerList.setAdapter(mAdapter);
    }
}
