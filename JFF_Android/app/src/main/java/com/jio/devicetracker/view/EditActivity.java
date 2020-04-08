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
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.util.Constant;

/**
 * Implementation of Edit device detail to change the tracee's details.
 */
public class EditActivity extends Activity implements View.OnClickListener {

    private DBManager mDBmanager;
    private EditText mName;
    private EditText mNumber;
    private String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mDBmanager = new DBManager(this);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.EDIT);
        mName = findViewById(R.id.memberName);
        mNumber = findViewById(R.id.deviceNumber);
        Button update = findViewById(R.id.update);
        update.setOnClickListener(this);
        Intent intent = getIntent();
        String name = intent.getStringExtra(Constant.NAME);
        number = intent.getStringExtra(Constant.NUMBER_CARRIER);
        mName.setText(name);
        mNumber.setText(number);
    }

    @Override
    public void onClick(View v) {
        gotoDashboard();
    }

    // Navigates to the Dashboard Activity with the entered data, and them update in database
    private void gotoDashboard() {
        mDBmanager.updateProfile(number, mName.getText().toString(), mNumber.getText().toString());
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}
