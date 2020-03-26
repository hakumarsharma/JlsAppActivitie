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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.TrackerListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.TrackerListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TrackerListActivity extends AppCompatActivity {

    private List<TrackerListData> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_tracker_list);
        mList = new ArrayList<>();
        addDataInList();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        Intent intent = getIntent();
        toolbarTitle.setText(intent.getStringExtra("groupName"));
        RecyclerView mRecyclerList = findViewById(R.id.trackerList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
        TrackerListAdapter mAdapter = new TrackerListAdapter(mList);
        mRecyclerList.setAdapter(mAdapter);
    }

    private void addDataInList() {
        for (int i =0; i < 5 ; i++){
            TrackerListData data = new TrackerListData();
            data.setName("Test");
            data.setNumber("1234567890");
            data.setDurationTime("15 min");
            data.setExpiryTime("05 min");
            data.setProfileImage(R.drawable.ic_tracee_list);
            mList.add(data);
        }
    }

}
