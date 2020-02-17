// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
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
import com.jio.devicetracker.database.pojo.EditProfileData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/**
 * Implementation of Edit Profile Screen to change the tracee's details.
 */
public class EditActivity extends Activity implements View.OnClickListener {

    private EditText mName;
    private EditText mNumber;
    private EditText mIMEI;
    private String number;
    private DBManager mDBmanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.EDIT);
        mName = findViewById(R.id.memberName);
        mNumber = findViewById(R.id.deviceName);
        mIMEI = findViewById(R.id.deviceIMEINumber);
        Button update = findViewById(R.id.update);
        mDBmanager = new DBManager(this);
        update.setOnClickListener(this);
        Intent intent = getIntent();
        number = intent.getStringExtra(Constant.NUMBER_CARRIER);
        EditProfileData editData;
        editData = mDBmanager.getUserdataForEdit(number);
        mName.setText(editData.getName());
        mNumber.setText(editData.getPhoneNumber());
        mIMEI.setText(editData.getImeiNumber());


    }

    @Override
    public void onClick(View v) {
        mDBmanager.updateProfile(number, mName.getText().toString(), mNumber.getText().toString(), mIMEI.getText().toString());
        gotoDashboard();
    }

    private void gotoDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}
