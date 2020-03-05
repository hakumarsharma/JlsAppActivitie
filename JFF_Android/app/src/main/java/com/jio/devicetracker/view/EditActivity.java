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

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;

/**
 * Implementation of Edit Profile Screen to change the tracee's details.
 */
public class EditActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.EDIT);
        EditText mName = findViewById(R.id.memberName);
        EditText relationEditText = findViewById(R.id.relationWithUser);
        Button update = findViewById(R.id.update);
        update.setOnClickListener(this);
        Intent intent = getIntent();
        String name = intent.getStringExtra(Constant.NAME);
        String relation = intent.getStringExtra(Constant.NUMBER_CARRIER);
        mName.setText(name);
        relationEditText.setText(relation);
    }

    @Override
    public void onClick(View v) {
        gotoDashboard();
    }

    private void gotoDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}
