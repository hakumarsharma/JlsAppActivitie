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

package com.jio.devicetracker.view.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.EditMemberDetailsData;
import com.jio.devicetracker.database.pojo.request.EditUserDetailsRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;


public class EditUserProfileActivity extends Activity implements View.OnClickListener {

    private TextView userName;
    private EditText userEmail;
    private DBManager mDbManager;
    private EditText userNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Button updateBtn = findViewById(R.id.update_btn);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.EDIT_PROFILE_TITLE);
        title.setTypeface(Util.mTypeface(this, 5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        mDbManager = new DBManager(this);
        userName = findViewById(R.id.update_edit_name);
        userNumber = findViewById(R.id.update_edit_number);
        userEmail = findViewById(R.id.update_edit_email);
        userName.setText(mDbManager.getAdminLoginDetail().getName());
        userNumber.setText(mDbManager.getAdminLoginDetail().getPhoneNumber());
        userEmail.setText(mDbManager.getAdminLoginDetail().getEmailId());
        backBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.update_btn:
                if (userName.getText().toString().isEmpty()) {
                    userName.setError(Constant.NAME_VALIDATION);
                    return;
                }
                if (userEmail.getText().toString().isEmpty() && Util.isValidEmailId(userEmail.getText().toString())) {
                    userEmail.setError(Constant.VALID_EMAIL_ID);
                    return;
                }
                EditMemberDetailsData data = new EditMemberDetailsData();
                data.setName(userName.getText().toString());
                String userId = mDbManager.getAdminLoginDetail().getUserId();
                GroupRequestHandler.getInstance(this).handleRequest(new EditUserDetailsRequest(new EditUserSuccessListener(), new EditUserErrorListener(), data, userId));
                break;
            default:
                // Todo
                break;
        }
    }

    private class EditUserSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            mDbManager.updateAdminLoginTable(userNumber.getText().toString(), userEmail.getText().toString(), userName.getText().toString());
            gotoNavigateUserProfileActivity();
        }
    }

    private void gotoNavigateUserProfileActivity() {
        Intent intent = new Intent(this, NavigateUserProfileActivity.class);
        startActivity(intent);

    }

    private class EditUserErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {

            Toast.makeText(EditUserProfileActivity.this, "User name didn't update ", Toast.LENGTH_SHORT).show();
        }
    }
}
