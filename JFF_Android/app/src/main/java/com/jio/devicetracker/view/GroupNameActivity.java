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
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.ListOnHomeScreen;
import com.jio.devicetracker.util.Constant;

public class GroupNameActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText groupNameEditText = null;
    private EditText relationWithGroupMembers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.GROUP_NAME);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        relationWithGroupMembers = findViewById(R.id.relationWithGroupMembers);
        Button createGroup = findViewById(R.id.createGroupName);
        createGroup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.createGroupName){
            String groupName = groupNameEditText.getText().toString();
            String relationWithGroupMembersText = relationWithGroupMembers.getText().toString();
            boolean isGroupMember;
            if(DashboardActivity.isComingFromGroupList){
                isGroupMember = true;
            } else  {
                isGroupMember = false;
            }
            setHomeScreenData(groupName, relationWithGroupMembersText, isGroupMember);
        }
    }

    private void setHomeScreenData(String groupName, String relationWithGroupMembersText, boolean isGroupMember) {
        DashboardActivity.listOnHomeScreens.clear();
        HomeActivityListData listOnHomeScreen = new HomeActivityListData();
        listOnHomeScreen.setName(groupName);
        listOnHomeScreen.setPhoneNumber(relationWithGroupMembersText);
        listOnHomeScreen.setGroupMember(isGroupMember);
        DashboardActivity.listOnHomeScreens.add(listOnHomeScreen);
        startActivity(new Intent(this, DashboardActivity.class));
    }
}
