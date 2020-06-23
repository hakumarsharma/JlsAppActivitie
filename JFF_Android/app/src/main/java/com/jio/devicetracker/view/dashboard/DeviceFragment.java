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

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.DeviceListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class DeviceFragment extends Fragment {

    private static CardView cardInstruction;
    private static ImageView instructionIcon;
    private DBManager mDbManager;
    private RecyclerView deviceFragmentRecyclerView;
    private static List<HomeActivityListData> groupList;

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
        deviceFragmentRecyclerView = view.findViewById(R.id.deviceListRecyclerView);
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
        DeviceListAdapter deviceListAdapter = new DeviceListAdapter(groupList, getActivity());
        deviceFragmentRecyclerView.setAdapter(deviceListAdapter);
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
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GET_GROUP_INFO_PER_USER_ERROR, Constant.ALERT_TITLE, getActivity());
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
            if (data.getStatus() != null && (data.getStatus().equalsIgnoreCase(Constant.ACTIVE)) || (data.getStatus().equalsIgnoreCase(Constant.SCHEDULED)) || (data.getStatus().equalsIgnoreCase(Constant.COMPLETED))) {
                for (HomeActivityListData groupDbData : groupDetailList) {
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
                    mGroupMemberDataLists.add(groupMemberDataList);
                }
            }
        }
        mDbManager.insertAllDataIntoGroupTable(groupList);
        mDbManager.insertGroupMemberDataInListFormat(mGroupMemberDataLists);
    }

}