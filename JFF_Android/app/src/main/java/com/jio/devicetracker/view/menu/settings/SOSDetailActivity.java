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

package com.jio.devicetracker.view.menu.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.SOSContactData;
import com.jio.devicetracker.database.pojo.request.GetAllSOSDetailRequest;
import com.jio.devicetracker.database.pojo.response.GetAllSOSDetailsResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SOSDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private DBManager mDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_detail);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.SOS_DETAILS);
        title.setTypeface(Util.mTypeface(this, 5));
        Button sosEditButton = findViewById(R.id.sosEditButton);
        sosEditButton.setOnClickListener(this);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        TextView sosContact1TextView = findViewById(R.id.sosContact1TextView);
        TextView sosContact2TextView = findViewById(R.id.sosContact2TextView);
        TextView sosContact3TextView = findViewById(R.id.sosContact3TextView);
        sosContact1TextView.setTypeface(Util.mTypeface(this, 3));
        sosContact2TextView.setTypeface(Util.mTypeface(this, 3));
        sosContact3TextView.setTypeface(Util.mTypeface(this, 3));
        mDbManager = new DBManager(this);
        getAllSOSDetails();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sosEditButton) {
            startActivity(new Intent(this, SOSActivity.class));
        } else if(v.getId() == R.id.back) {
            finish();
        }
    }

    // Get all SOS details API calls
    private void getAllSOSDetails() {
        AdminLoginData adminLoginData = mDbManager.getAdminLoginDetail();
        List<GroupMemberDataList> mList = mDbManager.getAllGroupMemberData();
        String devideId = Constant.EMPTY_STRING;
        for (GroupMemberDataList groupMemberDataList : mList) {
            if (adminLoginData.getPhoneNumber().equalsIgnoreCase(groupMemberDataList.getNumber())) {
                devideId = groupMemberDataList.getDeviceId();
                break;
            }
        }
        RequestHandler.getInstance(this).handleRequest(new GetAllSOSDetailRequest(new GetAllSOSDetailSuccessListener(), new GetAllSOSDetailErrorListener(), devideId, mDbManager.getAdminLoginDetail().getUserToken()));
    }

    /**
     * Success Listener of Create SOS request
     */
    public class GetAllSOSDetailSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetAllSOSDetailsResponse getAllSOSDetailsResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetAllSOSDetailsResponse.class);
            List<GetAllSOSDetailsResponse.Data.Desired.Phonebooks> phonebooks = getAllSOSDetailsResponse.getData().getDesired().getmList();
            getAllSOSDetailsResponse.getData().getDesired().getmList();
            List<SOSContactData> mList = new ArrayList<>();
            if (getAllSOSDetailsResponse.getCode() == 200 && !phonebooks.isEmpty()) {
                for (GetAllSOSDetailsResponse.Data.Desired.Phonebooks mPhoneBooks : phonebooks) {
                    SOSContactData sosContactData = new SOSContactData();
                    sosContactData.setPriority(mPhoneBooks.getPriority());
                    sosContactData.setNumber(mPhoneBooks.getNumber());
                    sosContactData.setPhonebookId(mPhoneBooks.getId());
                    mList.add(sosContactData);
                }
            }
            mDbManager.insertIntoSOSTable(mList);
            displayDataInSOSDetailActivity();
        }
    }

    private void displayDataInSOSDetailActivity() {
        TextView contact1Number = findViewById(R.id.contact1Number);
        TextView contact2Number = findViewById(R.id.contact2Number);
        TextView contact3Number = findViewById(R.id.contact3Number);
        contact1Number.setTypeface(Util.mTypeface(this, 5));
        contact2Number.setTypeface(Util.mTypeface(this, 5));
        contact3Number.setTypeface(Util.mTypeface(this, 5));
        List<SOSContactData> mList = mDbManager.getAllSOStableData();
        for(SOSContactData sosContactData : mList) {
            if(sosContactData.getPriority() == 1) {
                contact1Number.setText(sosContactData.getNumber());
            } else if(sosContactData.getPriority() == 2) {
                contact2Number.setText(sosContactData.getNumber());
            } else {
                contact3Number.setText(sosContactData.getNumber());
            }
        }
    }

    /**
     * Error Listener of Create SOS request
     */
    private class GetAllSOSDetailErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Todo
        }
    }

}
