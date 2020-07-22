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
package com.jio.devicetracker.view.location;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.PeopleListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapPeopleListFragment extends Fragment {

    private DBManager mDbManager;
    private static PeopleListAdapter peopleListAdapter;
    private List<MapData> mapDataList;
    private String groupId;
    private Locale locale = Locale.ENGLISH;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_map_peoplelist, container, false);
        mDbManager = new DBManager(getActivity());
        mapDataList = getActivity().getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);
        groupId = getActivity().getIntent().getStringExtra(Constant.GROUP_ID);
        displayGroupDataInDashboard(view);
        return view;
    }

    // If location is not available then just show the group member details else display the member with the location details
    private void displayGroupDataInDashboard(View view) {
        RecyclerView groupListRecyclerView = view.findViewById(R.id.memberDetailsList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        groupListRecyclerView.setLayoutManager(mLayoutManager);
        List<GroupMemberDataList> groupMemberList = new ArrayList<>();
        List<GroupMemberDataList> allGroupMemberDataBasedOnGroupId = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
        if (!mapDataList.isEmpty()) {
            int count = 0;
            for (MapData mapData : mapDataList) {
                for (GroupMemberDataList groupMemberDataList : allGroupMemberDataBasedOnGroupId) {
                    if (groupMemberDataList.getConsentId().equalsIgnoreCase(mapData.getConsentId())
                            && !groupMemberDataList.getNumber().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getPhoneNumber())
                            && (groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_APPROVED) || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_PENDING) || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_EXPIRED))) {
                        GroupMemberDataList groupDataList = new GroupMemberDataList();
                        groupDataList.setGroupId(groupMemberDataList.getGroupId());
                        groupDataList.setName(groupMemberDataList.getName());
                        groupDataList.setNumber(groupMemberDataList.getNumber());
                        groupDataList.setConsentStatus(groupMemberDataList.getConsentStatus().substring(0, 1).toUpperCase(locale) + groupMemberDataList.getConsentStatus().substring(1));
                        groupDataList.setAddress(getAddressFromLocation(mapData.getLatitude(), mapData.getLongitude()));
                        groupDataList.setConsentId(mapData.getConsentId());
                        groupMemberList.add(groupDataList);
                        count++;
                    }
                }
            }
            // If group member is present but address is not present for few group members
            if (count != allGroupMemberDataBasedOnGroupId.size()) {
                for (GroupMemberDataList groupMemberDataList : allGroupMemberDataBasedOnGroupId) {
                    boolean isFound = false;
                    for (GroupMemberDataList grpMemberList : groupMemberList) {
                        if (groupMemberDataList.getConsentId().equalsIgnoreCase(grpMemberList.getConsentId())) {
                            isFound = true;
                        }
                    }
                    if (!isFound && (groupMemberDataList.getConsentStatus().equals(Constant.CONSET_STATUS_APPROVED) || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_PENDING) || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_EXPIRED))) {
                        GroupMemberDataList groupDataList = new GroupMemberDataList();
                        groupDataList.setGroupId(groupMemberDataList.getGroupId());
                        groupDataList.setName(groupMemberDataList.getName());
                        groupDataList.setNumber(groupMemberDataList.getNumber());
                        groupDataList.setConsentStatus(groupMemberDataList.getConsentStatus().substring(0, 1).toUpperCase(locale) + groupMemberDataList.getConsentStatus().substring(1));
                        groupMemberList.add(groupDataList);
                    }
                }
            }
        }
        // Location is not there for any of the group member
        else if (mapDataList.isEmpty()) {
            for (GroupMemberDataList allGroupMemberData : allGroupMemberDataBasedOnGroupId) {
                if (!allGroupMemberData.getUserId().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getUserId())
                        && (allGroupMemberData.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_APPROVED) || allGroupMemberData.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_PENDING) || allGroupMemberData.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_EXPIRED))) {
                    GroupMemberDataList groupDataList = new GroupMemberDataList();
                    groupDataList.setGroupId(allGroupMemberData.getGroupId());
                    groupDataList.setName(allGroupMemberData.getName());
                    groupDataList.setNumber(allGroupMemberData.getNumber());
                    groupDataList.setConsentId(allGroupMemberData.getConsentId());
                    groupDataList.setConsentStatus(allGroupMemberData.getConsentStatus().substring(0, 1).toUpperCase(locale) + allGroupMemberData.getConsentStatus().substring(1));
                    groupMemberList.add(groupDataList);
                } else if (allGroupMemberData.getUserId().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getUserId())
                        && !allGroupMemberData.getNumber().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getPhoneNumber())
                        && (allGroupMemberData.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_APPROVED) || allGroupMemberData.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_PENDING) || allGroupMemberData.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_EXPIRED))) {
                    GroupMemberDataList groupDataList = new GroupMemberDataList();
                    groupDataList.setGroupId(allGroupMemberData.getGroupId());
                    groupDataList.setName(allGroupMemberData.getName());
                    groupDataList.setNumber(allGroupMemberData.getNumber());
                    groupDataList.setConsentId(allGroupMemberData.getConsentId());
                    groupDataList.setConsentStatus(allGroupMemberData.getConsentStatus().substring(0, 1).toUpperCase(locale) + allGroupMemberData.getConsentStatus().substring(1));
                    groupMemberList.add(groupDataList);
                }
            }
        }
        peopleListAdapter = new PeopleListAdapter(groupMemberList, getContext());
        groupListRecyclerView.setAdapter(peopleListAdapter);
    }


    /**
     * Returns real address based on Lat and Long(Geo Coding)
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.ENGLISH);
        StringBuilder strAddress = new StringBuilder();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address fetchedAddress = addresses.get(0);
                strAddress.setLength(0);
                strAddress.append(fetchedAddress.getAddressLine(0)).append(" ");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strAddress.toString();
    }
}
