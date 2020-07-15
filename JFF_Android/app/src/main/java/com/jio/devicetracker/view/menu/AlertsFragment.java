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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AlertHistoryData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.AlertsFragmentAdapter;
import com.jio.devicetracker.view.geofence.GeofenceMapFragment;

import java.util.ArrayList;
import java.util.List;

public class AlertsFragment extends Fragment implements View.OnClickListener {

    private List<AlertHistoryData> alertHistoryData;
    private TextView alertsMemberName;
    private TextView alertMemberAddress;
    private DBManager mDbManager;
    private int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        alertsMemberName = view.findViewById(R.id.alertsMemberName);
        alertMemberAddress = view.findViewById(R.id.alertMemberAddress);
        ImageView alertBackButton = view.findViewById(R.id.alertBackButton);
        ImageView alertNextButton = view.findViewById(R.id.alertNextButton);
        alertBackButton.setOnClickListener(this);
        alertNextButton.setOnClickListener(this);
        mDbManager = new DBManager(getActivity());
        getAlertsHistoryAndDisplay(GeofenceMapFragment.consentId, view);
        alertHistoryData = new ArrayList<>();
        getIndividualMemberList();
        return view;
    }

    private void getIndividualMemberList() {
        List<GroupMemberDataList> groupMemberDataLists = mDbManager.getAllGroupMemberData();
        for (GroupMemberDataList groupMemberDataList : groupMemberDataLists) {
            if (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName() != null
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getStatus().equalsIgnoreCase(Constant.ACTIVE)
                    && groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.APPROVED)) {
                AlertHistoryData mAlertHistoryData = new AlertHistoryData();
                mAlertHistoryData.setName(groupMemberDataList.getName());
                mAlertHistoryData.setNumber(groupMemberDataList.getNumber());
                mAlertHistoryData.setAddress(groupMemberDataList.getAddress());
                alertHistoryData.add(mAlertHistoryData);
            }
        }
    }

    private void displayIndividualUserDetail(int position) {
        if (!alertHistoryData.isEmpty()) {
            alertsMemberName.setText(alertHistoryData.get(position).getName());
            alertMemberAddress.setText(alertHistoryData.get(position).getAddress());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.alertBackButton) {
            if (position > 0) {
                displayIndividualUserDetail(--position);
            }
        } else if (v.getId() == R.id.alertNextButton) {
            if (position < alertHistoryData.size() - 1) {
                displayIndividualUserDetail(++position);
            }
        }
    }

    private void getAlertsHistoryAndDisplay(String consentId, View view) {
        List<AlertHistoryData> mAlertHistoryListData = mDbManager.getHistoryTableData(consentId);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = view.findViewById(R.id.alertsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        AlertsFragmentAdapter alertsFragmentAdapter = new AlertsFragmentAdapter(mAlertHistoryListData);
        mRecyclerView.setAdapter(alertsFragmentAdapter);
    }
}
