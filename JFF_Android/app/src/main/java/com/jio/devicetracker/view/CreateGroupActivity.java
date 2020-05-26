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
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.CreateGroupData;
import com.jio.devicetracker.database.pojo.request.CreateGroupRequest;
import com.jio.devicetracker.database.pojo.response.CreateGroupResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText createGroupEditText;
    private Button addGroupCreateGroup;
    private DBManager mDbManager;
    private Button createGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initUI();
        changeButtonColorOnDataEntry();
    }

    /**
     * Initialize UI component
     */
    private void initUI() {
        TextView chooseGroupTextView = findViewById(R.id.createGroupTextView);
        chooseGroupTextView.setTypeface(Util.mTypeface(this, 5));
        createGroupEditText = findViewById(R.id.createGroupEditText);
        createGroupEditText.setTypeface(Util.mTypeface(this, 5));
        addGroupCreateGroup = findViewById(R.id.addGroupCreateGroup);
        addGroupCreateGroup.setOnClickListener(this);
        createGroup = findViewById(R.id.createGroup);
        createGroup.setTypeface(Util.mTypeface(this, 5));
        createGroup.setOnClickListener(this);
        mDbManager = new DBManager(CreateGroupActivity.this);
    }

    /**
     * Change the button color when user enter trackee name
     */
    private void changeButtonColorOnDataEntry() {
        createGroupEditText.addTextChangedListener(new TextWatcher() {
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

    /**
     * To do event handling
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createGroup || v.getId() == R.id.addGroupCreateGroup) {
            String groupName = createGroupEditText.getText().toString();
            if ("".equalsIgnoreCase(groupName)) {
                createGroupEditText.setError(Constant.GROUP_NAME_VALIDATION_ERROR);
                return;
            }
            makeCreateGroupAPICall(groupName);
        }
    }

    /**
     * Create Group API call
     */
    private void makeCreateGroupAPICall(String groupName) {
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
            DBManager mDbManager = new DBManager(CreateGroupActivity.this);
            mDbManager.insertIntoGroupTable(createGroupResponse);
            Util.progressDialog.dismiss();
            startActivity(new Intent(CreateGroupActivity.this, ChooseGroupActivity.class));
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
                Util.alertDilogBox(Constant.GROUP_CREATION_FAILURE, Constant.ALERT_TITLE, CreateGroupActivity.this);
            }
        }
    }

}
