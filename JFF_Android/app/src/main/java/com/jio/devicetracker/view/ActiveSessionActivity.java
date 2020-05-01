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
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.ActiveSessionListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class shows all the active session(To whom you are tracking or you are tracked by)
 */
public class ActiveSessionActivity extends AppCompatActivity {
    private ActiveSessionListAdapter mAdapter;
    private DBManager mDbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        mDbManager = new DBManager(ActiveSessionActivity.this);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.ACTIVE_SESSION_TITLE);
        RecyclerView mRecyclerList = findViewById(R.id.activeSessionsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
        mAdapter = new ActiveSessionListAdapter(addDatainList());
        mRecyclerList.setAdapter(mAdapter);
        adapterEventListener();
    }

    private void adapterEventListener() {
        mAdapter.setOnItemClickPagerListener((image, groupName, groupId, createdBy, updatedBy) -> {
            if (image == R.drawable.ic_group_button) {
                Intent intent = new Intent(ActiveSessionActivity.this, ActiveMemberActivity.class);
                intent.putExtra(Constant.GROUPNAME, groupName);
                intent.putExtra(Constant.GROUP_ID, groupId);
                intent.putExtra(Constant.CREATED_BY, createdBy);
                intent.putExtra(Constant.UPDATED_BY, updatedBy);
                ActiveSessionActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Adds data in List
     */
    private List<HomeActivityListData> addDatainList() {
        List<HomeActivityListData> mList = mDbManager.getAllGroupDetail();
        List<HomeActivityListData> mListWithIcon = new ArrayList<>();
        for(HomeActivityListData data : mList) {
            HomeActivityListData homeActivityListData = new HomeActivityListData();
            homeActivityListData.setGroupName(data.getGroupName());
            homeActivityListData.setGroupId(data.getGroupId());
            homeActivityListData.setCreatedBy(data.getCreatedBy());
            homeActivityListData.setUpdatedBy(data.getUpdatedBy());
            homeActivityListData.setProfileImage(R.drawable.ic_group_button);
            mListWithIcon.add(homeActivityListData);
        }
        return mListWithIcon;
    }
}
