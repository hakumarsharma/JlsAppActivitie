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

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;

public class CreateGroupActivity extends BaseActivity implements View.OnClickListener {

    private EditText createGroupEditText;
    private Button addGroupCreateGroup;
    private DBManager mDbManager;
    private Button createGroup;
    public static String trackeeNumber;
    public static String trackeeName;
    private FrameLayout frameLayout;
    private ImageView userIconCreateGroup;
    public static String groupIdFromPeopleFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initUI();
        Intent intent = getIntent();
        trackeeName = intent.getStringExtra("TrackeeName");
        trackeeNumber = intent.getStringExtra("TrackeeNumber");
        changeButtonColorOnDataEntry();
    }

    /**
     * Initialize UI component
     */
    private void initUI() {
        Toolbar toolbar = findViewById(R.id.createGroupToolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);

        title.setText(Constant.CREATE_GROUP);
        title.setTypeface(Util.mTypeface(this, 5));

        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);

        this.selectedIcon = "ic_creategroup";
        createGroupEditText = findViewById(R.id.groupName);
        createGroupEditText.setTypeface(Util.mTypeface(this, 5));

        userIconCreateGroup = findViewById(R.id.userIconCreateGroup);
        userIconCreateGroup.setOnClickListener(this);

        createGroup = findViewById(R.id.createGroupInCreateGroupActivity);
        createGroup.setTypeface(Util.mTypeface(this, 5));
        createGroup.setOnClickListener(this);
        mDbManager = new DBManager(CreateGroupActivity.this);
        frameLayout = findViewById(R.id.chooseIcon_view);

        Button closeBtn = findViewById(R.id.closeFrameLayout);
        closeBtn.setOnClickListener(this);

        Button homeGroupBtn = findViewById(R.id.homeGroupButton);
        homeGroupBtn.setOnClickListener(this);

        Button familyGroupBtn = findViewById(R.id.familyGroupButton);
        familyGroupBtn.setOnClickListener(this);

        Button friendsGroupButton = findViewById(R.id.friendsGroupButton);
        friendsGroupButton.setOnClickListener(this);

        Button petGroupButton = findViewById(R.id.petGroupButton);
        petGroupButton.setOnClickListener(this);
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
        if (v.getId() == R.id.createGroupInCreateGroupActivity ) {
            String groupName = createGroupEditText.getText().toString();
            if ("".equalsIgnoreCase(groupName)) {
                createGroupEditText.setError(Constant.GROUP_NAME_VALIDATION_ERROR);
                return;
            } if(DashboardMainActivity.flowFromPeople){
                this.isFromCreateGroup = false;
                memberName = trackeeName;
                memberNumber = trackeeNumber;
                isGroupMember = false;
                createdGroupId = groupIdFromPeopleFlow;
                isFromDevice = false;
            } else {
                this.isFromCreateGroup = true;
            }

            createGroupAndAddContactAPICall(groupName);
        }else if (v.getId() == R.id.back){
            finish();
        }else  if (v.getId() == R.id.userIconCreateGroup){
             frameLayout.setVisibility(View.VISIBLE);
        }else if(v.getId() == R.id.closeFrameLayout){
            this.selectedIcon  = "ic_creategroup";
             userIconCreateGroup.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_creategroup));
             frameLayout.setVisibility(View.INVISIBLE);
        }else if(v.getId() == R.id.homeGroupButton){
            this.selectedIcon  = "home_group";
            userIconCreateGroup.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.home_group));
            frameLayout.setVisibility(View.INVISIBLE);
        }else if(v.getId() == R.id.familyGroupButton){
            this.selectedIcon  = "family_group";
            userIconCreateGroup.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.family_group));
            frameLayout.setVisibility(View.INVISIBLE);
        }else if(v.getId() == R.id.friendsGroupButton){
            this.selectedIcon  = "friends_group";
            userIconCreateGroup.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.friends_group));
            frameLayout.setVisibility(View.INVISIBLE);
        }else if(v.getId() == R.id.petGroupButton){
            this.selectedIcon  = "group_pet";
            userIconCreateGroup.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.group_pet));
            frameLayout.setVisibility(View.INVISIBLE);
        }
    }
}
