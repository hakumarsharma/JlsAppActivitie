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

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.database.pojo.SearchEventData;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.request.SearchEventRequest;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.database.pojo.response.SearchEventResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.DeviceListAdapter;
import com.jio.devicetracker.view.location.LocationActivity;

import java.util.ArrayList;
import java.util.List;


public class DeviceFragment extends Fragment {

    private static CardView cardInstruction;
    private static ImageView instructionIcon;
    private DBManager mDbManager;
    private RecyclerView deviceFragmentRecyclerView;
    private static List<HomeActivityListData> groupList;
    private DeviceListAdapter deviceListAdapter;
    private String groupId;
    private HomeActivityListData homeActivityListData;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        cardInstruction = view.findViewById(R.id.instruction_card);
        TextView instruction1 = view.findViewById(R.id.device_instruction1);
        instruction1.setTypeface(Util.mTypeface(getActivity(), 5));
        TextView instruction2 = view.findViewById(R.id.device_instruction2);
        instruction2.setTypeface(Util.mTypeface(getActivity(), 3));
        instructionIcon = view.findViewById(R.id.devices_default_icon);
        cardInstruction.setVisibility(View.VISIBLE);
        instructionIcon.setVisibility(View.VISIBLE);
        mDbManager = new DBManager(getActivity());
        userId = mDbManager.getAdminLoginDetail().getUserId();
        deviceFragmentRecyclerView = view.findViewById(R.id.deviceListRecyclerView);
        adapterEventListener();
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage){
        CustomAlertActivity alertActivity = new CustomAlertActivity(getContext());
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    @Override
    public void onStart() {
        super.onStart();
        makeGroupInfoPerUserRequestAPICall();
    }

    private void displayGroupDataInDashboard() {
        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        List<HomeActivityListData> mGroupIconList = mDbManager.getAllGroupIconTableData();
        groupList = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            if (data.getCreatedBy() != null && data.getCreatedBy().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getUserId()) && data.getGroupName().equals(Constant.INDIVIDUAL_DEVICE_GROUP_NAME)) {
                List<GroupMemberDataList> memberDataList = mDbManager.getAllGroupMemberDataBasedOnGroupId(data.getGroupId());
                for (GroupMemberDataList memberData : memberDataList) {
                    HomeActivityListData homeActivityListData = new HomeActivityListData();
                    homeActivityListData.setGroupName(memberData.getName());
                    homeActivityListData.setPhoneNumber(memberData.getNumber());
                    homeActivityListData.setConsentStaus(memberData.getConsentStatus());
                    homeActivityListData.setConsentId(memberData.getConsentId());
                    homeActivityListData.setGroupId(data.getGroupId());
                    homeActivityListData.setStatus(data.getStatus());
                    homeActivityListData.setCreatedBy(data.getCreatedBy());
                    homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                    homeActivityListData.setProfileImage(data.getProfileImage());
                    homeActivityListData.setFrom(data.getFrom());
                    homeActivityListData.setTo(data.getTo());
                    homeActivityListData.setDeviceId(data.getDeviceId());
                    for (HomeActivityListData mHomeActivityListData : mGroupIconList) {
                        if (mHomeActivityListData.getGroupId().equalsIgnoreCase(data.getGroupId())) {
                            homeActivityListData.setGroupIcon(mHomeActivityListData.getGroupIcon());
                        }
                    }
                    groupList.add(homeActivityListData);
                }
            }
        }
        checkMemberPresent();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        deviceFragmentRecyclerView.setLayoutManager(mLayoutManager);
        deviceListAdapter = new DeviceListAdapter(groupList, getActivity());
        deviceFragmentRecyclerView.setAdapter(deviceListAdapter);
    }

    /**
     * Adapter Listener
     */
    private void adapterEventListener() {
        if (deviceListAdapter != null) {
            deviceListAdapter.setOnItemClickPagerListener(homeActivityListData -> {
                makeGetLocationAPICall(homeActivityListData);
            });
        }
    }

    /**
     * find locations for group members
     *
     * @param homeActivityListData
     */
    private void makeGetLocationAPICall(HomeActivityListData homeActivityListData) {
        this.groupId = homeActivityListData.getGroupId();
        this.homeActivityListData = homeActivityListData;
        SearchEventData searchEventData = new SearchEventData();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.LOCATION);
        mList.add(Constant.SOS);
        searchEventData.setTypes(mList);
        Util.getInstance().showProgressBarDialog(getActivity());
        GroupRequestHandler.getInstance(getActivity()).handleRequest(new SearchEventRequest(new SearchEventRequestSuccessListener(), new SearchEventRequestErrorListener(), searchEventData, userId, groupId, Constant.GET_LOCATION_URL));
    }

    /**
     * Search Event Request API call Success Listener
     */
    private class SearchEventRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            SearchEventResponse searchEventResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchEventResponse.class);
            List<MapData> mapDataList = new ArrayList<>();
            List<SearchEventResponse.Data> mList = searchEventResponse.getData();
            if (!mList.isEmpty()) {
                List<GroupMemberDataList> grpMembersOfParticularGroupId = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
                for (SearchEventResponse.Data data : mList) {
                    for (GroupMemberDataList grpMembers : grpMembersOfParticularGroupId) {
                        if (grpMembers.getDeviceId() != null && grpMembers.getDeviceId().equalsIgnoreCase(data.getDevice()) && grpMembers.getUserId().equalsIgnoreCase(data.getUserId()) && (grpMembers.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_APPROVED) || grpMembers.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_PENDING) || grpMembers.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_EXPIRED))) {
                            MapData mapData = new MapData();
                            mapData.setLatitude(data.getLocation().getLat());
                            mapData.setLongitude(data.getLocation().getLng());
                            mapData.setName(grpMembers.getName());
                            mapData.setConsentId(grpMembers.getConsentId());
                            mapDataList.add(mapData);
                        }
                    }
                }
            }
            goToMapActivity(mapDataList);
        }
    }

    /**
     * Navigates to the Map activity
     */
    private void goToMapActivity(List<MapData> mapDataList) {
        Intent intent = new Intent(getContext(), LocationActivity.class);
        intent.putParcelableArrayListExtra(Constant.MAP_DATA, (ArrayList<? extends Parcelable>) mapDataList);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.GROUP_STATUS, homeActivityListData.getStatus());
        startActivity(intent);
    }


    /**
     * Search Event Request API Call Error listener
     */
    private class SearchEventRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            showCustomAlertWithText(Constant.FETCH_LOCATION_ERROR);
        }

    }

    /**
     * Get All Group info per user API Call
     */
    protected void makeGroupInfoPerUserRequestAPICall() {
        GroupRequestHandler.getInstance(getActivity()).handleRequest(new GetGroupInfoPerUserRequest(new GetGroupInfoPerUserRequestSuccessListener(), new GetGroupInfoPerUserRequestErrorListener(), mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * GetGroupInfoPerUserRequest Success listener
     */
    private class GetGroupInfoPerUserRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetGroupInfoPerUserResponse getGroupInfoPerUserResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetGroupInfoPerUserResponse.class);
            parseResponseStoreInDatabase(getGroupInfoPerUserResponse);
            displayGroupDataInDashboard();
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

    public static void checkMemberPresent() {
        if (groupList != null && !groupList.isEmpty()) {
            cardInstruction.setVisibility(View.INVISIBLE);
            instructionIcon.setVisibility(View.INVISIBLE);
        } else {
            cardInstruction.setVisibility(View.VISIBLE);
            instructionIcon.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Parse the response and store in DB(Group Table and Member table)
     */
    public void parseResponseStoreInDatabase(GetGroupInfoPerUserResponse getGroupInfoPerUserResponse) {
        List<HomeActivityListData> groupList = new ArrayList<>();
        List<GroupMemberDataList> mGroupMemberDataLists = new ArrayList<>();
        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
            if (data.getStatus() != null && (data.getStatus().equalsIgnoreCase(Constant.ACTIVE) || data.getStatus().equalsIgnoreCase(Constant.SCHEDULED) || data.getStatus().equalsIgnoreCase(Constant.COMPLETED))) {
                for (HomeActivityListData groupDbData : groupDetailList) {
                    HomeActivityListData homeActivityListData = new HomeActivityListData();
                    homeActivityListData.setGroupName(data.getGroupName());
                    homeActivityListData.setCreatedBy(data.getCreatedBy());
                    homeActivityListData.setGroupId(data.getId());
                    homeActivityListData.setStatus(data.getStatus());
                    homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                    homeActivityListData.setFrom(data.getSession().getFrom());
                    homeActivityListData.setTo(data.getSession().getTo());
                    int count = 0;
                    for (GetGroupInfoPerUserResponse.Consents consentData : data.getConsents()) {
                        if ((!consentData.getUserId().equalsIgnoreCase(userId)) && (consentData.getStatus().equalsIgnoreCase(Constant.CONSET_STATUS_APPROVED) || consentData.getStatus().equalsIgnoreCase(Constant.CONSET_STATUS_PENDING) || consentData.getStatus().equalsIgnoreCase(Constant.CONSET_STATUS_EXPIRED))) {
                            count = count + 1;
                            homeActivityListData.setConsentsCount(count);
                        }
                    }
                    if(!data.getGroupOwner().isEmpty()) {
                        homeActivityListData.setGroupOwnerName(data.getGroupOwner().get(0).getName());
                        homeActivityListData.setGroupOwnerPhoneNumber(data.getGroupOwner().get(0).getPhone());
                        homeActivityListData.setGroupOwnerUserId(data.getGroupOwner().get(0).getUserId());
                    }
                    if (data.getId().equals(groupDbData.getGroupId())) {
                        if (groupDbData.getGroupIcon() != null) {
                            homeActivityListData.setGroupIcon(groupDbData.getGroupIcon());
                        }
                    }
                    groupList.add(homeActivityListData);
                }
            }
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
                    groupMemberDataList.setDeviceId(mConsents.getDevice());
                    mGroupMemberDataLists.add(groupMemberDataList);
                }
            }
        }
        mDbManager.insertAllDataIntoGroupTable(groupList);
        mDbManager.insertGroupMemberDataInListFormat(mGroupMemberDataLists);
    }

}