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
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddMemberInGroupData;
import com.jio.devicetracker.database.pojo.CreateGroupData;
import com.jio.devicetracker.database.pojo.request.AddMemberInGroupRequest;
import com.jio.devicetracker.database.pojo.request.CreateGroupRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupMemberRequest;
import com.jio.devicetracker.database.pojo.response.CreateGroupResponse;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    public String memberName;
    public String memberNumber;
    private DBManager mDbManager;
    public String createdGroupId;
    public Boolean isFromCreateGroup;
    public Boolean isGroupMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbManager = new DBManager(this);
    }

    /**
     * Adds individual contact in Dashboard, but it adds as a member of group.
     * Group Name is hardcoded as a Individual_User
     */
    public void createGroupAndAddContactAPICall(String groupName) {
        CreateGroupData createGroupData = new CreateGroupData();
        createGroupData.setName(groupName);
        createGroupData.setType(Constant.ONE_TO_ONE);
        CreateGroupData.Session session = new CreateGroupData().new Session();
        session.setFrom(Util.getInstance().getTimeEpochFormatAfterCertainTime(1));
        session.setTo(Util.getInstance().getTimeEpochFormatAfterCertainTime(60));
        createGroupData.setSession(session);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new CreateGroupRequest(new CreateGroupSuccessListener(), new CreateGroupErrorListener(), createGroupData, Util.userId));
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
                createdGroupId = createGroupResponse.getData().getId();
                if (isFromCreateGroup) {
                    Util.progressDialog.dismiss();
                    startActivity(new Intent(BaseActivity.this, AddDeviceActivity.class));
                } else {
                    addIndividualUserInGroupAPICall();
                }
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
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.alertDilogBox(Constant.ADDING_INDIVIDUAL_USER_FAILED, Constant.ALERT_TITLE, BaseActivity.this);
            }
        }
    }


    /**
     * Adds individual member inside Group Called Individual_User
     */
    public void addIndividualUserInGroupAPICall() {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.EVENTS);
        consents.setEntities(mList);
        consents.setPhone(memberNumber);
        consents.setName(memberName);
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, createdGroupId, Util.userId));
    }


    /**
     * Add Members in Group API Call, member will be part of group
     */
    public void addMemberInGroupAPICall() {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.EVENTS);
        consents.setEntities(mList);
        consents.setPhone(memberNumber);
        consents.setName(memberName);
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, createdGroupId, Util.userId));
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
                if(isGroupMember) {
                    getAllForOneGroupAPICall();
                }else {
                    Util.progressDialog.dismiss();
                    gotoDashboardActivity();
                }
            }
        }
    }

    /**
     * Add Member in Group Error Listener
     */
    private class AddMemberInGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.GROUP_MEMBER_ADDITION_FAILURE, Constant.ALERT_TITLE, BaseActivity.this);
            } else if (error.networkResponse.statusCode == Constant.STATUS_CODE_404) {
                // Make Verify and Assign call
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.DEVICE_NOT_FOUND, Constant.ALERT_TITLE, BaseActivity.this);
            } else {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.GROUP_MEMBER_ADDITION_FAILURE, Constant.ALERT_TITLE, BaseActivity.this);
            }
        }
    }

    /**
     * Get all members of a particular group and update the database
     */
    public void getAllForOneGroupAPICall() {
        if(Util.progressDialog == null) {
            Util.getInstance().showProgressBarDialog(this);
        }
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupMemberRequest(new GetGroupMemberRequestSuccessListener(), new GetGroupMemberRequestErrorListener(), createdGroupId, Util.userId));
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
                BaseActivity baseActivity = new AddPeopleActivity();
                ((AddPeopleActivity)baseActivity).getAllMembers(groupMemberResponse.getData());
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
        }
    }

    private void gotoGroupListActivity() {
        Intent intent = new Intent(this, GroupListActivity.class);
        intent.putExtra(Constant.GROUP_ID, createdGroupId);
        intent.putExtra(Constant.USER_ID, Util.userId);
        startActivity(intent);
    }

    private void gotoDashboardActivity() {
        startActivity(new Intent(this, ChooseGroupActivity.class));
    }

}
