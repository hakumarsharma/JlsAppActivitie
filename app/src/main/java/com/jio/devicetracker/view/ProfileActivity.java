// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AdminLoginData;

/**
 * Implementation of Admin's profile Screen to show the admin's details.
 */
public class ProfileActivity extends AppCompatActivity {

    private DBManager mDbManager;
    private TextView userName;
    private TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDbManager = new DBManager(this);
        userName = findViewById(R.id.name);
        userEmail = findViewById(R.id.user_Email);
        getUserAdminDetail();
    }

    private void getUserAdminDetail() {
        AdminLoginData adminLoginData = mDbManager.getAdminLoginDetail();
        userName.setText(adminLoginData.getName());
        userEmail.setText(adminLoginData.getEmail());
    }
}
