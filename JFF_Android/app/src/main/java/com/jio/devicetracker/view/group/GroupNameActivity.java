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
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.CreateGroupData;
import com.jio.devicetracker.database.pojo.request.CreateGroupRequest;
import com.jio.devicetracker.database.pojo.response.CreateGroupResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.dashboard.DashboardActivity;

/**
 * Class to create Group name and the relation with group members
 */
public class GroupNameActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText groupNameEditText = null;
    private Button createGroup = null;
    private DBManager mDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name);
        mDbManager = new DBManager(this);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.GROUP_NAME);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        createGroup = findViewById(R.id.createGroupName);
        createGroup.setOnClickListener(this);
        changeButtonColorOnDataEntry();
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage){
        CustomAlertActivity alertActivity = new CustomAlertActivity(this);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    /**
     * Change the button color when you enter data in edittext
     */
    private void changeButtonColorOnDataEntry() {
        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createGroup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                createGroup.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                createGroup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                createGroup.setTextColor(Color.WHITE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createGroupName) {
            String groupName = groupNameEditText.getText().toString();
            if ("".equalsIgnoreCase(groupName)) {
                groupNameEditText.setError(Constant.GROUP_NAME_VALIDATION_ERROR);
                return;
            }
            createGroupAPICall(groupName);
        }
    }

    /**
     * Create Group API call
     */
    private void createGroupAPICall(String groupName) {
        CreateGroupData createGroupData = new CreateGroupData();
        createGroupData.setName(groupName);
        createGroupData.setType("one_to_one");
        CreateGroupData.Session session = new CreateGroupData().new Session();
        session.setFrom(Util.getInstance().getTimeEpochFormatAfterCertainTime(1));
        session.setTo(Util.getInstance().getTimeEpochFormatAfterCertainTime(60));
        createGroupData.setSession(session);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(getApplicationContext()).handleRequest(new CreateGroupRequest(new CreateGroupSuccessListener(), new CreateGroupErrorListener(), createGroupData, mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * Create Group Success Listener
     */
    private class CreateGroupSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            CreateGroupResponse createGroupResponse = Util.getInstance().getPojoObject(String.valueOf(response), CreateGroupResponse.class);
            mDbManager.insertIntoGroupTable(createGroupResponse);
            Util.progressDialog.dismiss();
            startActivity(new Intent(GroupNameActivity.this, DashboardActivity.class));
        }
    }

    /**
     * Create Group error Listener
     */
    private class CreateGroupErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            if(error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                showCustomAlertWithText(Constant.GROUP_CREATION_FAILURE);
            }
        }
    }
}
