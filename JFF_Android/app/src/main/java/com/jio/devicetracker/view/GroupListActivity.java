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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupData;
import com.jio.devicetracker.database.pojo.GroupmemberListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.GroupMemberListAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        Toolbar toolbar = findViewById(R.id.customToolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        Button createGroupButtonOnToolbar = toolbar.findViewById(R.id.createGroupButtonOnToolbar);
        createGroupButtonOnToolbar.setText(Constant.CREATE_GROUP_LIST);
        createGroupButtonOnToolbar.setVisibility(View.VISIBLE);
        createGroupButtonOnToolbar.setOnClickListener(this);
        toolbarTitle.setText(Constant.GROUP_TITLE);
        mRecyclerList = findViewById(R.id.groupList);
        FloatingActionButton groupMembersListFloatButton = findViewById(R.id.groupMembersListFloatButton);
        groupMembersListFloatButton.setOnClickListener(this);
        addDataInList();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
    }

    private void addDataInList() {
        List<GroupmemberListData> mList = new ArrayList<>();
        if (DashboardActivity.specificGroupMemberData.size() > 0) {
            for (GroupData groupData : DashboardActivity.specificGroupMemberData) {
                if (groupData.getGroupName().equalsIgnoreCase(DashboardActivity.groupName)) {
                    GroupmemberListData data = new GroupmemberListData();
                    data.setName(groupData.getName());
                    data.setNumber(groupData.getNumber());
                    data.setProfileImage(R.drawable.ic_tracee_list);
                    mList.add(data);
                }
            }
        }
        GroupMemberListAdapter mAdapter = new GroupMemberListAdapter(mList);
        mRecyclerList.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.groupMembersListFloatButton) {
            DashboardActivity.isComingFromGroupList = true;
            startActivity(new Intent(this, ContactDetailsActivity.class));
        } else if(v.getId() == R.id.createGroupButtonOnToolbar){
            Util.alertDilogBox("Coming Soon...","Alert",this);
//            Toast.makeText(this, "We will make an API to create group on borqs", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DashboardActivity.class));
    }
}
