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
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.request.ExitRemovedGroupRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.ActiveMemberListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActiveMemberActivity extends AppCompatActivity {

    private String groupId;
    private DBManager mDbManager;
    private ActiveMemberListAdapter mAdapter;
    private String consentId;
    private int position;
    private String errorMessage;
    private String createdBy;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_member_list);
        mDbManager = new DBManager(this);
        userId = mDbManager.getAdminLoginDetail().getUserId();
        addDataInList();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        Intent intent = getIntent();
        toolbarTitle.setText(intent.getStringExtra(Constant.GROUPNAME));
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        createdBy = intent.getStringExtra(Constant.CREATED_BY);
        RecyclerView mRecyclerList = findViewById(R.id.trackerList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
        mAdapter = new ActiveMemberListAdapter(addDataInList());
        mRecyclerList.setAdapter(mAdapter);
        adapterEventListener();
    }

    /**
     * Adapter Listener
     */
    private void adapterEventListener() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickPagerListener((v, position, groupId, isGroupAdmin, phoneNumber, consentId) -> {
                PopupMenu popup = new PopupMenu(ActiveMemberActivity.this, v);
                this.consentId = consentId;
                this.position = position;
                if (isGroupAdmin == true && createdBy.equalsIgnoreCase(userId)) {
                    popup.getMenu().add(Menu.NONE, 1, 1, Constant.REMOVE);
                    errorMessage = Constant.REMOVE_FROM_GROUP_FAILURE;
                } else {
                    popup.getMenu().add(Menu.NONE, 2, 2, Constant.EXIT);
                    errorMessage = Constant.EXIT_FROM_GROUP_FAILURE;
                }
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case 1:
                            makeRemoveAPICall(phoneNumber);
                            break;
                        case 2:
                            makeExitAPICall(phoneNumber);
                            break;
                        default:
                            break;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }

    /**
     * make an Exit API Call
     */
    private void makeExitAPICall(String phoneNumber) {
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(phoneNumber);
        consent.setStatus(Constant.EXITED);
        exitRemovedGroupData.setConsent(consent);
        Util.getInstance().showProgressBarDialog(ActiveMemberActivity.this);
        GroupRequestHandler.getInstance(this).handleRequest(new ExitRemovedGroupRequest(new ExitRemovedGroupRequestSuccessListener(), new ExitRemovedGroupRequestErrorListener(), exitRemovedGroupData, groupId, userId));
    }

    /**
     * Make a Remove API Call
     * @param phoneNumber
     */
    private void makeRemoveAPICall(String phoneNumber) {
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(phoneNumber);
        consent.setStatus(Constant.REMOVED);
        exitRemovedGroupData.setConsent(consent);
        Util.getInstance().showProgressBarDialog(ActiveMemberActivity.this);
        GroupRequestHandler.getInstance(this).handleRequest(new ExitRemovedGroupRequest(new ExitRemovedGroupRequestSuccessListener(), new ExitRemovedGroupRequestErrorListener(), exitRemovedGroupData, groupId, userId));
    }

    /**
     * Exit API Call Success Listener
     */
    private class ExitRemovedGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            mDbManager.deleteSelectedDataFromGroup(consentId);
            mAdapter.removeItem(position);
            addDataInList();
        }
    }

    /**
     * Exit API Call Error Listener
     */
    private class ExitRemovedGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(errorMessage, Constant.ALERT_TITLE, ActiveMemberActivity.this);
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
            GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
            groupMemberDataList.setName(data.getName());
            groupMemberDataList.setNumber(data.getNumber());
            groupMemberDataList.setConsentStatus(data.getConsentStatus());
            groupMemberDataList.setGroupId(data.getGroupId());
            groupMemberDataList.setGroupAdmin(data.isGroupAdmin());
            groupMemberDataList.setProfileImage(R.drawable.ic_tracee_list);
            memberList.add(groupMemberDataList);
        }
        return memberList;
    }
}
