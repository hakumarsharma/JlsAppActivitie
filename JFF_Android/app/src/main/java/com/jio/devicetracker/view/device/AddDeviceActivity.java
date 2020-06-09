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

package com.jio.devicetracker.view.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.people.AddPeopleActivity;

public class AddDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar = null;
    private static String groupId;
    private static String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddevice);
        toolbar = findViewById(R.id.adddDeviceToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.Add_Device);
        toolbar.setBackgroundColor(getResources().getColor(R.color.cardviewlayout_device_background_color));

        Intent intent = getIntent();
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        groupName = intent.getStringExtra(Constant.GROUP_NAME);

        Button addDevice = findViewById(R.id.add_device);
        addDevice.setOnClickListener(this);

        Button addPerson = findViewById(R.id.add_contact);
        addPerson.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_device) {
            Intent intent = new Intent(this, QRReaderInstruction.class);
            intent.putExtra(Constant.GROUP_ID, groupId);
            intent.putExtra(Constant.GROUP_NAME, groupName);
            startActivity(intent);
        } else if (v.getId() == R.id.add_contact){
            Intent intent = new Intent(this, AddPeopleActivity.class);
            intent.putExtra(Constant.GROUP_ID, groupId);
            startActivity(intent);
        }
    }
}

