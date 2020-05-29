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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class CreateGroupActivity extends BaseActivity implements View.OnClickListener {

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
        Toolbar toolbar = findViewById(R.id.createGroupToolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);

        title.setText(Constant.Choose_Group);
        title.setTypeface(Util.mTypeface(this, 5));

        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);

        createGroupEditText = findViewById(R.id.groupName);
        createGroupEditText.setTypeface(Util.mTypeface(this, 5));


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
        if (v.getId() == R.id.createGroup ) {
            String groupName = createGroupEditText.getText().toString();
            if ("".equalsIgnoreCase(groupName)) {
                createGroupEditText.setError(Constant.GROUP_NAME_VALIDATION_ERROR);
                return;
            }
            this.isFromCreateGroup = true;
            createGroupAndAddContactAPICall(groupName);
        }else if (v.getId() == R.id.back){
            finish();
        }
    }
}
