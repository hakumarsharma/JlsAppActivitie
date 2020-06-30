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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.ActiveMemberListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActiveMemberActivity extends AppCompatActivity implements View.OnClickListener{

    private String groupId;
    private DBManager mDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_member_list);
        mDbManager = new DBManager(this);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        Intent intent = getIntent();
        toolbarTitle.setText(Constant.EDIT_MEMBER_TITLE);
        TextView groupNameTitle = findViewById(R.id.groupNameTitle);
        groupNameTitle.setText(intent.getStringExtra(Constant.GROUP_NAME));
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        RecyclerView mRecyclerList = findViewById(R.id.trackerList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
        ActiveMemberListAdapter mAdapter = new ActiveMemberListAdapter(addDataInList(), this);
        mRecyclerList.setAdapter(mAdapter);
        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(this);
        backButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                // Todo
                break;
        }
    }

    /**
     * Displays group member data in list
     *
     * @return List<GroupMemberDataList>
     */
    private List<GroupMemberDataList> addDataInList() {
        List<GroupMemberDataList> mList = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
        List<GroupMemberDataList> memberList = new ArrayList<>();
        for (GroupMemberDataList data : mList) {
            if (!data.getConsentStatus().equalsIgnoreCase(Constant.EXITED) && !data.getConsentStatus().equalsIgnoreCase(Constant.REMOVED)) {
                GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
                groupMemberDataList.setName(data.getName());
                groupMemberDataList.setNumber(data.getNumber());
                groupMemberDataList.setConsentStatus(data.getConsentStatus());
                groupMemberDataList.setGroupId(data.getGroupId());
                groupMemberDataList.setGroupAdmin(data.isGroupAdmin());
                groupMemberDataList.setProfileImage(R.drawable.ic_tracee_list);
                groupMemberDataList.setFrom(mDbManager.getGroupDetail(data.getGroupId()).getFrom());
                groupMemberDataList.setTo(mDbManager.getGroupDetail(data.getGroupId()).getTo());
                memberList.add(groupMemberDataList);
            }
        }
        return memberList;
    }
}
