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

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.GroupListAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    private DBManager mDbManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        mDbManager = new DBManager(getActivity());
        displayGroupDataInDashboard(view);
        return view;
    }

    private void displayGroupDataInDashboard(View view) {
        RecyclerView groupListRecyclerView = view.findViewById(R.id.groupListRecyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        groupListRecyclerView.setLayoutManager(mLayoutManager);

        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        List<HomeActivityListData> groupList = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            if (data.getCreatedBy() != null && data.getCreatedBy().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getUserId())) {
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
                    groupList.add(homeActivityListData);
                }
            }
        }
        GroupListAdapter groupListAdapter = new GroupListAdapter(groupList, getContext());
        groupListRecyclerView.setAdapter(groupListAdapter);
    }
}