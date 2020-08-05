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
import com.jio.devicetracker.view.adapter.NotificationFragmentAdapter;

import java.util.List;


public class NotificationFragment extends Fragment implements View.OnClickListener{

    private DBManager mDbManager;
    private RecyclerView mRecyclerView;

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
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.notificationsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        NotificationFragmentAdapter alertsFragmentAdapter = new NotificationFragmentAdapter(mList);
        mRecyclerView.setAdapter(alertsFragmentAdapter);
    }

    @Override
    public void onClick(View v) {
        // Todo
    }
}
