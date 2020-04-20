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
import com.jio.devicetracker.database.pojo.UpdateGroupNameData;
import com.jio.devicetracker.database.pojo.request.UpdateGroupNameRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/**
 * Implementation of Edit device detail to change the tracee's details.
 */
public class EditActivity extends Activity implements View.OnClickListener {

    private DBManager mDBmanager;
    private EditText mName;
    private EditText mNumber;
    private Button update;
    private String number;
    private String name;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mDBmanager = new DBManager(this);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.EDIT);
        mName = findViewById(R.id.memberName);
        mNumber = findViewById(R.id.deviceNumber);
        mNumber.setVisibility(View.INVISIBLE);
        update = findViewById(R.id.update);
        update.setOnClickListener(this);
        Intent intent = getIntent();
        name = intent.getStringExtra(Constant.NAME);
        number = intent.getStringExtra(Constant.NUMBER_CARRIER);
        groupId = intent.getStringExtra(Constant.GROUP_ID);
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
     */
    private void makeUpdateGroupNameAPICall() {
        UpdateGroupNameData updateGroupNameData = new UpdateGroupNameData();
        updateGroupNameData.setName(mName.getText().toString().trim());
        GroupRequestHandler.getInstance(EditActivity.this).handleRequest(new UpdateGroupNameRequest(new UpdateGroupNameSuccessListener(), new UpdateGroupNameErrorListener(), updateGroupNameData, groupId, mDBmanager.getAdminLoginDetail().getUserId()));
    }

    /**
     * Success Listener of Update Group API request
     */
    private class UpdateGroupNameSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            mDBmanager.updateGroupName(name, mName.getText().toString(), groupId);
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
            if(error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GROUP_UPDATION_FAILURE, Constant.ALERT_TITLE, EditActivity.this);
            } if(error.networkResponse.statusCode == 404) {
                Util.alertDilogBox(Constant.NO_GROUP_FOUND, Constant.ALERT_TITLE, EditActivity.this);
            }
        }
    }
}
