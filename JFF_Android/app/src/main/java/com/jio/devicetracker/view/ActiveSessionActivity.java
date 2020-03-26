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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.ActiveSessionData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.ActiveSessionListAdapter;
import com.jio.devicetracker.view.adapter.TrackerDeviceListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActiveSessionActivity extends AppCompatActivity {
    private List<ActiveSessionData> mList;
    private ActiveSessionListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        mList = new ArrayList<>();
        addDataInList();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.ACTIVE_SESSION_TITLE);
        RecyclerView mRecyclerList = findViewById(R.id.activeSessionsList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
        mAdapter = new ActiveSessionListAdapter(mList);
        mRecyclerList.setAdapter(mAdapter);
        adapterEventListener();
    }

    private void adapterEventListener() {
        mAdapter.setOnItemClickPagerListener((image, groupName) -> {
            if(image == R.drawable.ic_group_button) {
                Intent intent = new Intent(this, TrackerListActivity.class);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        });
    }

    private void addDataInList() {
        for (int i = 0; i < 6; i++) {
            if (i < 2) {
                ActiveSessionData data = new ActiveSessionData();
                data.setName("Test");
                data.setNumber("1234567890");
                data.setDurationTime("15 min");
                data.setExpiryTime("05 min");
                data.setProfileImage(R.drawable.ic_group_button);
                mList.add(data);
            }
            if (i >= 2 && i < 4) {
                ActiveSessionData data = new ActiveSessionData();
                data.setName("Test");
                data.setNumber("1234567890");
                data.setDurationTime("15 min");
                data.setExpiryTime("05 min");
                data.setProfileImage(R.drawable.ic_tracee_list);
                mList.add(data);
            }
            if (i >= 4 && i < 6) {
                ActiveSessionData data = new ActiveSessionData();
                data.setName("Test");
                data.setNumber("1234567890");
                data.setDurationTime("15 min");
                data.setExpiryTime("05 min");
                data.setProfileImage(R.drawable.ic_pet);
                mList.add(data);
            }
        }
    }
}
