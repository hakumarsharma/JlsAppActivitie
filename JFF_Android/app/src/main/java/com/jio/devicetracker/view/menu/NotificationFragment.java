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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.NotificationData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.NotificationFragmentAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NotificationFragment extends Fragment implements View.OnClickListener {

    private DBManager mDbManager;
    private String currentDateValues = Constant.EMPTY_STRING;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDbManager = new DBManager(getActivity());
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        displayAllNotificationData(view);
        return view;
    }


    private void displayAllNotificationData(View view) {
        List<NotificationData> mList = mDbManager.getAllNotificationData();
        List<NotificationData> setInAdapterList = new ArrayList<>();
        try {
            for (NotificationData notificationData : mList) {
                NotificationData mData = new NotificationData();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                Long oldDateLong = simpleDateFormat.parse(notificationData.getNotificationDate()).getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                Date parsedDate = sdf.parse(new Date().toString());
                String todayDate = simpleDateFormat.format(parsedDate);
                Long todayDateLong = simpleDateFormat.parse(todayDate).getTime();
                long diffInMillies = todayDateLong - oldDateLong;
                int diffhours = (int) (diffInMillies / (60 * 60 * 1000));
                if (diffhours > 12) {
                    if (!currentDateValues.equalsIgnoreCase(Constant.YESTERDAY) && ((notificationData.getNotificationDate().contains("pm") && todayDate.contains("pm"))
                            || (notificationData.getNotificationDate().contains("am") && todayDate.contains("am")))) {
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
                    mData.setDay(notificationData.getNotificationDate());
                    currentDateValues = notificationData.getNotificationDate();
                }
                mData.setNotificationTitle(notificationData.getNotificationTitle());
                mData.setNotificationDate(notificationData.getNotificationDate());
                mData.setNotificationMessage(notificationData.getNotificationMessage());
                setInAdapterList.add(mData);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = view.findViewById(R.id.notificationsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        NotificationFragmentAdapter alertsFragmentAdapter = new NotificationFragmentAdapter(setInAdapterList);
        mRecyclerView.setAdapter(alertsFragmentAdapter);
    }

    @Override
    public void onClick(View v) {
        // Todo
    }
}
