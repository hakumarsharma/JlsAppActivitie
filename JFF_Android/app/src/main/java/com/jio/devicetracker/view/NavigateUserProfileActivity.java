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
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class NavigateUserProfileActivity extends Activity implements View.OnClickListener {

    private TextView userName;
    private TextView userNumber;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent intent = getIntent();
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.PROFILE_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        userName = findViewById(R.id.userName);
        userNumber = findViewById(R.id.userNumber);
        userName.setText(intent.getStringExtra("Name"));
        userNumber.setText(intent.getStringExtra("Number"));
        Button editBtn = findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.edit_btn:
                gotoUpdateProfileActivity();
                break;
        }
    }

    private void gotoUpdateProfileActivity() {
        Intent intent = new Intent(this,EditUserProfileActivity.class);
        intent.putExtra("Name",userName.getText().toString());
        intent.putExtra("Number",userNumber.getText().toString());
        startActivity(intent);
    }
}
