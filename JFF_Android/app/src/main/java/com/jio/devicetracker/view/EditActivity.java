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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddMemberInGroupData;
import com.jio.devicetracker.database.pojo.CreateGroupData;
import com.jio.devicetracker.database.pojo.UpdateGroupNameData;
import com.jio.devicetracker.database.pojo.request.AddMemberInGroupRequest;
import com.jio.devicetracker.database.pojo.request.CreateGroupRequest;
import com.jio.devicetracker.database.pojo.request.DeleteGroupRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupMemberRequest;
import com.jio.devicetracker.database.pojo.request.UpdateGroupNameRequest;
import com.jio.devicetracker.database.pojo.response.CreateGroupResponse;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Edit device detail to change the tracee's details.
 */
public class EditActivity extends Activity implements View.OnClickListener {

    private DBManager mDbManager;
    private EditText mName;
    private String name;
    private String groupId;
    private String isGroupMember;
    private String userId;
    private EditText mNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mDbManager = new DBManager(this);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.EDIT);
        mName = findViewById(R.id.memberName);
        mNumber = findViewById(R.id.deviceNumber);
        mNumber.setVisibility(View.INVISIBLE);
        Button update = findViewById(R.id.update);
        update.setOnClickListener(this);
        Intent intent = getIntent();
        name = intent.getStringExtra(Constant.NAME);
        String number = intent.getStringExtra(Constant.NUMBER_CARRIER);
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        isGroupMember = intent.getStringExtra(Constant.IS_GROUP_MEMBER);
        userId = intent.getStringExtra(Constant.USER_ID);
        mName.setText(name);
        mNumber.setText(number);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.update) {
            makeUpdateGroupNameAPICall();
        }
    }

    /**
     * Makes an update Group Name API call and updates the database
     * If the selected member is individual user then delete the entire group update the database, and create a new group along with the new user name
     */
    private void makeUpdateGroupNameAPICall() {
        if (isGroupMember.equalsIgnoreCase(Constant.GROUP)) {
            UpdateGroupNameData updateGroupNameData = new UpdateGroupNameData();
            updateGroupNameData.setName(mName.getText().toString().trim());
            GroupRequestHandler.getInstance(this).handleRequest(new UpdateGroupNameRequest(new UpdateGroupNameSuccessListener(), new UpdateGroupNameErrorListener(), updateGroupNameData, groupId, userId));
        } else {
            makeDeleteGroupAPICall(groupId);
        }
    }

    /**
     * Delete the Group and update the database
     */
    private void makeDeleteGroupAPICall(String groupId) {
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new DeleteGroupRequest(new DeleteGroupRequestSuccessListener(), new DeleteGroupRequestErrorListener(), groupId, userId));
    }

    /**
     * Delete Group Request API Call Success Listener
     */
    private class DeleteGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            mDbManager.deleteSelectedDataFromGroup(groupId);
            mDbManager.deleteSelectedDataFromGroupMember(groupId);
            createGroupAndAddContactAPICall();
        }
    }

    /**
     * Delete Group Request API Call Error Listener
     */
    private class DeleteGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.INDIVIDUAL_USER_EDIT_FAILURE, Constant.ALERT_TITLE, EditActivity.this);
        }
    }

    /**
     * Adds individual contact in Dashboard, but it adds as a member of group.
     * Group Name is hardcoded as a Individual_User
     */
    private void createGroupAndAddContactAPICall() {
        CreateGroupData createGroupData = new CreateGroupData();
        createGroupData.setName(Constant.INDIVIDUAL_USER_GROUP_NAME);
        createGroupData.setType(Constant.ONE_TO_ONE);
        CreateGroupData.Session session = new CreateGroupData().new Session();
        session.setFrom(Util.getInstance().getTimeEpochFormatAfterCertainTime(1));
        session.setTo(Util.getInstance().getTimeEpochFormatAfterCertainTime(15));
        createGroupData.setSession(session);
        GroupRequestHandler.getInstance(getApplicationContext()).handleRequest(new CreateGroupRequest(new CreateGroupSuccessListener(), new CreateGroupErrorListener(), createGroupData, userId));
    }

    /**
     * Create Group Success Listener
     */
    private class CreateGroupSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            CreateGroupResponse createGroupResponse = Util.getInstance().getPojoObject(String.valueOf(response), CreateGroupResponse.class);
            if (createGroupResponse.getCode() == 200) {
                mDbManager.insertIntoGroupTable(createGroupResponse);
                groupId = createGroupResponse.getData().getId();
                addIndividualUserInGroupAPICall();
            }
        }
    }

    /**
     * Create Group error Listener
     */
    private class CreateGroupErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.INDIVIDUAL_USER_EDIT_FAILURE, Constant.ALERT_TITLE, EditActivity.this);
        }
    }

    /**
     * Adds individual member inside Group Called Individual_User
     */
    private void addIndividualUserInGroupAPICall() {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.EVENTS);
        consents.setEntities(mList);
        consents.setPhone(mNumber.getText().toString().trim());
        consents.setName(mName.getText().toString().trim());
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, groupId, userId));
    }

    /**
     * Add Member in group Success Listener
     */
    private class AddMemberInGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                mDbManager.insertGroupMemberDataInTable(groupMemberResponse);
                getAllForOneGroupAPICall();
            }
        }
    }

    /**
     * Add Member in Group Error Listener
     */
    private class AddMemberInGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.INDIVIDUAL_USER_EDIT_FAILURE, Constant.ALERT_TITLE, EditActivity.this);
        }
    }


    /**
     * Get all members of a particular group and update the database
     */
    public void getAllForOneGroupAPICall() {
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
                startActivity(new Intent(EditActivity.this, DashboardActivity.class));
            }
        }
    }

    /**
     * Get all members of a particular group error listener
     */
    private class GetGroupMemberRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            startActivity(new Intent(EditActivity.this, DashboardActivity.class));
        }
    }

    /**
     * Success Listener of Update Group API request
     */
    private class UpdateGroupNameSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            mDbManager.updateGroupName(name, mName.getText().toString(), groupId);
            Intent intent = new Intent(EditActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Error Listener of Update Group API request
     */
    private class UpdateGroupNameErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GROUP_UPDATION_FAILURE, Constant.ALERT_TITLE, EditActivity.this);
            }
            if (error.networkResponse.statusCode == 404) {
                Util.alertDilogBox(Constant.NO_GROUP_FOUND, Constant.ALERT_TITLE, EditActivity.this);
            }
        }
    }
}
