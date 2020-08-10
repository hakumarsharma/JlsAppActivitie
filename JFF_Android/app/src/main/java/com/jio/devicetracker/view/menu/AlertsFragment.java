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
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.view.adapter.AlertsFragmentAdapter;
import com.jio.devicetracker.view.geofence.GeofenceMapFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertsFragment extends Fragment implements View.OnClickListener {

    private List<AlertHistoryData> alertHistoryData;
    private TextView alertsMemberName;
    private TextView alertMemberAddress;
    private DBManager mDbManager;
    private int position = 0;
    private TextView alertNumbers;
    private List<AlertHistoryData> mAlertHistoryListData;
    private RecyclerView mRecyclerView;
    private String currentDateValues = Constant.EMPTY_STRING;

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
        alertHistoryData = new ArrayList<>();
        alertNumbers = view.findViewById(R.id.alertNumbers);
        getIndividualMemberList();
        getAlertsHistoryAndDisplay(GeofenceMapFragment.consentId, view);
        return view;
    }

    private void getIndividualMemberList() {
        List<GroupMemberDataList> groupMemberDataLists = mDbManager.getAllGroupMemberData();
        for (GroupMemberDataList groupMemberDataList : groupMemberDataLists) {
            if (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName() != null
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getCreatedBy().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getUserId())
                    && (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)
                    || mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_DEVICE_GROUP_NAME))
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getStatus().equalsIgnoreCase(Constant.ACTIVE)
                    && !groupMemberDataList.getNumber().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getPhoneNumber())
                    && groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.APPROVED)) {
                AlertHistoryData mAlertHistoryData = new AlertHistoryData();
                mAlertHistoryData.setName(groupMemberDataList.getName());
                mAlertHistoryData.setNumber(groupMemberDataList.getNumber());
                mAlertHistoryData.setAddress(groupMemberDataList.getAddress());
                mAlertHistoryData.setConsentId(groupMemberDataList.getConsentId());
                alertHistoryData.add(mAlertHistoryData);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.alertBackButton) {
            if (position > 0) {
                currentDateValues = Constant.EMPTY_STRING;
                displayAlertHistory(alertHistoryData.get(--position).getConsentId());
            }
        } else if (v.getId() == R.id.alertNextButton) {
            if (position < alertHistoryData.size() - 1) {
                currentDateValues = Constant.EMPTY_STRING;
                displayAlertHistory(alertHistoryData.get(++position).getConsentId());
            }
        }
    }

    // Displays the name, address and alert count of user who breached the Geofence
    private void getAlertsHistoryAndDisplay(String consentId, View view) {
        mAlertHistoryListData = mDbManager.getHistoryTableData(consentId);
        List<AlertHistoryData> setInAdapterList = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.alertsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        try {
            for (AlertHistoryData alertHistoryData : mAlertHistoryListData) {
                AlertHistoryData mData = new AlertHistoryData();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                Long oldDateLong = simpleDateFormat.parse(alertHistoryData.getDate()).getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                Date parsedDate = sdf.parse(new Date().toString());
                String todayDate = simpleDateFormat.format(parsedDate);
                Long todayDateLong = simpleDateFormat.parse(todayDate).getTime();
                long diffInMillies = todayDateLong - oldDateLong;
                int diffhours = (int) (diffInMillies / (60 * 60 * 1000));
                if (diffhours > 12) {
                    if (!currentDateValues.equalsIgnoreCase(Constant.YESTERDAY) && ((alertHistoryData.getDate().contains("pm") && todayDate.contains("pm"))
                            || (alertHistoryData.getDate().contains("am") && todayDate.contains("am")))) {
                        mData.setDay(Constant.YESTERDAY);
                        currentDateValues = Constant.YESTERDAY;
                    }
                } else if (diffhours < 12) {
                    if (!currentDateValues.equalsIgnoreCase(Constant.TODAY)) {
                        mData.setDay(Constant.TODAY);
                        currentDateValues = Constant.TODAY;
                    }
                } else if (diffhours >= 24 && diffhours <= 48) {
                    if (!currentDateValues.equalsIgnoreCase(Constant.YESTERDAY)) {
                        mData.setDay(Constant.YESTERDAY);
                        currentDateValues = Constant.YESTERDAY;
                    }
                } else {
                    mData.setDay(alertHistoryData.getDate());
                    currentDateValues = alertHistoryData.getDate();
                }
                mData.setName(alertHistoryData.getName());
                mData.setAddress(alertHistoryData.getAddress());
                mData.setNumber(alertHistoryData.getNumber());
                mData.setDate(alertHistoryData.getDate());
                mData.setState(alertHistoryData.getState());
                setInAdapterList.add(mData);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        AlertsFragmentAdapter alertsFragmentAdapter = new AlertsFragmentAdapter(setInAdapterList);
        mRecyclerView.setAdapter(alertsFragmentAdapter);
        if (mAlertHistoryListData.size() < 10) {
            alertNumbers.setText("0" + mAlertHistoryListData.size());
        } else {
            alertNumbers.setText(mAlertHistoryListData.size());
        }
        alertNumbers.setText(String.valueOf(mAlertHistoryListData.size()));
        if (!mAlertHistoryListData.isEmpty()) {
            alertsMemberName.setText(String.valueOf(mAlertHistoryListData.get(mAlertHistoryListData.size() - 1).getName()));
            alertMemberAddress.setText(String.valueOf(mAlertHistoryListData.get(mAlertHistoryListData.size() - 1).getAddress()));
        }
    }

    // Displays the name, address and alert count of user after clicking on next/back button
    private void displayAlertHistory(String consentId) {
        mAlertHistoryListData = mDbManager.getHistoryTableData(consentId);
        List<AlertHistoryData> setInAdapterList = new ArrayList<>();
        if (mAlertHistoryListData != null && mAlertHistoryListData.isEmpty()) {
            showCustomAlertWithText(Constant.ALERTS_ERRORS);
            return;
        } else {
            alertsMemberName.setText(mAlertHistoryListData.get(0).getName());
        }
        if (mAlertHistoryListData != null && mAlertHistoryListData.isEmpty()) {
            showCustomAlertWithText(Constant.ALERTS_ERRORS);
            return;
        } else {
            alertMemberAddress.setText(mAlertHistoryListData.get(0).getAddress());
        }
        try {
            for (AlertHistoryData alertHistoryData : mAlertHistoryListData) {
                AlertHistoryData mData = new AlertHistoryData();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                Long oldDateLong = simpleDateFormat.parse(alertHistoryData.getDate()).getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                Date parsedDate = sdf.parse(new Date().toString());
                String todayDate = simpleDateFormat.format(parsedDate);
                Long todayDateLong = simpleDateFormat.parse(todayDate).getTime();
                long diffInMillies = todayDateLong - oldDateLong;
                int diffhours = (int) (diffInMillies / (60 * 60 * 1000));
                if (diffhours > 12) {
                    if (!currentDateValues.equalsIgnoreCase(Constant.YESTERDAY) && ((alertHistoryData.getDate().contains("pm") && todayDate.contains("pm"))
                            || (alertHistoryData.getDate().contains("am") && todayDate.contains("am")))) {
                        mData.setDay(Constant.YESTERDAY);
                        currentDateValues = Constant.YESTERDAY;
                    }
                } else if (diffhours < 12) {
                    if (!currentDateValues.equalsIgnoreCase(Constant.TODAY)) {
                        mData.setDay(Constant.TODAY);
                        currentDateValues = Constant.TODAY;
                    }
                } else if (diffhours >= 24 && diffhours <= 48) {
                    if (!currentDateValues.equalsIgnoreCase(Constant.YESTERDAY)) {
                        mData.setDay(Constant.YESTERDAY);
                        currentDateValues = Constant.YESTERDAY;
                    }
                } else {
                    mData.setDay(alertHistoryData.getDate());
                    currentDateValues = alertHistoryData.getDate();
                }
                mData.setName(alertHistoryData.getName());
                mData.setAddress(alertHistoryData.getAddress());
                mData.setNumber(alertHistoryData.getNumber());
                mData.setDate(alertHistoryData.getDate());
                mData.setState(alertHistoryData.getState());
                setInAdapterList.add(mData);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.removeAllViewsInLayout();
        mRecyclerView.setLayoutManager(mLayoutManager);
        AlertsFragmentAdapter alertsFragmentAdapter = new AlertsFragmentAdapter(setInAdapterList);
        mRecyclerView.setAdapter(alertsFragmentAdapter);
        alertNumbers.setText(String.valueOf(mAlertHistoryListData.size()));
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(getActivity());
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

}
