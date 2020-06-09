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

package com.jio.devicetracker.view.group;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.request.GetGroupMemberRequest;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.people.ContactDetailsActivity;
import com.jio.devicetracker.view.dashboard.DashboardActivity;
import com.jio.devicetracker.view.adapter.GroupMemberListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the group member of a particular group
 */
public class GroupListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerList;
    private DBManager mDbManager;
    private String groupId;
    private String userId;
    private String groupName;
    private List<GroupMemberDataList> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        Toolbar toolbar = findViewById(R.id.customToolbar);
        mDbManager = new DBManager(this);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        Button createGroupButtonOnToolbar = toolbar.findViewById(R.id.createGroupButtonOnToolbar);
        createGroupButtonOnToolbar.setText(Constant.CREATE_GROUP_LIST);
        createGroupButtonOnToolbar.setVisibility(View.VISIBLE);
        createGroupButtonOnToolbar.setOnClickListener(this);
        mRecyclerList = findViewById(R.id.groupList);
        Intent intent = getIntent();
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        userId = intent.getStringExtra(Constant.USER_ID);
        groupName = intent.getStringExtra(Constant.GROUPNAME);
        toolbarTitle.setText(groupName);
        FloatingActionButton groupMembersListFloatButton = findViewById(R.id.groupMembersListFloatButton);
        groupMembersListFloatButton.setOnClickListener(this);
        makeGetGroupMemberAPICall();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
    }

    /**
     * Displays group members in a list
     */
    private void makeGetGroupMemberAPICall() {
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupMemberRequest(new GetGroupMemberRequestSuccessListener(), new GetGroupMemberRequestErrorListener(), groupId, userId));
    }

    /**
     * Get all members of a particular group success listener
     */
    public class GetGroupMemberRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                mDbManager.insertGroupMemberDataInTable(groupMemberResponse);
            }
            showDataInList();
            isMemberPresent();
        }
    }

    private void isMemberPresent() {
        TextView instruction = findViewById(R.id.groupListMember);
        if (mList.isEmpty()) {
            mRecyclerList.setVisibility(View.INVISIBLE);
            instruction.setVisibility(View.VISIBLE);
            instruction.setText(Constant.ADD_GROUP_MEMBER_INSTRUCTION1 + groupName + Constant.ADD_GROUP_MEMBER_INSTRUCTION2);
        } else {
            mRecyclerList.setVisibility(View.VISIBLE);
            instruction.setVisibility(View.GONE);
        }
    }

    /**
     * Displays group members of group
     */
    private void showDataInList() {
        mList = new ArrayList<>();
        List<GroupMemberDataList> listData = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
        for (GroupMemberDataList groupMemberDataList : listData) {
            if (groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.PENDING) || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.APPROVED)
            || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.EXITED)) {
                GroupMemberDataList data = new GroupMemberDataList();
                data.setName(groupMemberDataList.getName());
                data.setNumber(groupMemberDataList.getNumber());
                data.setProfileImage(R.drawable.ic_tracee_list);
                data.setConsentStatus(groupMemberDataList.getConsentStatus());
                mList.add(data);
            }
        }
        GroupMemberListAdapter mAdapter = new GroupMemberListAdapter(mList);
        mRecyclerList.setAdapter(mAdapter);
    }

    /**
     * Get all members of a particular group error listener
     */
    private class GetGroupMemberRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.groupMembersListFloatButton) {
            gotoContactDetailsActivity();
        } else if (v.getId() == R.id.createGroupButtonOnToolbar) {
            Util.alertDilogBox("Coming Soon...", "Alert", this);
        }
    }

    /**
     * Goto Contact Details Activity
     */
    private void gotoContactDetailsActivity() {
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.USER_ID, userId);
        intent.putExtra(Constant.IS_COMING_FROM_GROUP_LIST, true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DashboardActivity.class));
    }
}
